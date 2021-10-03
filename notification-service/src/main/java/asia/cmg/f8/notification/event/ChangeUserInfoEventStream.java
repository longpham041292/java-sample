package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 2/7/17.
 */
public interface ChangeUserInfoEventStream {
    String CHANGE_USER_INFO_INPUT_CHANNEL = "changeUserInfoInput";

    @Input(CHANGE_USER_INFO_INPUT_CHANNEL)
    SubscribableChannel consumeChangeUserInfoEvent();
    
}
