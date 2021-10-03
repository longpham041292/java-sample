package asia.cmg.f8.commerce.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.commerce.WalletEvent;
import asia.cmg.f8.common.event.OrderCreditCompletedEvent;
import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CommerceEventStream.class)
public class CommerceEventProducer {

    private final CommerceEventStream eventStream;

    public CommerceEventProducer(final CommerceEventStream eventStream) {
        this.eventStream = eventStream;
    }

    @Autowired
    @Qualifier("orderCompletedEventConverter")
    private MessageConverter orderCompletedEventConverter;
    
	@Autowired
	@Qualifier("orderSubscriptionCompletedEventConverter")
	private MessageConverter orderSubscriptionCompletedEventConverter;
	
	@Autowired
	@Qualifier("orderCreditCompletedEventConverter")
	private MessageConverter orderCreditCompletedEventConverter;

	@Autowired
    @Qualifier("walletEventConverter")
    private MessageConverter walletEventConverter;

    public void publish(final OrderCompletedEvent event) {
        eventStream.orderCompletedEvent().send(orderCompletedEventConverter.toMessage(event, null));
    }
    
    public void publish(final OrderSubscriptionCompletedEvent event) {
        eventStream.orderSubscriptionCompletedEvent().send(orderSubscriptionCompletedEventConverter.toMessage(event, null));
    }
    
    public void publish(final OrderCreditCompletedEvent event) {
        eventStream.orderCreditCompletedEvent().send(orderCreditCompletedEventConverter.toMessage(event, null));
    }

    public void publish(final WalletEvent event) {
        eventStream.walletEvent().send(walletEventConverter.toMessage(event, null));
    }
}
