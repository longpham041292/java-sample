package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.event.notification.DeviceRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;

/**
 * Created on 1/6/17.
 */
@Service
public class NotificationEventPublisher {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationEventPublisher.class);

    @Autowired
    @Qualifier("deviceRegisteredEventConverter")
    private MessageConverter messageConverter;

    private final NotificationEventStream stream;

    public NotificationEventPublisher(final NotificationEventStream stream) {
        this.stream = stream;
    }

    public boolean publish(final DeviceRegisteredEvent event) {
        final Message message = messageConverter.toMessage(event, null);
        final boolean success = stream.deviceRegisteredMessageChannel().send(message);

        // logging.
        final String simpleName = DeviceRegisteredEvent.class.getSimpleName();
        final CharSequence deviceId = event.getDeviceId();
        final CharSequence userId = event.getUserId();
        if (success) {
            LOGGER.info("Sent {} event with device {} and user {}", simpleName, deviceId, userId);
        } else {
            LOGGER.info("Failed to send {} event with device {} and user {}", simpleName, deviceId, userId);
        }

        return success;
    }
}
