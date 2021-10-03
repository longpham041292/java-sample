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
 * Listen on {@link asia.cmg.f8.commerce.OrderCompletedEvent} and create new user if it's not existed.
 * <p>
 * Created on 12/23/16.
 */
@Component
public class CreatePtUserHandler extends BaseOrderCompletedEventHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreatePtUserHandler.class);
    public static final String SETUP_PT_USER_CHANNEL_NAME = "setupPtUserChannel";

    @Inject
    private UserManagementService userManagementService;

    @StreamListener(value = SETUP_PT_USER_CHANNEL_NAME)
    public final void onEvent(final Message message) {

        final OrderCompletedEvent event = (OrderCompletedEvent) getOrderCompletedEventConverter().fromMessage(message, OrderCompletedEvent.class);

        final String ptUuid = String.valueOf(event.getPtUuid());
        userManagementService.createIfNotExist(ptUuid);
        LOGGER.info("Setup user {} - SUCCESS", ptUuid);
    }
}
