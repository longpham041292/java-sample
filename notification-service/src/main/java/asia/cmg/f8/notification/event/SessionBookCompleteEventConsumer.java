package asia.cmg.f8.notification.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.notify.NotifyChangeSessionStatusEvent;
import asia.cmg.f8.common.event.session.SessionBook;
import asia.cmg.f8.common.event.session.SessionBookCompleteEvent;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.InboxMessageType;
import asia.cmg.f8.notification.push.NotifyChangeSessionStatusHandler;
import asia.cmg.f8.notification.service.inbox.InboxService;

/**
 * Created on 1/5/17.
 */
@Component
@EnableBinding(SessionBookCompleteEventStream.class)
public class SessionBookCompleteEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(SessionBookCompleteEventConsumer.class);

    private static final String SESSION_ID = "session_id";
    private static final String SESSION_DATE = "session_date";

    @Inject
    private AvroSchemaMessageConverter sessionBookCompleteEventConverter;

    private final InboxService inboxService;
    private final NotifyChangeSessionStatusHandler notifyHandler;

    @Inject
    public SessionBookCompleteEventConsumer(final InboxService inboxService,
                                            final NotifyChangeSessionStatusHandler notifyHandler) {
        this.inboxService = inboxService;
        this.notifyHandler = notifyHandler;
    }

    /**
     * Handle created package event.
     *
     * @param message message
     */
    @StreamListener(SessionBookCompleteEventStream.SESSION_BOOK_COMPLETE_INPUT)
    public void handleSessionBookCompleteEvent(final Message<?> message) {

        LOG.info("Handling Session Book Complete Event to create inbox message NEW_BOOKING");

        final SessionBookCompleteEvent event = (SessionBookCompleteEvent) sessionBookCompleteEventConverter.fromMessage(message, SessionBookCompleteEvent.class);
        if (Objects.isNull(event.getPtUuid()) || Objects.isNull(event.getUserId())) {
            LOG.error("PT uuid or Eu uuid is invalid");
            return;
        }

        final String ptUuid = event.getPtUuid().toString();
        final String euUuid = event.getUserId().toString();
        final String bookedBy = getBookedBy(event);
        final List<SessionBook> listSessionBook = event.getSessions();
        Map<String, String> inboxUuidMap = Collections.emptyMap();

        /**
         * We only create inbox message if session is created by EU.
         */
        if (!shouldCreateTrainerInbox(bookedBy)) {

            final List<InboxMessageEntity> listInboxMessageEntity = listSessionBook.stream().filter(session -> {
                final String newStatus = session.getNewSessionStatus().toString();
                return SessionStatus.valueOf(newStatus).equals(SessionStatus.PENDING);
            }).map(sessionBook -> {
                final String sessionUuid = sessionBook.getSessionUuid().toString();
                final String packageUuid = sessionBook.getPackageUuid().toString();
                final Long sessionDate = sessionBook.getSessionDate();
                final Map<String, Object> messageContent = new HashMap<>();
                messageContent.put(SESSION_ID, sessionUuid);
                messageContent.put(SESSION_DATE, sessionDate);
                return InboxMessageEntity.builder()
                        .content(messageContent)
                        .createdDate(event.getSubmittedAt())
                        .inboxMessageType(InboxMessageType.NEW_BOOKING)
                        .isRead(Boolean.FALSE)
                        .packageId(packageUuid)
                        .senderId(euUuid)
                        .userId(ptUuid)
                        .clientInfo(Collections.emptyList())
                        .build();
            }).collect(Collectors.toList());

            final Optional<List<InboxMessageEntity>> bulkCreatedResp = inboxService.bulkCreateInboxMessage(listInboxMessageEntity);
            if (!bulkCreatedResp.isPresent()) {
                LOG.error("Could not create inbox message for event {}", event.getEventId());
                inboxUuidMap = Collections.emptyMap();
            } else {
                inboxUuidMap = bulkCreatedResp.get().stream()
                        .filter(msg -> msg.getContent() != null && msg.getContent().get(SESSION_ID) != null)
                        .collect(Collectors.toMap(inboxMessageEntity -> (String) inboxMessageEntity.getContent().get(SESSION_ID), InboxMessageEntity::getId));
            }

            LOG.info("Created {} inbox messages for event {}", listInboxMessageEntity.size(), event.getEventId());
        }

        final Map<String, String> inboxIds = inboxUuidMap;

        listSessionBook.stream().forEach(sessionBook -> {
            final String sessionId = sessionBook.getSessionUuid().toString();
            final NotifyChangeSessionStatusEvent notifyEvent = new NotifyChangeSessionStatusEvent();
            notifyEvent.setEndUserUuid(euUuid);
            notifyEvent.setTrainerUuid(ptUuid);
            notifyEvent.setSessionUuid(sessionId);
            notifyEvent.setSessionDate(sessionBook.getSessionDate());
            notifyEvent.setBookedBy(event.getBookedBy());
            notifyEvent.setChangedBy(event.getBookedBy());
            
            notifyEvent.setNewSessionStatus(sessionBook.getNewSessionStatus().toString());
            notifyEvent.setOldSessionStatus(SessionStatus.OPEN.toString());

            notifyEvent.setSubmittedAt(System.currentTimeMillis());
            notifyEvent.setEventId(UUID.randomUUID().toString());
            notifyEvent.setInboxMessageUuid(inboxIds.get(sessionId));
            notifyEvent.setOwnerUuid(event.getOwnerUuid());
            
            notifyHandler.publish(notifyEvent);
            LOG.info("Fired notify event for new booking session {}", sessionId);
        });
    }

    private boolean shouldCreateTrainerInbox(final String bookedBy) {
        return CommonConstant.PT_USER_TYPE.equalsIgnoreCase(bookedBy) || CommonConstant.ADMIN_USER_TYPE.equalsIgnoreCase(bookedBy) ;
    }

    private String getBookedBy(final SessionBookCompleteEvent event) {
        final CharSequence bookedBy = event.getBookedBy();
        if (bookedBy != null) {
            return String.valueOf(bookedBy);
        }
        return null;
    }
}
