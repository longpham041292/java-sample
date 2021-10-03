package asia.cmg.f8.notification.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created on 4/18/17.
 */
@Component
public class SessionNotificationClientFallback implements SessionNotificationClient {

    @Override
    public List<SessionNotificationInfo> getEligibleSessions(@RequestParam("fromTime") final long fromTime, @RequestParam("toTime") final long toTime) {
        return null;
    }
}
