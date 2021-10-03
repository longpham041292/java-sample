package asia.cmg.f8.session.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
public interface UserEventStream {

    String USER_ACTIVATED_INPUT_EVENT_CHANNEL = "userActivatedInput";
    String CHANGE_USER_INFO_INPUT_CHANNEL = "changeUserInfoIn";
    


    @Input(USER_ACTIVATED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleNewUserActivated();

    @Input(CHANGE_USER_INFO_INPUT_CHANNEL)
    SubscribableChannel handleChangeUserInfoEvent();
    
    
}
