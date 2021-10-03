package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 1/6/17.
 */
public interface SessionBookCompleteEventStream {

    String SESSION_BOOK_COMPLETE_INPUT = "sessionBookingCompletedInput";

    @Input(SESSION_BOOK_COMPLETE_INPUT)
    SubscribableChannel consumeCompleteBookSessionEvent();
}
