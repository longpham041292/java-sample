/**
 * 
 */
package asia.cmg.f8.commerce.event;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.commerce.service.PromotionService;
import asia.cmg.f8.common.event.OrderCreditCompletedEvent;

/**
 * @author khoa.bui
 *
 */
@Component
@EnableBinding(CommerceEventStream.class)
public class CommerceEventListener {

	@Autowired
	@Qualifier("orderCompletedEventConverter")
	private MessageConverter orderCompletedEventConverter;
	
	@Autowired
	@Qualifier("orderCreditCompletedEventConverter")
	private MessageConverter orderCreditCompletedEventConverter;

	private final PromotionService promotionService;
	private static final Logger LOG = LoggerFactory.getLogger(CommerceEventListener.class);

	@Inject
	public CommerceEventListener(final PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	@StreamListener(CommerceEventStream.ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
	public void handleNewOrderCompletedEvent(final Message<Object> message) {
		final OrderCompletedEvent event = (OrderCompletedEvent) orderCompletedEventConverter.fromMessage(message,
				OrderCompletedEvent.class);
		LOG.info("[handleNewOrderCompletedEvent] Handle Order Product Completed event for updating coupon usage");
		promotionService.updatePromotionUsage(event);
	}
	
	@StreamListener(CommerceEventStream.ORDER_CREDIT_COMPLETED_INPUT_EVENT_CHANNEL)
	public void handleNewOrderCreditCompletedEvent(final Message<Object> message) {
		final OrderCreditCompletedEvent event = (OrderCreditCompletedEvent) orderCreditCompletedEventConverter.fromMessage(message,
				OrderCreditCompletedEvent.class);
		LOG.info("[handleNewOrderCreditCompletedEvent] Handle Order Credit Completed event for updating coupon usage");
		promotionService.updatePromotionUsage(event);
	}
}
