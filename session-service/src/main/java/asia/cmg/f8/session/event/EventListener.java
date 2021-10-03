package asia.cmg.f8.session.event;

import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.user.UserActivationEvent;
import asia.cmg.f8.session.NewOrderCommerceEvent;
import asia.cmg.f8.session.service.SynchronizationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created on 11/11/16.
 */
@Component
@EnableBinding(value = {UserEventStream.class})
public class EventListener {

	private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);
	
    @Autowired
    @Qualifier("newOrderEventConverter")
    private MessageConverter newOrderEventConverter;

    @Autowired
    @Qualifier("userActivatedEventConverter")
    private MessageConverter userActivatedEventConverter;

    @Autowired
    @Qualifier("changeUserInfoEventConverter")
    private MessageConverter changeUserInfoEventConverter;

    private final SynchronizationService synchronizationService;

    @Inject
    public EventListener(final SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @StreamListener(EventStream.NEW_ORDER_INPUT_EVENT_CHANNEL)
    public void handleNewOrderCommerceEvent(final Message message) {
        final NewOrderCommerceEvent newOrderCommerceEvent = (NewOrderCommerceEvent) newOrderEventConverter
                .fromMessage(message, NewOrderCommerceEvent.class);
        synchronizationService.handleNewOrderCommerceEvent(newOrderCommerceEvent);
    }

    @StreamListener(UserEventStream.USER_ACTIVATED_INPUT_EVENT_CHANNEL)
    public void handleNewUserActivatedEvent(final Message message) {
        final UserActivationEvent userActivationEvent = (UserActivationEvent) userActivatedEventConverter
                .fromMessage(message, UserActivationEvent.class);
        synchronizationService.handleNewUserActivated(userActivationEvent);
    }

    @StreamListener(UserEventStream.CHANGE_USER_INFO_INPUT_CHANNEL)
    public void handleChangeUserInfoEvent(final Message message) {
        final ChangeUserInfoEvent changeUserInfoEvent = (ChangeUserInfoEvent) changeUserInfoEventConverter
                .fromMessage(message, ChangeUserInfoEvent.class);
        synchronizationService.updateUserInfo(changeUserInfoEvent);
    }
    
    
   
}
