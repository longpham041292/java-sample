package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 1/5/17.
 */
public interface ChangeSessionStatusEventStream {

    String CHANGE_SESSION_STATUS_CHANNEL = "changeSessionStatusInput";
    
    @Input(CHANGE_SESSION_STATUS_CHANNEL)
    SubscribableChannel changeSessionStatus();
}
