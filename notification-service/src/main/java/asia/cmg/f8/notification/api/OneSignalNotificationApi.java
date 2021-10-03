package asia.cmg.f8.notification.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.service.OneSignalNotificationService;

@RestController
public class OneSignalNotificationApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(OneSignalNotificationApi.class);
	private GsonJsonParser gson = new GsonJsonParser();
	
	@Autowired
	private OneSignalNotificationService notificationService;
	
	@PostMapping(value = "/mobile/v1/notification", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createNotification(@RequestBody NotificationDTO body, Account account) {
		ApiRespObject<NotificationEntity> apiResponse = new ApiRespObject<NotificationEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			NotificationEntity entity = new NotificationEntity();
			entity.setApsPayload(body.apsPayload);
			entity.setFirebasePayload(body.firebasePayload);
			entity.setReceiver(body.receiver);
			
			entity = notificationService.save(entity);
			apiResponse.setData(entity);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOGGER.error("[createNotification] failed with detail: {}", e.getMessage());
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/mobile/v1/notifications/receiver/{receiver_uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getLatestNotificationsByUser(@PathVariable("receiver_uuid")final String receiverUuid, final Account account,
															   @PageableDefault final Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			PagedResponse<NotificationEntity> pagedResp = notificationService.getLatestNotifications(receiverUuid, pageable);
			
			if(pagedResp != null) {
				List<NotificationEntity> notifications = pagedResp.getEntities();
				
				List<Object> objects = notifications.stream()
				.filter(notification -> notification.getApsPayload() != null && !notification.getApsPayload().isEmpty() && notification.getFirebasePayload() != null && !notification.getFirebasePayload().isEmpty())
				.map(notification -> {
					String apsPayload = notification.getApsPayload();
					Map<String, Object> mapPayload = gson.parseMap(apsPayload);
					Map<String , Object> data = (Map<String, Object>)mapPayload.get("data");
					if(data != null && data.get("type") != null && !data.get("type").equals("chat")) {
						data.put("created_date", notification.getCreatedDate());
						return data;
					}
					return null;
				})
				.collect(Collectors.toList());
				
				apiResponse.setData(objects);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOGGER.error("[getLatestNotificationsByUser] failed with detail: {}", e.getMessage());
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
