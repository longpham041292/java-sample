package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.event.notification.DeviceRegisteredEvent;
import asia.cmg.f8.notification.client.DeviceClient;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created on 1/5/17.
 */
@Component
@EnableBinding(NotificationEventStream.class)
public class DeviceRegisteredEventHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegisteredEventHandler.class);

    private final DeviceClient deviceClient;

    @Autowired
    @Qualifier("deviceRegisteredEventConverter")
    private MessageConverter messageConverter;

    public DeviceRegisteredEventHandler(final DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    @StreamListener(NotificationEventStream.DEVICE_REGISTERED_INPUT_CHANNEL)
    public void onEvent(final Message message) {
        final DeviceRegisteredEvent event = (DeviceRegisteredEvent) messageConverter.fromMessage(message, DeviceRegisteredEvent.class);
        if (event != null) {

            final UserGridResponse<Map<String, Object>> response = deviceClient.connectDevice(String.valueOf(event.getUserId()), String.valueOf(event.getDeviceId()));
            if (response != null) {
                LOGGER.info("Connected device {} and user {}", event.getDeviceId(), event.getUserId());
            } else {
                LOGGER.info("Failed to connect device {} and user {}", event.getDeviceId(), event.getUserId());
            }
        }
    }
}
