package asia.cmg.f8.session.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.session.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created on 12/23/16.
 */
@Component
public class CreateUserHandler extends BaseOrderCompletedEventHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreateUserHandler.class);
    public static final String SETUP_USER_CHANNEL_NAME = "setupUserChannel";

    @Inject
    private UserManagementService userManagementService;

    @StreamListener(value = SETUP_USER_CHANNEL_NAME)
    public final void onEvent(final Message message) {

        final OrderCompletedEvent event = (OrderCompletedEvent) getOrderCompletedEventConverter().fromMessage(message, OrderCompletedEvent.class);

        final String userUuid = String.valueOf(event.getUserUuid());
        userManagementService.createIfNotExist(userUuid);
        LOGGER.info("Setup user {} - SUCCESS", userUuid);
    }
}
