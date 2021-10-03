package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OrderSubscriptionEventStream {

    String ORDER_SUBSCRIPTION_COMPLETED_CHANNEL = "orderSubscriptionCompletedInput";

    @Input(ORDER_SUBSCRIPTION_COMPLETED_CHANNEL)
    SubscribableChannel orderSubscriptionCompleted();
}
