package asia.cmg.f8.notification.push;

import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.DateTimeUtils;
import asia.cmg.f8.session.ScheduleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableBinding(NotificationEventStream.class)
public class ScheduleEventNotifier extends NotificationSender {
    public static final String SCHEDULE_TYPE = "wallet";
    private static final SocialUserInfo NO_AVATAR = null;
    private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();

    public static final String SCHEDULE_REMIND_CLIENT_MESSAGE_KEY = "message.session.schedule.remind.client";
    public static final String SCHEDULE_EXPIRE_MESSAGE_KEY = "message.session.schedule.expire";
    public static final String SCHEDULE_REMIND_PT_CONFIRM_MESSAGE_KEY = "message.session.schedule.remind.pt.confirm";

    public static final Logger LOGGER = LoggerFactory.getLogger(ScheduleEventNotifier.class);

    private MessageConverter messageConverter;

    @Autowired
    public ScheduleEventNotifier(@Qualifier("scheduleEventConverter") MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @StreamListener(NotificationEventStream.SCHEDULE_EVENT_INPUT_CHANNEL)
    public void onEvent(final Message message) {
        ScheduleEvent event = (ScheduleEvent) messageConverter.fromMessage(message, ScheduleEvent.class);
        switch (ENotificationEventName.valueOf(event.getNotiType().toString())) {
            case CLASS_STARTING_REMIND:
                remindEu(event);
                break;
            case SESSION_PT_CONFIRM:
                remindPTConfirm(event);
                break;
            case CLASS_EXPIRE_REMIND:
                notifyExpiredSchedule(event);
                break;
        }
    }

    private void notifyExpiredSchedule(ScheduleEvent event) {
        LocalDateTime startAt = DateTimeUtils.convertToLocalDateTime(event.getStartAt());

        final PushMessage message =
                createLocalizedMessageClient(
                        SCHEDULE_EXPIRE_MESSAGE_KEY, event, DateTimeUtils.formatDatetime(startAt));
        sendToUser(event.getUserUuid().toString(), message,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    private void remindPTConfirm(ScheduleEvent event) {
        LocalDateTime startAt = DateTimeUtils.convertToLocalDateTime(event.getStartAt());

        final PushMessage message =
                createLocalizedMessageClient(
                        SCHEDULE_REMIND_PT_CONFIRM_MESSAGE_KEY, event, DateTimeUtils.formatDatetime(startAt));
        sendToUser(event.getUserUuid().toString(), message,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    private void remindEu(ScheduleEvent event) {
        final PushMessage message =
                createLocalizedMessageClient(
                        SCHEDULE_REMIND_CLIENT_MESSAGE_KEY, event, event.getClassName(), event.getMinBeforeStarting());
        sendToUser(event.getUserUuid().toString(), message,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    private PushMessage createLocalizedMessageClient(final String messageCode, ScheduleEvent event, final Object... args) {
        final PushMessage message = new PushMessage(SCHEDULE_TYPE);
        message.setLocalizedMessage(messageCode, args);

        Map<String, Object> customData = new HashMap<>();
        customData.put(PushMessage.CUSTOM_DATA_TYPE_KEY,
                ENotificationEventName.valueOf(event.getNotiType().toString()).getType());
        customData.put(PushMessage.CUSTOM_DATA_UUID_KEY, event.getUserUuid().toString());
        message.setCustomData(customData);

        return message;
    }
}
