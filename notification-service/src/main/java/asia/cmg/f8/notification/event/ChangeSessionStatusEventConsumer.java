package asia.cmg.f8.notification.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.notify.NotifyChangeSessionStatusEvent;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.InboxMessageType;
import asia.cmg.f8.notification.push.NotifyChangeSessionStatusHandler;
import asia.cmg.f8.notification.service.inbox.InboxService;
import asia.cmg.f8.session.ChangeSessionStatusEvent;

/**
 * Created on 1/5/17.
 */
@Component
@EnableBinding(ChangeSessionStatusEventStream.class)
public class ChangeSessionStatusEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeSessionStatusEventConsumer.class);

    @Autowired
    @Qualifier("changeSessionStatusEventConverter")
    private MessageConverter changeSessionStatusEventConverter;
    private final InboxService inboxService;
    private final NotifyChangeSessionStatusHandler notifyHandler;

    @Inject
    public ChangeSessionStatusEventConsumer(final InboxService inboxService, final NotifyChangeSessionStatusHandler notifyHandler) {
        this.inboxService = inboxService;
        this.notifyHandler = notifyHandler;
    }

    /**
     * Handle change session status for inbox message event.
     *
     * @param message message
     */
    @StreamListener(ChangeSessionStatusEventStream.CHANGE_SESSION_STATUS_CHANNEL)
    public void handleEvent(final Message<?> message) {

        final ChangeSessionStatusEvent event = (ChangeSessionStatusEvent) changeSessionStatusEventConverter.fromMessage(message, ChangeSessionStatusEvent.class);

        final SessionStatus newStatus = SessionStatus.valueOf(event.getNewStatus().toString());
        final InboxMessageType messageType = getMessageType(newStatus);
        String inboxUuid = StringUtils.EMPTY;

        /**
         * Only create inbox message if session is cancelled.
         */
        if (messageType != null) {

            LOG.info("Handling Change Session Status Event to create inbox message type: {}", messageType.toString());

            final Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("session_id", event.getSessionId().toString());
            messageContent.put("session_date", event.getSessionDate());
            final InboxMessageEntity entity = InboxMessageEntity.builder()
                    .content(messageContent)
                    .createdDate(event.getSubmittedAt())
                    .inboxMessageType(messageType)
                    .isRead(Boolean.FALSE)
                    .packageId(event.getPackageUuid().toString())
                    .senderId(event.getEndUserId().toString())
                    .userId(event.getTrainerId().toString())
                    .clientInfo(Collections.emptyList())
                    .build();

            final Optional<InboxMessageEntity> inboxMessageOptional = inboxService.createInboxMessage(entity);

            if (inboxMessageOptional.isPresent()) {
                inboxUuid = inboxMessageOptional.get().getId();
                LOG.info("Created inbox message {} for event {}", inboxUuid, event.getEventId());
            } else {
                LOG.error("Could not create inbox message for event {}", event.getEventId());
            }
        }

        //fire event push notification
        final SessionStatus oldStatus = SessionStatus.valueOf(event.getOldStatus().toString());
        final NotifyChangeSessionStatusEvent notifyEvent = new NotifyChangeSessionStatusEvent();
        notifyEvent.setEventId(UUID.randomUUID().toString());
        notifyEvent.setSubmittedAt(System.currentTimeMillis());
        notifyEvent.setNewSessionStatus(newStatus.toString());
        notifyEvent.setOldSessionStatus(oldStatus.toString());
        notifyEvent.setInboxMessageUuid(inboxUuid);
        notifyEvent.setSessionUuid(event.getSessionId().toString());
        notifyEvent.setTrainerUuid(event.getTrainerId().toString());
        notifyEvent.setEndUserUuid(event.getEndUserId().toString());
        notifyEvent.setSessionDate(event.getSessionDate());
        notifyEvent.setBookedBy(event.getBookedBy());
        notifyEvent.setChangedBy(event.getChangedBy());
        notifyHandler.publish(notifyEvent);
        LOG.info("Fired event notify for user session status event");
    }

    private InboxMessageType getMessageType(final SessionStatus newStatus) {
        switch (newStatus) {
            case EU_CANCELLED:
                return InboxMessageType.CANCELLATION_WITHIN_24H;
            case CANCELLED:
                return InboxMessageType.CANCELLATION_OUTSIDE_24H;
            default:
                return null;
        }
    }
}
