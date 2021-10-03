//package asia.cmg.f8.notification.api;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.currencyfair.onesignal.OneSignal;
//import com.currencyfair.onesignal.model.notification.CreateNotificationResponse;
//import com.currencyfair.onesignal.model.notification.NotificationRequest;
//import com.google.gson.Gson;
//import asia.cmg.f8.common.dto.ApiRespObject;
//import asia.cmg.f8.common.security.Account;
//import asia.cmg.f8.common.web.errorcode.ErrorCode;
//import asia.cmg.f8.notification.config.NotificationProperties;
//import asia.cmg.f8.notification.database.entity.DeviceEntity;
//import asia.cmg.f8.notification.database.entity.NotificationEntity;
//import asia.cmg.f8.notification.dto.SocialUserInfo;
//import asia.cmg.f8.notification.entity.PagedResponse;
//import asia.cmg.f8.notification.enumeration.ENotificationEventName;
//import asia.cmg.f8.notification.push.NotificationSender;
//import asia.cmg.f8.notification.push.PushMessage;
//import asia.cmg.f8.notification.service.DeviceService;
//import asia.cmg.f8.notification.service.OneSignalNotificationService;
//
//@RestController
//public class TestOneSignalNotificationApi extends NotificationSender {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(TestOneSignalNotificationApi.class);
//	private static final Gson GSON = new Gson();
//	private static final OneSignal oneSignalProvider = OneSignal.getInstance();
//	
//	@Autowired
//	DeviceService deviceService;
//	
//	@Autowired
//	OneSignalNotificationService notificationService;
//	
//	@Autowired
//	private NotificationProperties notificationProperties;
//	
//	@GetMapping(value = "/mobile/v1/notification/test_store", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object> testStoreNotification(final Account account) {
//		ApiRespObject<NotificationEntity> apiResponse = new ApiRespObject<NotificationEntity>();
//		apiResponse.setStatus(ErrorCode.SUCCESS);
//		
//		try {
//			String MESSAGE_DOCUMENT_APPROVAL_MSG = "message.document.approval";
//			String receiverUuid = "6845fb2c-2bab-11ea-a0d3-0a580a820367";
//			String DOCUMENT_TYPE = "document";
//			List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
//			SocialUserInfo NO_AVATAR = null;
//			
//			final PushMessage pushMessage = new PushMessage(DOCUMENT_TYPE);
//	        pushMessage.setLocalizedMessage(MESSAGE_DOCUMENT_APPROVAL_MSG);
//	        
//			sendToUser(receiverUuid, pushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.LIKE_POST.name());
//			
//		} catch (Exception e) {
//			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
//			LOGGER.error("[testOneSignalNotification] sent notification via OneSignal failed with detail: {}", e.getMessage());
//		}
//		
//		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
//	}
//	
//	@GetMapping(value = "/mobile/v1/notification/test_send", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object> testOneSignalNotification(final Account account) {
//		ApiRespObject<NotificationEntity> apiResponse = new ApiRespObject<NotificationEntity>();
//		apiResponse.setStatus(ErrorCode.SUCCESS);
//		
//		try {
//			String receiverUuid = "6845fb2c-2bab-11ea-a0d3-0a580a820367";
//			PageRequest paging = new PageRequest(0, 10);
//			PagedResponse<NotificationEntity> pagedResponse = notificationService.getLatestNotifications(receiverUuid, paging);
//			List<NotificationEntity> notifications = pagedResponse.getEntities();
//			
//			notifications.forEach(notification -> {
//				NotificationRequest notifRequest = this.buildOneSignalNotificationRequest(notification);
//				CreateNotificationResponse notifResponse = oneSignalProvider.createNotification(notificationProperties.getOneSignal().getApiKey(), notifRequest);
//				
//				LOGGER.info("Sent notification via OneSignal: {}", GSON.toJson(notifResponse));	
//			});
//			
//		} catch (Exception e) {
//			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
//			LOGGER.error("[testOneSignalNotification] sent notification via OneSignal failed with detail: {}", e.getMessage());
//		}
//		
//		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
//	}
//	
//	private NotificationRequest buildOneSignalNotificationRequest(NotificationEntity notificationEntity) {
//		NotificationRequest OSnotification = new NotificationRequest();
//		Map<String, String> contents = new HashMap<String, String>();
//		List<String> players = new ArrayList<String>();
//		
//		contents.put("en", notificationEntity.getContent());
//		List<DeviceEntity> deviceEntities = deviceService.getDevicesByUserUuid(notificationEntity.getReceiver());
//		players = deviceEntities.stream().map(DeviceEntity::getDeviceId).collect(Collectors.toList());
//		
//		OSnotification.setAppId(notificationProperties.getOneSignal().getAppId());
//		OSnotification.setContents(contents);
//		OSnotification.setIncludeExternalUserIds(players);		
//		
//		return OSnotification;
//	}
//}
