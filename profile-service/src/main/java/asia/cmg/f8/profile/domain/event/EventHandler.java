package asia.cmg.f8.profile.domain.event;

import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.profile.CompleteProfileEvent;
import asia.cmg.f8.common.profile.FollowingConnectionEvent;
import asia.cmg.f8.common.profile.UserUnFollowingConnectionEvent;
import asia.cmg.f8.profile.AnswerSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 10/25/16.
 */
@Component
@EnableBinding(SubEventStream.class)
public class EventHandler {

    @Autowired
    @Qualifier("answerSubmittedEventConverter")
    private MessageConverter answerMessageConverter;

    @Autowired
    @Qualifier("completeProfileEventConverter")
    private MessageConverter completeProfileEventConverter;

    @Autowired
    @Qualifier("changeUserInfoEventConverter")
    private MessageConverter changUserInfoEventConverter;

    @Autowired
    @Qualifier("changeFollowingEventConverter")
    private MessageConverter changeFollowingEventConverter;
    
    @Autowired
    @Qualifier("userUnFollowingEventConverter")
    private MessageConverter userUnFollowingEventConverter;

    private final SubEventStream subEventStream;

    public EventHandler(final SubEventStream subEventStream) {
        this.subEventStream = subEventStream;
    }

    public void publish(final AnswerSubmittedEvent event) {
    	subEventStream.answerEvents().send(answerMessageConverter.toMessage(event, null));
    }

    public void publish(final CompleteProfileEvent event) {
    	subEventStream.completeProfile().send(completeProfileEventConverter.toMessage(event, null));
    }

    public void publish(final ChangeUserInfoEvent event) {
    	subEventStream.changeUserInfo().send(changUserInfoEventConverter.toMessage(event, null));
    }

    public void publish(final FollowingConnectionEvent event) {
    	subEventStream.changeFollowingEvent().send(changeFollowingEventConverter.toMessage(event, null));
    }

    public void publish(final UserUnFollowingConnectionEvent event) {
    	subEventStream.userUnFollowingInfo().send(userUnFollowingEventConverter.toMessage(event, null));
    }
}
