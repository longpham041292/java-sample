package asia.cmg.f8.session.api;

import asia.cmg.f8.session.service.SessionNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Query sessions will be started from given time range for notification.
 * Created on 4/18/17.
 */
@RestController
public class SessionNotificationApi {

    private final SessionNotificationService notificationService;

    public SessionNotificationApi(final SessionNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(path = "/notification/sessions")
    public ResponseEntity listOfEligibleSessions(@RequestParam(name = "fromTime") final long fromTime,
                                                 @RequestParam("toTime") final long toTime) {

        return new ResponseEntity<>(notificationService.searchForNotification(fromTime, toTime), HttpStatus.OK);
    }
}
