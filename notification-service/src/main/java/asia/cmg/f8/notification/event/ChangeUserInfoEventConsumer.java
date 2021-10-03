package asia.cmg.f8.notification.event;

import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.notification.push.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 2/7/17.
 */
@Component
@EnableBinding(ChangeUserInfoEventStream.class)
public class ChangeUserInfoEventConsumer {
    @Autowired
    @Qualifier("changeUserInfoEventConverter")
    private MessageConverter changeUserInfoEventConverter;
    
    private final UserInfoService userInfoService;

    public ChangeUserInfoEventConsumer(final UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @StreamListener(ChangeUserInfoEventStream.CHANGE_USER_INFO_INPUT_CHANNEL)
    public void handleChangeUserInfoEvent(final Message<?> message) {
        final ChangeUserInfoEvent event =
                (ChangeUserInfoEvent) changeUserInfoEventConverter.fromMessage(
                        message, ChangeUserInfoEvent.class);
        userInfoService.reloadLocale(event.getUserId().toString());

    }
}
