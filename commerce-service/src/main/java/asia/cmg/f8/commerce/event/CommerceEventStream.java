package asia.cmg.f8.commerce.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CommerceEventStream {

	String ORDER_COMPLETED_INPUT_EVENT_CHANNEL = "orderCompletedInput";
	String ORDER_CREDIT_COMPLETED_INPUT_EVENT_CHANNEL = "orderCreditCompletedInput";
    String ORDER_COMPLETED_OUTPUT_CHANNEL = "orderCompletedOutput";
    String ORDER_SUBSCRIPTION_COMPLETED_OUTPUT_CHANNEL = "orderSubscriptionCompletedOutput";
    String ORDER_CREDIT_COMPLETED_OUTPUT_CHANNEL = "orderCreditCompletedOutput";
    String WALLET_EVENT_OUTPUT_CHANNEL = "walletEventOutput";

    @Output(ORDER_COMPLETED_OUTPUT_CHANNEL)
    MessageChannel orderCompletedEvent();
    
    @Output(ORDER_SUBSCRIPTION_COMPLETED_OUTPUT_CHANNEL)
    SubscribableChannel orderSubscriptionCompletedEvent();
    
    @Output(ORDER_CREDIT_COMPLETED_OUTPUT_CHANNEL)
    SubscribableChannel orderCreditCompletedEvent();

    @Output(WALLET_EVENT_OUTPUT_CHANNEL)
    SubscribableChannel walletEvent();
    
    @Input(ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleOrderCompleted();
    
    @Input(ORDER_CREDIT_COMPLETED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleOrderCreditCompleted();
}
