package asia.cmg.f8.notification.push;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import asia.cmg.f8.notification.api.NotificationResponseDTO;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.repository.NotificationRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.event.notification.DeviceRegisteredEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.notification.client.CounterClient;
import asia.cmg.f8.notification.client.DeviceClient;
import asia.cmg.f8.notification.client.LastLoadNotificationTime;
import asia.cmg.f8.notification.client.NotificationClient;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.config.NotifierProperties;
import asia.cmg.f8.notification.dto.DeviceInfo;
import asia.cmg.f8.notification.dto.DeviceRequest;
import asia.cmg.f8.notification.dto.NotificationCounterRequest;
import asia.cmg.f8.notification.dto.NotificationResponse;
import asia.cmg.f8.notification.dto.UnreadCounter;
import asia.cmg.f8.notification.entity.UserGridResponse;

/**
 * Created on 1/5/17.
 */
@Service
public class PushNotificationService {

    private static final String FIREBASE_NOTIFIER_ID = " firebase.notifier.id ='";
	private static final String APS_NOTIFIER_ID = " aps.notifier.id ='";
	private static final String OR_CONDITIONS = " or ";
	private static final String STRING = "'";
    private static final String SELECT_WHERE_DEVICE_ID = "select * where device_id = '";
    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationService.class);
   
    /**
     * This is convention of User-Grid for notifier id.
     */
    private static final String NOTIFIER_ID_POSTFIX = ".notifier.id";
    private static final String IOS_DEVICE = "ios";
    private static final String ANDROID_DEVICE = "android";
    private static final UserGridResponse<Map<String, Object>> DEFAULT_RESPONSE = new UserGridResponse<>();
    private static final String DATA = "data";
    private static final String ALERT = "alert";
    private static final String TYPE = "type";

    private final NotificationClient notificationClient;
    private final CounterClient counterClient;
    private final NotifierProperties notifierProperties;
    private final NotificationEventPublisher notificationEventPublisher;
    private final DeviceClient deviceClient;
    private final NotificationProperties notificationProperties;
    private final NotificationRepository notificationRepository;

    public PushNotificationService(
            final NotificationClient notificationClient, final CounterClient counterClient,
            final NotifierProperties notifierProperties, final NotificationEventPublisher notificationEventPublisher,
            final DeviceClient deviceClient, final NotificationProperties notificationProperties,
            NotificationRepository notificationRepository) {
        this.notificationClient = notificationClient;
        this.counterClient = counterClient;
        this.notifierProperties = notifierProperties;
        this.notificationEventPublisher = notificationEventPublisher;
        this.deviceClient = deviceClient;
        this.notificationProperties = notificationProperties;
        this.notificationRepository = notificationRepository;
    }


    public Optional<DeviceInfo> registerDevice(final DeviceRequest deviceRequest, final Account account) {

        final String type = deviceRequest.getType();
        final String token = deviceRequest.getToken();
        
        final UserGridResponse<Map<String, Object>> deviceInfoDuplicate = deviceClient.searchDevices(SELECT_WHERE_DEVICE_ID+deviceRequest.getId()+STRING+OR_CONDITIONS+APS_NOTIFIER_ID+token+STRING+OR_CONDITIONS+FIREBASE_NOTIFIER_ID+token+STRING);
         
         if(deviceInfoDuplicate != null && deviceInfoDuplicate.getEntities() != null){
        	
        	 LOGGER.info("----  delete size --- {} =",deviceInfoDuplicate.getEntities().size());
        	
         }
        
         final Optional<String> key = buildKey(type, notifierProperties);
        if (!key.isPresent()) {
            LOGGER.warn("Not found notifier is configured with device type " + type);
            return Optional.empty();
        }
        final String notifierName = key.get();

        // create device info.
        final DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceRequest.getId());
        deviceInfo.setNotifier(notifierName);
        deviceInfo.setPhoneType(type);
        deviceInfo.setUserUuid(account.uuid());
        deviceInfo.setCreated(System.currentTimeMillis());
        deviceInfo.setModified(System.currentTimeMillis());

        // convert to map to add token
        final Map<String, Object> map = deviceInfo.toMap();
        map.put(notifierName + NOTIFIER_ID_POSTFIX, token);

        
        
        // register device.
        final UserGridResponse<DeviceInfo> response = notificationClient.registerDevice(map, deviceInfo.getDeviceId());
        //final UserGridResponse<DeviceInfo> response = null; 
        if (response != null && response.getEntities() != null && !response.getEntities().isEmpty()) {

            final DeviceInfo device = response.getEntities().iterator().next();

            final DeviceRegisteredEvent event = DeviceRegisteredEvent.newBuilder()
                    .setSubmittedAt(System.currentTimeMillis())
                    .setDeviceId(device.getUuid())
                    .setUserId(account.uuid())
                    .setEventId(UUID.randomUUID().toString()).build();

            // TODO what if it's failed
            notificationEventPublisher.publish(event);

            return Optional.of(device);
        }
        return Optional.empty();
    }

    private Optional<String> buildKey(final String type, final NotifierProperties properties) {
        if (IOS_DEVICE.equals(type)) {
            return Optional.ofNullable(properties.getiOsNotifier());
        }
        if (ANDROID_DEVICE.equals(type)) {
            return Optional.ofNullable(properties.getAndroidNotifier());
        }
        return Optional.empty();
    }

    public Page<NotificationEntity> loadLatestSentNotifications(final String uuid, int page, int perPage) {
        Pageable pageable = new PageRequest(page, perPage);
        return notificationRepository.getLatestByReceiver(uuid, pageable);
    }

    /**
     * Load latest notifications of given user and device type.
     *
     * @param uuid  the user uuid
     * @param token the access token
     * @return list of sent notifications. It never return null.
     */
    public List<Object> loadLatestSentNotifications(final String uuid, final String token, final UserType userType) {
    	final LastLoadNotificationTime lastLoadTime = getLastLoadNotificationTime(uuid, token);
        
    	/**
         * If user does not exist or is deactivated, we just return empty list.
         */
        if (lastLoadTime == null || !lastLoadTime.isActivated()) {
            return Collections.emptyList();
        }

        final String query = composeQuery(uuid, lastLoadTime, userType);

        LOGGER.info("Load notification by query {}", query);

        final UserGridResponse<NotificationResponse> notifications = notificationClient.getLatest(query, 100);
        if (notifications == null || notifications.getEntities() == null) {
            return Collections.emptyList();
        }
        return notifications.getEntities().stream()
                .filter(notification -> notification.getPayloads() != null && !notification.getPayloads().isEmpty())
                .map(notification -> {
                    final Object value = notification.getPayloads().entrySet().iterator().next().getValue();
                    if (value instanceof Map) {
                        final Map<String, Object> payload = (Map<String, Object>) value;
                        payload.put("created_date", notification.getCreated());
                        return payload;
                    }
                    return null;
                })
                .filter(payload -> {

                    final Map<String, Object> data = getDataFromPayload(payload);
                    if (data != null && data.get(TYPE) !=null) {
                        return (!Objects.isNull(payload)) && (!data.get(TYPE).equals(NotificationSender.CHAT_TYPE));
                    }
                    return !Objects.isNull(payload);
                })
                .map(payload -> {

                    final Map<String, Object> data = getDataFromPayload(payload);
                    if (data != null) {
                        data.put("created_date", payload.get("created_date"));
                        return data;
                    }
                    return null;
                })
                .filter(data -> !Objects.isNull(data))
                .collect(Collectors.toList());
    }

	private Map<String, Object> getDataFromPayload(final Map<String, Object> payload) {
		String key = NotificationSender.ANDROID_NOTIFICATION_PROP;
		if (payload.containsKey(NotificationSender.IOS_NOTIFICATION_PROP)) {
			key = NotificationSender.IOS_NOTIFICATION_PROP;
		}

		final Map<String, Object> message = (Map<String, Object>) payload.get(key);

		if (message != null) {
			final Map<String, Object> data = (Map<String, Object>) message.get(DATA);
			if (data != null) {
				return data;
			}
			return (Map<String, Object>) message.get(ALERT);
		}
		return message;
	}

    @SuppressWarnings("PMD")
    private String composeQuery(final String uuid, final LastLoadNotificationTime lastLoadTime, final UserType userType) {
		final StringBuilder query = new StringBuilder("SELECT * WHERE (receiver='" + uuid + "' OR receiver='" + notificationProperties.getDefaultGroupId());
		final String groupTypeId = userType.equals(UserType.EU) ? notificationProperties.getEuGroupId() : notificationProperties.getPtGroupId();
		query.append("' OR receiver='" + groupTypeId + "') ");

        final Long time = lastLoadTime.getLastTime();
        if (time != null) {
            query.append(" AND created > ").append(time);
        }
        query.append(" ORDER BY created desc");
        return query.toString();
    }

    private LastLoadNotificationTime getLastLoadNotificationTime(final String uuid, final String token) {
        final UserGridResponse<LastLoadNotificationTime> response = notificationClient.getLastNotificationLoadedTime(uuid, LastLoadNotificationTime.FIND_QUERY);
        if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
            return null;
        }
        return response.getEntities().iterator().next();
    }

    public void clearNotificationHistory(final String uuid, final String accessToken) {
        final UserGridResponse<Map<String, Object>> response = notificationClient.updateLastNotificationLoadedTime(uuid, Collections.singletonMap(LastLoadNotificationTime.LAST_LOAD_NOTIFICATION_TIME, System.currentTimeMillis()));
        if (response != null) {
            LOGGER.info("Clear notification history of user {} successful", uuid);
        } else {
            LOGGER.warn("Failed to clear notification history of user {}", uuid);
        }
    }

    public NotificationEntity markAsRead(final Account account, final String uuid) {
        NotificationEntity entity = notificationRepository.findByUuid(uuid);
        entity.setRead(true);
        notificationRepository.save(entity);
        return entity;
    }

    public int getUnreadCounter(final Account account) {
        final String counterName = buildNotificationCounter(account);
        final UnreadCounter counter = counterClient.getUnReadCounter(counterName);
        if (counter == null) {
            return 0;
        }

        int count = counter.getCount();
        if (count < 0) {
            count = 0;
        }
        return count;
    }

    private String buildNotificationCounter(final Account account) {
        return NotificationCounterRequest.buildCounterName(account.uuid());
    }

    public void resetUnReadCounter(final Account account) {
        counterClient.resetCounter(buildNotificationCounter(account));
    }

    public UserGridResponse<Map<String, Object>> unregisterDevice(final DeviceRequest deviceRequest, final Account account) {
        return deviceClient.unConnectDevice(account.uuid(), deviceRequest.getId());
    }
    
    public UserGridResponse<Map<String, Object>> unregisterDevice(final DeviceRequest deviceRequest, final String uuid) {
        return deviceClient.unConnectDevice(uuid, deviceRequest.getId());
    }
    
    public int countByReceiverAndOffset(String userUuid, long offset) {
    	return notificationRepository.countByReceiverAndOffset(userUuid, offset);
    }
    
    public long getLastIdByReceiver(String userUuid) {
    	Optional<BigInteger> optional = notificationRepository.findMaxIdByReceiver(userUuid);
    	return optional.get() == null ? (long) 0 : optional.get().longValue();
    }
    
    public void clearAllByReceiver(String receiverUuid) {
    	notificationRepository.deleteByReceiver(receiverUuid);
    }
}
