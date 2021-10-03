package asia.cmg.f8.notification.push;

import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.client.CounterClient;
import asia.cmg.f8.notification.client.NotificationClient;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.config.NotifierProperties;
import asia.cmg.f8.notification.database.entity.DeviceEntity;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.ChatMessageType;
import asia.cmg.f8.notification.dto.NotificationCounterRequest;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.service.DeviceService;
import asia.cmg.f8.notification.service.OneSignalNotificationService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Async;

import com.currencyfair.onesignal.OneSignal;
import com.currencyfair.onesignal.model.notification.CreateNotificationResponse;
import com.currencyfair.onesignal.model.notification.NotificationRequest;
import com.google.gson.Gson;

import java.util.*;
//import asia.cmg.f8.user.service.UserEntity;
import java.util.stream.Collectors;

/**
 * Created on 1/7/17.
 */
public abstract class NotificationSender implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSender.class);
	private static final Gson GSON = new Gson();
	protected static final String CHAT_TYPE = "chat";
	public static final String ANDROID_NOTIFICATION_PROP = "notification";
	public static final String IOS_NOTIFICATION_PROP = "aps";

	@Autowired
	private NotificationClient notificationClient;

	@Autowired
	private CounterClient counterClient;

	@Autowired
	private NotifierProperties notifierProperties;
	
	@Autowired
	private NotificationProperties notificationProperties;

	@Autowired
	private BasicUserInfoClient basicUserInfoClient;

	private MessageSource messageSource;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private UserClient userClient;
	
	@Autowired
	private OneSignalNotificationService notificationService;
	
	@Autowired
	private DeviceService deviceService;

	private String iosNotifier;
	private String androidNotifier;
	private static OneSignal oneSignalProvider = OneSignal.getInstance();
	
	@Async
	protected final void sendToUser(final String userUuid, final PushMessage message, List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar, String eventName) {

		final Locale locale = userInfoService.getLocale(userUuid);

		// Ticket-1692: Recorded notification entity to database and send via OneSignal
		sendToUser(userUuid, message, tagged_accounts, avatar, eventName, locale.getLanguage());
		// End ticket-1692
	}
	
	@Async
	protected final void sendToUser(final String userUuid, final PushMessage message, 
			List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar, String eventName, String language) {
		
		Locale locale = null;
		if(language == null) {
			locale = userInfoService.getLocale(userUuid);
		} else {
			locale = new Locale(language);
		}

		// Ticket-1692: Recorded notification entity to database and send via OneSignal
		try {
			// Store notification into DB
			NotificationEntity notificationEntity = createNotificationEntity(message, userUuid, locale, tagged_accounts, avatar, eventName);
			notificationService.save(notificationEntity);
			LOGGER.info("Saved notification entity info: {}", GSON.toJson(notificationEntity));

			LOGGER.info("sendNotification2User to user {}", userUuid);
			NotificationRequest notificationRequest = buildOneSignalNotificationRequest(notificationEntity, locale);
			if(notificationRequest == null) return;

			CreateNotificationResponse notificationResponse = oneSignalProvider.createNotification(
					notificationProperties.getOneSignal().getApiKey(), notificationRequest);
			Object errors = notificationResponse.getErrors();
			if(errors != null) {
				LOGGER.error("Push notification failed => " + errors.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Recorded notification entity to database failed: {}", e.getMessage());
		}
		// End ticket-1692
	}

	@Async
	protected final void sendToSegments(List<String> segments, final String message, Map<String, String> data, String language) {
		try {
			NotificationRequest OSnotification = new NotificationRequest();
			Map<String, String> contents = new HashMap<String, String>();
			contents.put(language, message);
			
			OSnotification.setAppId(notificationProperties.getOneSignal().getAppId());
			OSnotification.setContents(contents);
			OSnotification.setIncludedSegments(segments);
			OSnotification.setData(data);
			
			CreateNotificationResponse notificationResponse = oneSignalProvider.createNotification(
					notificationProperties.getOneSignal().getApiKey(), OSnotification);
			Object errors = notificationResponse.getErrors();
			if(errors != null) {
				LOGGER.error("Push notification to segment failed => " + errors.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Push notification to segment failed => " + e.getMessage());
		}
	}
	
	@Async
	protected final void sendToGroup(final String groupUuid, final PushMessage message, List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar) {
	
		final Locale locale = userInfoService.getLocale(groupUuid);

		final UserGridResponse<Map<String, Object>> response = notificationClient.sendNotification2UserGroup(
				Collections.singletonList(createNotification(message, groupUuid, locale, tagged_accounts, avatar)), groupUuid);
		if (response != null) {
			LOGGER.info("Pushed message \"{}\" to user {}", message.getLocalizedMessage(messageSource, locale),
					groupUuid);
			if (message.getType() == CHAT_TYPE) {
				return;
			}
			final UserGridResponse<Map<String, Object>> result = counterClient
					.increaseCounter(NotificationCounterRequest.buildCounterName(groupUuid));
			if (result != null) {
				LOGGER.info("Success increase unread counter of {} to 1", groupUuid);
			}
		} else {
			LOGGER.warn("Failed to push notification to {}", groupUuid);
		}
	}

	private NotificationRequest buildOneSignalNotificationRequest(NotificationEntity notificationEntity, Locale locale) {
		List<DeviceEntity> deviceEntities = deviceService.getByActivatedTrueAndUserUuid(notificationEntity.getReceiver());
		if(deviceEntities.isEmpty()) return null;

		NotificationRequest OSnotification = new NotificationRequest();
		Map<String, String> contents = new HashMap<String, String>();
		List<String> players = new ArrayList<String>();
		
		contents.put("en", notificationEntity.getContent());
		players = deviceEntities.stream().map(DeviceEntity::getDeviceId).collect(Collectors.toList());
		
		OSnotification.setAppId(notificationProperties.getOneSignal().getAppId());
		OSnotification.setContents(contents);
		OSnotification.setIncludeExternalUserIds(players);
		if(notificationEntity.getCustomData() != null) {
			OSnotification.setData(notificationEntity.getCustomDataAsMap());
		}
		
		return OSnotification;
	}
	
	private Map<String, Object> createNotification(final PushMessage message, final String receiver, final Locale locale, 
													List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar) {

		final Map<String, Object> notification = new HashMap<>();
		final String notificationId = UUID.randomUUID().toString();
		notification.put("receiver", receiver);
		notification.put("uuid", notificationId);
		notification.put("display", Boolean.TRUE);
		
		final Map<String, Object> payloads = new HashMap<>(2);
		payloads.put(iosNotifier, toIosMessage(message, notificationId, locale, tagged_accounts, avatar));
		payloads.put(androidNotifier, toAndroidMessage(message, notificationId, locale, tagged_accounts, avatar));

		notification.put("payloads", payloads);

		return notification;
	}
	
	private NotificationEntity createNotificationEntity(final PushMessage message, final String receiver, final Locale locale, 
														List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar, String eventName) {
		NotificationEntity notification = new NotificationEntity();
		final String notificationId = UUID.randomUUID().toString();

		notification.setDisplay(Boolean.TRUE);
		notification.setRead(Boolean.FALSE);
		notification.setReceiver(receiver);
		notification.setSent(Boolean.TRUE);
		notification.setSentDate(new Date());
		notification.setUuid(notificationId);
		notification.setContent(message.getLocalizedMessage(messageSource, locale));
		notification.setEventName(eventName);
		if(avatar != null) notification.setAvatar(avatar.getAvatar());
		if(message.getCustomData() != null) {
			JsonObject customData = new JsonObject();
			message.getCustomData().forEach((key, value) -> {
				customData.addProperty(key, value.toString());
			});
			notification.setCustomData(new Gson().toJson(customData));
		}

		if(tagged_accounts != null) {
			JsonArray taggedAccounts = new JsonArray();
			tagged_accounts.forEach(entity -> {
				JsonObject object = new JsonObject();
				object.addProperty("name", entity.getName());
				object.addProperty("avatar", entity.getAvatar());
				object.addProperty("uuid", entity.getUuid());
				taggedAccounts.add(object);
			});
			notification.setTaggedAccounts(new Gson().toJson(taggedAccounts));
		}

		return notification;
	}

	/**
	 * Convert a {@link PushMessage} for Android
	 *
	 * @param message
	 *            the message
	 * @param notificationId
	 *            the notification uuid
	 * @param locale
	 *            the locale
	 * @return Android message as map
	 */
	private Map<String, Object> toAndroidMessage(final PushMessage message, final String notificationId,
												 final Locale locale, List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar) {
		final Map<String, Object> content = new HashMap<>();
		content.put("title", message.getDefaultTitle());
		content.put("body", message.getLocalizedMessage(messageSource, locale));
		content.put("sound", "default");
		content.put("badge", 1);
		content.put("data", convert(message, notificationId, locale, tagged_accounts, avatar));
		return Collections.singletonMap(ANDROID_NOTIFICATION_PROP, content);
	}

	private Map<String, Object> convert(final PushMessage message, final String notificationId, final Locale locale, 
										List<SocialUserInfo> tagged_accounts, SocialUserInfo userAvatar) {
		final Map<String, Object> data = new HashMap<>();
		data.put("uuid", notificationId);
		data.put("type", message.getType());
		data.put("message", message.getLocalizedMessage(messageSource, locale));
		data.put("title", message.getDefaultTitle());
		data.put("body", message.getDefaultMessage());
		data.put("custom_data", message.getCustomData() == null ? Collections.emptyList() : message.getCustomData());
		data.put("tagged_accounts", tagged_accounts == null ? Collections.emptyList() : tagged_accounts);
		if(userAvatar != null) {
			data.put("avatar", userAvatar.getAvatar());
		}
		
		return data;
	}

	/**
	 * Convert a {@link PushMessage} for iOS
	 *
	 * @param message
	 *            the message
	 * @param notificationId
	 *            the notification uuid
	 * @param locale
	 *            the locale
	 * @return iOS message as map.
	 */
	private Map<String, Object> toIosMessage(final PushMessage message, final String notificationId,
											 final Locale locale, List<SocialUserInfo> tagged_accounts, SocialUserInfo avatar) {
		final Map<String, Object> content = new HashMap<>(3);
		content.put("sound", "default");
		content.put("badge", 1);
		if(StringUtils.isEmpty(message.getDefaultTitle())){
			content.put("alert", message.getLocalizedMessage(messageSource, locale));
			content.put("data", convert(message, notificationId, locale, tagged_accounts, avatar));		
		}
		else {
			content.put("alert", convert(message, notificationId, locale, tagged_accounts, avatar));	
		}
		return Collections.singletonMap(IOS_NOTIFICATION_PROP, content);
	}

	@Override
	@SuppressWarnings("PMD")
	public void afterPropertiesSet() throws Exception {
		final String androidNotifier = notifierProperties.getAndroidNotifier();
		if (androidNotifier == null) {
			throw new IllegalStateException("Missing configuration for android notifier");
		}

		final String iOsNotifier = notifierProperties.getiOsNotifier();
		if (iOsNotifier == null) {
			throw new IllegalStateException("Missing configuration for iOs notifier");
		}

		this.androidNotifier = androidNotifier;
		this.iosNotifier = iOsNotifier;

		// initialize message source
		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("i18n/push-messages");
		messageSource.setDefaultEncoding("utf-8");
		this.messageSource = messageSource;
	}

	protected Optional<BasicUserInfo> find(final CharSequence uuid) {
		if (uuid != null) {
			final UserGridResponse<BasicUserInfo> response = basicUserInfoClient
					.findBasicUserInfo(String.valueOf(uuid));
			if (response != null && response.getEntities() != null && !response.getEntities().isEmpty()) {
				return Optional.ofNullable(response.getEntities().iterator().next());
			}
			LOGGER.warn("Not found user with uuid := {}, or he/she is deactivated.", uuid);
		}
		return Optional.empty();
	}

	public void updateLastMsg(final String userUuid,
							  final String senderUuid,
							  final PushMessage message,
							  ChatMessageType chatMessageType,
							  final long chatMessageTime,
							  final String chatMessageId) {

		try {
			final Locale locale = userInfoService.getLocale(userUuid);
			final String lastMsg = message.getLocalizedMessage(messageSource, locale);
			final UserGridResponse<UserEntity> userResp = userClient.getUserByUuid(userUuid);
			final UserGridResponse<UserEntity> userSender = userClient.getUserByUuid(senderUuid);

			final List<UserEntity> entitiesuser = userResp.getEntities();
			final List<UserEntity> entitiessender = userSender.getEntities();

			String lastMessageContent = lastMsg.substring(lastMsg.indexOf(':') + 1).trim();			
			
			for (UserEntity entity : entitiesuser) {
				entity.setLastMsg(lastMessageContent);
				entity.setLastMsgOwner(senderUuid);
				entity.setLastMsgReceiver(userUuid);
				entity.setLastMsgType(chatMessageType.toString());
				entity.setLastMsgTime(chatMessageTime);
				entity.setLastMsgId(chatMessageId);
				LOGGER.info("updateLastMsg for user {}", userUuid );
				userClient.updateLastMsg(userUuid, entity);
			}

			for (UserEntity entity : entitiessender) {
				entity.setLastMsg(lastMessageContent);
				entity.setLastMsgOwner(senderUuid);
				entity.setLastMsgReceiver(userUuid);
				entity.setLastMsgType(chatMessageType.toString());
                entity.setLastMsgTime(chatMessageTime);
				entity.setLastMsgId(chatMessageId);
				LOGGER.info("updateLastMsg for sender {}", senderUuid );
				userClient.updateLastMsg(senderUuid, entity);
			}

		} catch (Exception ex) {
			LOGGER.error("error updateLastMsg", ex);
		}

	}

}
