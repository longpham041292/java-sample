package asia.cmg.f8.notification.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;
import asia.cmg.f8.notification.push.OrderSubscriptionNotifier;

@Component
@EnableBinding(OrderSubscriptionEventStream.class)
public class OrderSubscriptionEventConsumer {

	@Autowired
	@Qualifier("orderSubscriptionCompletedEventConverter")
	private AvroSchemaMessageConverter messageEventConverter;

	@Autowired
	private OrderSubscriptionNotifier subscribeNotifier;

	@StreamListener(OrderSubscriptionEventStream.ORDER_SUBSCRIPTION_COMPLETED_CHANNEL)
	public void handleOrderSubscriptionCompletedEvent(final Message<?> message) {
		final OrderSubscriptionCompletedEvent event = (OrderSubscriptionCompletedEvent) messageEventConverter
				.fromMessage(message, OrderCompletedEvent.class);
		subscribeNotifier.sendOrderSubscriptionCompletedMessage(event);
	}

}
