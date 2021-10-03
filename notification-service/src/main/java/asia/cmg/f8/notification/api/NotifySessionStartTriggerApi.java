package asia.cmg.f8.notification.api;

import asia.cmg.f8.notification.service.session.NotifySessionStartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 4/18/17.
 */
@RestController
public class NotifySessionStartTriggerApi {

    private final NotifySessionStartService notifySessionStartService;

    public NotifySessionStartTriggerApi(final NotifySessionStartService notifySessionStartService) {
        this.notifySessionStartService = notifySessionStartService;
    }

    @RequestMapping(path = "/event/session/notifySessionStart", method = POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity triggerNotifySessionStart() {

        // this logic is handled in the manner of async.
        notifySessionStartService.triggerNotifySessionStart();
        return new ResponseEntity<>(Collections.singletonMap("processing", Boolean.TRUE), HttpStatus.OK);
    }
}
