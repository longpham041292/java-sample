package asia.cmg.f8.user.event;

import asia.cmg.f8.common.profile.SignUpUserEvent;
import asia.cmg.f8.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 11/17/16.
 */
@Component
public class EventListener {

    @Autowired
    @Qualifier("signUpUserEventConverter")
    private MessageConverter signUpUserEventConverter;

    private final RoleService roleService;

    public EventListener(final RoleService roleService) {
        this.roleService = roleService;
    }

    @StreamListener(EventStream.SIGN_UP_IN_CHANNEL)
    public void handleSignUpUser(final Message message) {
        final SignUpUserEvent signUpUserEvent = (SignUpUserEvent) signUpUserEventConverter
                .fromMessage(message, SignUpUserEvent.class);

        roleService.processAssignTrainerRole(signUpUserEvent);
    }
}
