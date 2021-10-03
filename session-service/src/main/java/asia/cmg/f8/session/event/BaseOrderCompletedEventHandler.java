package asia.cmg.f8.session.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.converter.MessageConverter;

/**
 * Created on 12/23/16.
 */
public class BaseOrderCompletedEventHandler {

    @Autowired
    @Qualifier("orderCompletedEventConverter")
    private MessageConverter orderCompletedEventConverter;

    public MessageConverter getOrderCompletedEventConverter() {
        return orderCompletedEventConverter;
    }
}
