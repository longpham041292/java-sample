package asia.cmg.f8.session.event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;
import asia.cmg.f8.session.service.UserSubscriptionService;

@Component
public class OrderSubscriptionCompletedEventHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderSubscriptionCompletedEventHandler.class);
			
	@Autowired
	@Qualifier("orderSubscriptionCompletedEventConverter")
	private MessageConverter orderSubscriptionCompletedEventConverter;

	@Autowired
	private UserSubscriptionService userSubscriptionService;

	@StreamListener(EventStream.ORDER_SUBSCRIPTION_COMPLETED_INPUT_EVENT_CHANNEL)
	public final void onEvent(final Message message) {
		logger.info("on event handler order Subscription complete");
		
		final OrderSubscriptionCompletedEvent event = (OrderSubscriptionCompletedEvent) orderSubscriptionCompletedEventConverter
				.fromMessage(message, OrderSubscriptionCompletedEvent.class);
		userSubscriptionService.createUserSubscription(event);
	}
}
