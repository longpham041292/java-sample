package asia.cmg.f8.notification.api.v2;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.api.NotificationResponseDTO;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.push.PushNotificationService;
import asia.cmg.f8.notification.service.UserNotificationCounterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import java.util.Collections;

@RestController("NotificationApiV2")
@RequestMapping(value = "/mobile/v2", produces = APPLICATION_JSON_UTF8_VALUE)
public class NotificationApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationApi.class);

    private final PushNotificationService notificationService;
    private final UserNotificationCounterService userNotificationCounterService;

    public NotificationApi(PushNotificationService notificationService, 
    		UserNotificationCounterService userNotificationCounterService) {
        this.notificationService = notificationService;
        this.userNotificationCounterService = userNotificationCounterService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<Object> getNotifications(
            Account account,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "per_page", defaultValue = "20") int perPage) {
        ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
        apiResponse.setStatus(ErrorCode.SUCCESS);
        try {
            Page<NotificationEntity> pageData =
                    notificationService.loadLatestSentNotifications(account.uuid(), page, perPage);
            PagedResponse<NotificationResponseDTO> pagedResponse = new PagedResponse<NotificationResponseDTO>();
            pagedResponse.setEntities(pageData.map(entity -> new NotificationResponseDTO(entity)).getContent());
            pagedResponse.setCount((int) pageData.getTotalElements());
            apiResponse.setData(pagedResponse);
        } catch (Exception e) {
            apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
        }

        return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }
    
    @PutMapping(value = "/notifications/{uuid}/mark-as-read")
	public ResponseEntity<?> markAsRead(final Account account, @PathVariable("uuid") final String uuid) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
		NotificationEntity notification = notificationService.markAsRead(account, uuid);
		if (notification == null) {
			apiResponse.setStatus(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		apiResponse.setData(notification);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/notifications/counter/unread")
	public ResponseEntity<?> getUnReadCounter(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			long offset = userNotificationCounterService.getOffSetByUserUuid(account.uuid());
			int counter = notificationService.countByReceiverAndOffset(account.uuid(), offset);
			apiResponse.setData(Collections.singletonMap("count", counter));
		} catch(Exception e) {
			LOGGER.error("getUnReadCounter -> ", e.getMessage());
			apiResponse.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.withDetail(e.getMessage()));
		}
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value = "/notifications/counter/unread")
	public ResponseEntity<?> resetCounter(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			long offset = notificationService.getLastIdByReceiver(account.uuid());
			userNotificationCounterService.updateOffsetForUser(account.uuid(), offset);
			apiResponse.setData(Collections.singletonMap("success", true));
		} catch(Exception e) {
			LOGGER.error("resetCounter -> ", e.getMessage());
			apiResponse.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.withDetail(e.getMessage()));
		}
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/notifications")
	public ResponseEntity<?> clearAll(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			notificationService.clearAllByReceiver(account.uuid());
			apiResponse.setData(Collections.singletonMap("success", true));
		} catch(Exception e) {
			LOGGER.error("clearAll -> ", e.getMessage());
			apiResponse.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.withDetail(e.getMessage()));
		}
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
