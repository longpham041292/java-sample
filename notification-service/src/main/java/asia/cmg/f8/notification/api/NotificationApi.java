package asia.cmg.f8.notification.api;

import static asia.cmg.f8.common.web.errorcode.ErrorCode.INTERNAL_SERVICE_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.Collections;
import java.util.Map;

import javax.transaction.Transactional;

import asia.cmg.f8.notification.database.entity.NotificationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.database.entity.DeviceEntity;
import asia.cmg.f8.notification.dto.DeviceRequest;
import asia.cmg.f8.notification.dto.NotificationRequest;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.enumeration.EPhoneType;
import asia.cmg.f8.notification.push.PushNotificationService;
import asia.cmg.f8.notification.push.SocialNotifier;
import asia.cmg.f8.notification.service.DeviceService;

/**
 * Created on 1/5/17.
 */
@RestController
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class NotificationApi {

	public static final Logger LOGGER = LoggerFactory.getLogger(NotificationApi.class);

	private final PushNotificationService notificationService;

	private final SocialNotifier socialNotifier;
	
	@Autowired
	private DeviceService deviceService;

	public NotificationApi(final PushNotificationService notificationService, SocialNotifier socialNotifier) {
		this.notificationService = notificationService;
		this.socialNotifier = socialNotifier;
	}

	/**
	 * Checked with FE, do not used
	 * @author thach vo
	 * @date: 20/01/2020
	 */
//	@PutMapping(value = "/notifications/devices", consumes = APPLICATION_JSON_VALUE)
//	public ResponseEntity unregisterDevice(@RequestBody final DeviceRequest deviceRequest, final Account account) {
//		final UserGridResponse<Map<String, Object>> deviceInfo = notificationService.unregisterDevice(deviceRequest,
//				account);
//		if (deviceInfo != null && deviceInfo.getEntities() != null && !deviceInfo.getEntities().isEmpty()) {
//			LOGGER.info("Un-registered device {} with user {}", deviceRequest.getId(), account.uuid());
//			
//			// Ticket-1692: Unregister device from DB table "device"
//			boolean result = deviceService.unregisterDevice(account.uuid(), deviceRequest.getId());
//			if(result == true) {
//				LOGGER.info("Un-registered device {} with user {} from DB successfully", deviceRequest.getId(), account.uuid());
//			}
//			// End ticket-1692
//			
//			return new ResponseEntity<>(deviceInfo.getEntities().iterator().next(), HttpStatus.OK);
//		}
//		return new ResponseEntity<>(INTERNAL_SERVICE_ERROR.withDetail("Failed to unregister device"),
//				HttpStatus.BAD_REQUEST);
//	}

	/**
	 * 
	 * @param deviceRequest
	 * @param uuid - The uuid of user
	 * @return
	 */
	@PutMapping(value = "/notifications/devices/{uuid}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity unregisterDevice(@RequestBody final DeviceRequest deviceRequest,
			@PathVariable(value = "uuid") final String uuid) {
		final UserGridResponse<Map<String, Object>> deviceInfo = notificationService.unregisterDevice(deviceRequest,
				uuid);
		if (deviceInfo != null && deviceInfo.getEntities() != null && !deviceInfo.getEntities().isEmpty()) {
			LOGGER.info("Un-registered device {} with user {}", deviceRequest.getId(), uuid);
			
			// Ticket-1692: Unregister device from DB table "device"
			try {
				DeviceEntity deviceEntity = deviceService.getDeviceByDeviceIdAndUserUuid(deviceRequest.getId(), uuid);
				if(deviceEntity != null) {
					deviceService.deactivateRegisteredDevice(deviceEntity);
				} else {
					LOGGER.info("Device id {} of user {} not found", deviceRequest.getId(), uuid);
				}
			} catch (Exception e) {
				LOGGER.info("Unregister device to database failed: {}", e.getMessage());
			}
			// End ticket-1692
			
			return new ResponseEntity<>(deviceInfo.getEntities().iterator().next(), HttpStatus.OK);
		}
		return new ResponseEntity<>(INTERNAL_SERVICE_ERROR.withDetail("Failed to unregister device"),
				HttpStatus.BAD_REQUEST);
	}
	@Transactional
	@PostMapping(value = "/notifications/devices", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerDevice(@RequestBody final DeviceRequest deviceRequest, final Account account) {
		LOGGER.info("Logged device request info: {}", deviceRequest.toString());
		
//		final Optional<DeviceInfo> response = notificationService.registerDevice(deviceRequest, account);
		LOGGER.info("Registered device {} with user {}", deviceRequest.getId(), account.uuid());
		
		// Ticket-1692: Register device to DB table "device"
		EPhoneType phoneType = deviceService.checkDeviceType(deviceRequest.getType());
		if(phoneType == null) {
			LOGGER.info("Device type {} is not valid.", deviceRequest.getType());
			return new ResponseEntity<>(INTERNAL_SERVICE_ERROR.withDetail("Failed to register device"),
					HttpStatus.BAD_REQUEST);
		}
		
		try {
			DeviceEntity oldEntity = deviceService.getDeviceByDeviceIdAndUserUuid(deviceRequest.getId(), account.uuid());
			if(oldEntity == null) {	// First time of registering
				oldEntity = deviceService.convert(deviceRequest, phoneType, account.uuid());
				deviceService.registerDevice(oldEntity);
			} else {	// Update activate this one
				oldEntity.setNotifierId(deviceRequest.getToken());
				deviceService.activateRegisteredDevice(oldEntity);
			}
			deviceService.setDeactivateAllDeviceByUuidExceptOne(account.uuid(), oldEntity.getId());
		} catch (Exception e) {
			LOGGER.info("Register device to database failed: {}", e.getMessage());
			return new ResponseEntity<>(INTERNAL_SERVICE_ERROR.withDetail("Failed to register device"),
					HttpStatus.BAD_REQUEST);
		}
			// End of ticket-1692
		return new ResponseEntity<>(Collections.singletonMap("status", Boolean.TRUE), HttpStatus.OK);
	}

	@GetMapping(value = "/notifications")
	public ResponseEntity loadNotifications(final Account account) {
        
		final String accountType = account.type();
		Assert.notNull(accountType, "Account type must not be null");

		Page<NotificationEntity> pageData =
				notificationService.loadLatestSentNotifications(account.uuid(), 0, 100);
		return new ResponseEntity<>(
				pageData.map(entity -> new NotificationResponseDTO(entity)).getContent(),
				HttpStatus.OK);
	}

	@DeleteMapping(value = "/notifications", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity clearNotifications(final Account account) {
		notificationService.clearNotificationHistory(account.uuid(), account.ugAccessToken());
		return new ResponseEntity<>(Collections.singletonMap("success", Boolean.TRUE), HttpStatus.OK);
	}

	@PutMapping(value = "/notifications/{uuid}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity markAsRead(final Account account, @PathVariable("uuid") final String uuid) {
		NotificationEntity notification = notificationService.markAsRead(account, uuid);
		if (notification == null) {
			return new ResponseEntity<>(ErrorCode.INTERNAL_SERVICE_ERROR, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(notification, HttpStatus.OK);
	}

	@GetMapping(value = "/notifications/counter/unread")
	public ResponseEntity getUnReadCounter(final Account account) {
		return new ResponseEntity<>(Collections.singletonMap("count", notificationService.getUnreadCounter(account)),
				HttpStatus.OK);
	}

	@DeleteMapping(value = "/notifications/counter/unread")
	public ResponseEntity resetCounter(final Account account) {
		notificationService.resetUnReadCounter(account);
		return new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK);
	}

	@PostMapping(value = "/notifications/admin/send")
	@RequiredAdminRole
	public ResponseEntity<Map<String, Boolean>> sendNotifications(final Account account,
			@RequestBody final NotificationRequest request) {
		socialNotifier.sendNotifications(request);
		return new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK);
	}
}
