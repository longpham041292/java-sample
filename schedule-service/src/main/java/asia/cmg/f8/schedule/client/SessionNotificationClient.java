package asia.cmg.f8.schedule.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created on 4/18/17.
 */
@FeignClient(value = "notification", url = "${service.notificationUrl}", fallback = SessionNotificationClientFallback.class)
public interface SessionNotificationClient {

    @RequestMapping(path = "/event/session/notifySessionStart", method = RequestMethod.POST)
    void triggerNotification();
}
