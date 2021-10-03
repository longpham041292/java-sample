package asia.cmg.f8.notification.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.OrderCreditCompletedEvent;
import asia.cmg.f8.notification.push.OrderNotifier;

@Component
@EnableBinding(OrderEventStream.class)
public class OrderCreditEventConsumer {

	@Autowired
	@Qualifier("orderCreditCompletedEventConverter")
	private AvroSchemaMessageConverter messageEventConverter;
	
	@Autowired
	private OrderNotifier orderNotifier;
	
	@StreamListener(OrderEventStream.ORDER_CREDIT_COMPLETED_CHANNEL)
	public void handleOrderCreditCompletedEvent(Message<?> message) {
		final OrderCreditCompletedEvent eventMessage = (OrderCreditCompletedEvent)messageEventConverter.fromMessage(message, OrderCreditCompletedEvent.class);
		orderNotifier.sendOrderCreditCompletedMessage(eventMessage);
	}
}
