package asia.cmg.f8.schedule.client;

import org.springframework.stereotype.Component;

/**
 * Created on 4/18/17.
 */
@Component
public class SessionNotificationClientFallback implements SessionNotificationClient {

    @Override
    public void triggerNotification() {
        // empty
    }
}
