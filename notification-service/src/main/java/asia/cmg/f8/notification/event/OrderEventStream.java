package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by nhieu on 8/23/17.
 */
public interface OrderEventStream {

    String ORDER_COMPLETED_CHANNEL = "orderCompletedInput";
    String ORDER_CREDIT_COMPLETED_CHANNEL = "orderCreditCompletedInput";

    @Input(ORDER_COMPLETED_CHANNEL)
    SubscribableChannel orderCompleted();
    
    @Input(ORDER_CREDIT_COMPLETED_CHANNEL)
    SubscribableChannel orderCreditCompleted();
}
