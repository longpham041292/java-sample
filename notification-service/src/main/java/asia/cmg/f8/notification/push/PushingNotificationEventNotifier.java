package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.message.PushingNotificationEvent;
import asia.cmg.f8.common.spec.notification.NotificationType;
import asia.cmg.f8.notification.dto.SocialUserInfo;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@EnableBinding(NotificationEventStream.class)
public class PushingNotificationEventNotifier extends NotificationSender {
    private static final SocialUserInfo NO_AVATAR = null;
    private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
    private static final String MESSAGE_TYPE = "PushingNotification";

    public static final Logger LOGGER = LoggerFactory.getLogger(PushingNotificationEventNotifier.class);

    @Autowired
    @Qualifier("pushingNotificationEventConverter")
    private MessageConverter messageConverter;

    @StreamListener(NotificationEventStream.PUSHING_NOTIFICATION_EVENT_INPUT_CHANNEL)
    public void onEvent(final Message message) {
        PushingNotificationEvent event = (PushingNotificationEvent) messageConverter
        		.fromMessage(message, PushingNotificationEvent.class);
        
        PushMessage pushMessage = createLocalizedMessageClient(event);
        String language = event.getLanguage() == null ? null : event.getLanguage().toString();
        sendToUser(event.getReceiverUuid().toString(), pushMessage,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getName().toString(), language);
    }
    
    private PushMessage createLocalizedMessageClient(final PushingNotificationEvent event) {
        final PushMessage message = new PushMessage(MESSAGE_TYPE);
        message.setLocalizedMessage(event.getMessageKey().toString(), event.getMessageData().toArray());
        
        Map<String, Object> customData = new HashedMap();
        customData.put(PushMessage.CUSTOM_DATA_TYPE_KEY, NotificationType.valueOf(event.getName().toString()).getType());
        if(event.getCustomData() != null) {
        	event.getCustomData().forEach(data -> {
        		customData.put(data.getOption().toString(), data.getValue().toString());
        	});
        }
        message.setCustomData(customData);
        return message;
    }
}
