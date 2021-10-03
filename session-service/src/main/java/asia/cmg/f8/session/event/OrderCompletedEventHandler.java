package asia.cmg.f8.session.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.session.service.OrderManagementService;

import org.apache.log4j.spi.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Listen on {@link OrderCompletedEvent} and setup order and session package.
 * Created on 12/22/16.
 */
@Component
public class OrderCompletedEventHandler extends BaseOrderCompletedEventHandler {

    @Inject
    private OrderManagementService orderManagementService;

    @StreamListener(EventStream.ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
    public final void onEvent(final Message message) {
        final OrderCompletedEvent event = (OrderCompletedEvent) getOrderCompletedEventConverter()
                .fromMessage(message, OrderCompletedEvent.class);
        orderManagementService.createOrderSetupPackage(event);
    }
}
