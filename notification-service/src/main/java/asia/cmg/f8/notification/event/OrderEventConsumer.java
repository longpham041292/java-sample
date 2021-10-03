package asia.cmg.f8.notification.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.notification.push.NotificationService;
import asia.cmg.f8.notification.service.email.OrderConfirmationEmailService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by nhieu on 8/23/17.
 */
@Component
@EnableBinding(OrderEventStream.class)
public class OrderEventConsumer {

    @Inject
    private OrderConfirmationEmailService orderConfirmationEmailService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AvroSchemaMessageConverter orderCompletedEventConverter;

    /**
     * Handle Order complete event.
     *
     * @param message Consume message
     */
    @StreamListener(OrderEventStream.ORDER_COMPLETED_CHANNEL)
    public void handleOrderCompletedEvent(final Message<?> message) {
        final OrderCompletedEvent event =
                (OrderCompletedEvent) orderCompletedEventConverter.fromMessage(
                        message, OrderCompletedEvent.class);
        orderConfirmationEmailService.handle(event);
        notificationService.sendToUser(event);

    }

}
