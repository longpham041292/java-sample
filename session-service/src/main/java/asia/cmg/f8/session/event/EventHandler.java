package asia.cmg.f8.session.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.session.SessionBookCompleteEvent;
import asia.cmg.f8.common.message.PushingNotificationEvent;
import asia.cmg.f8.session.ChangeSessionStatusEvent;
import asia.cmg.f8.session.NewOrderCommerceEvent;
import asia.cmg.f8.session.TransferSessionPackageEvent;

/**
 * Created on 10/25/16.
 */
@Component
@EnableBinding(EventStream.class)
public class EventHandler {

	@Autowired
	@Qualifier("sessionStatusEventConverter")
	private MessageConverter sessionStatusEventConverter;

	@Autowired
	@Qualifier("newOrderEventConverter")
	private MessageConverter newOrderEventConverter;

	@Autowired
	@Qualifier("transferSessionsEventConverter")
	private MessageConverter transferSessionsEventConverter;

	@Autowired
	@Qualifier("sessionBookingEventConverter")
	private MessageConverter sessionBookingEventConverter;

	@Autowired
	@Qualifier("scheduleEventOutputConverter")
	private MessageConverter scheduleEventOutputConverter;
	
	@Autowired
	@Qualifier("pushingNotificationEventOutputConverter")
	private MessageConverter pushingNotificationEventOutputConverter;

	private final EventStream eventStream;

	public EventHandler(final EventStream eventStream) {
		this.eventStream = eventStream;
	}

	public void publish(final ChangeSessionStatusEvent event) {
		eventStream.changeSessionStatus().send(sessionStatusEventConverter.toMessage(event, null));
	}

	public void publish(final NewOrderCommerceEvent event) {
		eventStream.newOrderCommerce().send(newOrderEventConverter.toMessage(event, null));
	}

	public void publish(final TransferSessionPackageEvent event) {
		eventStream.transferSessions().send(transferSessionsEventConverter.toMessage(event, null));
	}

	public void publish(final SessionBookCompleteEvent event) {
		eventStream.bookingSessionCompleted().send(sessionBookingEventConverter.toMessage(event, null));
	}

	public void publish(final asia.cmg.f8.session.ScheduleEvent event) {
		eventStream.scheduleEventOutput().send(scheduleEventOutputConverter.toMessage(event, null));
	}
	
	public void publish(final PushingNotificationEvent event) {
		eventStream.pushingNotificationEventOutput().send(pushingNotificationEventOutputConverter.toMessage(event, null));
	}
}
