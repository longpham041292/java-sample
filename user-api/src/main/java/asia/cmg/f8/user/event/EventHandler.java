package asia.cmg.f8.user.event;

import asia.cmg.f8.common.event.email.SubmitDocumentAdminEvent;
import asia.cmg.f8.common.event.user.AdminApprovedDocumentEvent;
import asia.cmg.f8.common.event.user.UnapproveDocumentEvent;
import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.profile.SignUpUserEvent;
import asia.cmg.f8.common.user.UserActivationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created by tuong.le on 11/3/16.
 */
@Component
@EnableBinding(EventStream.class)
public class EventHandler {

    @Autowired
    @Qualifier("submitDocumentEventConverter")
    private MessageConverter submitDocumentEventConverter;

    @Autowired
    @Qualifier("changeUserInfoEventConverter")
    private MessageConverter changUserInfoEventConverter;

    @Autowired
    @Qualifier("signUpUserEventConverter")
    private MessageConverter signUpUserEventConverter;

    @Autowired
    @Qualifier("userActivatedEventConverter")
    private MessageConverter userActivatedEventConverter;

    @Autowired
    @Qualifier("approvedDocumentEventConverter")
    private MessageConverter approvedDocumentEventConverter;
    
    @Autowired
    @Qualifier("unApprovedDocumentEventConverter")
    private MessageConverter unapprovedDocumentEventConverter;

    private final EventStream eventStream;

    public EventHandler(final EventStream eventStream) {
        this.eventStream = eventStream;
    }

    public void publish(final SubmitDocumentAdminEvent event) {
        eventStream.submitDocument().send(submitDocumentEventConverter.toMessage(event, null));
    }

    public void publish(final ChangeUserInfoEvent event) {
        eventStream.changeUserInfo().send(changUserInfoEventConverter.toMessage(event, null));
    }

    public void publish(final SignUpUserEvent event) {
        eventStream.signUpUser().send(signUpUserEventConverter.toMessage(event, null));
    }

    public void publish(final UserActivationEvent event) {
        eventStream.userActivated().send(userActivatedEventConverter.toMessage(event, null));
    }

    public void publish(final AdminApprovedDocumentEvent event) {
        eventStream.adminApprovedDocument()
                .send(approvedDocumentEventConverter.toMessage(event, null));
    }
    
    public void publish(final UnapproveDocumentEvent event) {
        eventStream.adminUnapprovedDocument()
                .send(unapprovedDocumentEventConverter.toMessage(event, null));
    }
  }
