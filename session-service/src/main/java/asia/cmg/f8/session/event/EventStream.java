package asia.cmg.f8.session.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 10/21/16.
 */
public interface EventStream {

    String ORDER_COMPLETED_INPUT_EVENT_CHANNEL = "orderCompletedInput";
    String ORDER_SUBSCRIPTION_COMPLETED_INPUT_EVENT_CHANNEL = "orderSubscriptionCompletedInput";
    String CHANGE_SESSION_STATUS_OUTPUT_CHANNEL = "changeSessionStatusOutput";
    String NEW_ORDER_OUTPUT_EVENT_CHANNEL = "newOrderOutput";
    String NEW_ORDER_INPUT_EVENT_CHANNEL = "newOrderInput";
    String TRANSFER_SESSIONS_EVENT_CHANNEL = "transferSessionsOutput";
    String SESSION_BOOKING_COMPLETE_OUT_CHANNEL = "sessionBookingCompletedOutput";
    String SCHEDULE_EVENT_OUTPUT = "scheduleEventOutput";
    String PUSHING_NOTIFICATION_EVENT_OUTPUT = "pushingNotificationEventOutput";

    @Input(ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleOrderCompleted();
    
    @Input(ORDER_SUBSCRIPTION_COMPLETED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleOrderSubscriptionCompleted();

    @Output(CHANGE_SESSION_STATUS_OUTPUT_CHANNEL)
    MessageChannel changeSessionStatus();

    @Output(NEW_ORDER_OUTPUT_EVENT_CHANNEL)
    MessageChannel newOrderCommerce();

    @Input(NEW_ORDER_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleNewOrderCommerce();

    @Input(CreatePtUserHandler.SETUP_PT_USER_CHANNEL_NAME)
    SubscribableChannel handleSetupPtUserChannel();

    @Input(CreateUserHandler.SETUP_USER_CHANNEL_NAME)
    SubscribableChannel handleSetupUserChannel();

    @Output(TRANSFER_SESSIONS_EVENT_CHANNEL)
    MessageChannel transferSessions();

    @Output(SESSION_BOOKING_COMPLETE_OUT_CHANNEL)
    MessageChannel bookingSessionCompleted();
    
    @Output(SCHEDULE_EVENT_OUTPUT)
    MessageChannel scheduleEventOutput();
    
    @Output(PUSHING_NOTIFICATION_EVENT_OUTPUT)
    MessageChannel pushingNotificationEventOutput();
}
