package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.event.notify.NotifyChangeSessionStatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 1/9/17.
 */
@Component
@EnableBinding(NotifyChangeSessionStatusStream.class)
public class NotifyChangeSessionStatusHandler {

    private final NotifyChangeSessionStatusStream eventStream;

    public NotifyChangeSessionStatusHandler(final NotifyChangeSessionStatusStream eventStream) {
        this.eventStream = eventStream;
    }

    @Autowired
    @Qualifier("notifyChangeSessionStatusConverter")
    private MessageConverter notifyChangeSessionStatusConverter;

    public void publish(final NotifyChangeSessionStatusEvent event) {
        eventStream.notifyChangeSessionStatus()
                .send(notifyChangeSessionStatusConverter.toMessage(event, null));
    }
}
