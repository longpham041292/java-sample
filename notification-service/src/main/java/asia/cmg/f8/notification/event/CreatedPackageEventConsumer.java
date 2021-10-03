package asia.cmg.f8.notification.event;

import asia.cmg.f8.common.event.notify.NotifyChangeSessionStatusEvent;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.InboxMessageType;
import asia.cmg.f8.notification.push.NotifyChangeSessionStatusHandler;
import asia.cmg.f8.notification.service.inbox.InboxService;
import asia.cmg.f8.notification.service.inbox.QuestionAnswerService;
import asia.cmg.f8.session.CreatedSessionPackageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Created on 1/5/17.
 */
@Component
@EnableBinding(CreatedPackageEventStream.class)
public class CreatedPackageEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(CreatedPackageEventConsumer.class);

    @Inject
    private AvroSchemaMessageConverter createdPackageEventConverter;

    private final InboxService inboxService;
    private final QuestionAnswerService questionAnswerService;
    private final NotifyChangeSessionStatusHandler notifyHandler;

    @Inject
    public CreatedPackageEventConsumer(final InboxService inboxService,
                                       final QuestionAnswerService questionAnswerService,
                                       final NotifyChangeSessionStatusHandler notifyHandler) {
        this.inboxService = inboxService;
        this.questionAnswerService = questionAnswerService;
        this.notifyHandler = notifyHandler;
    }

    /**
     * Handle created package event.
     *
     * @param message message
     */
    @StreamListener(CreatedPackageEventStream.CREATED_PACKAGE_CHANNEL)
    public void handleCreatedPackageEvent(final Message<?> message) {
        LOG.info("Handling created package to create inbox message NEW_CLIENT");
        final CreatedSessionPackageEvent event =
                (CreatedSessionPackageEvent) createdPackageEventConverter.fromMessage(
                        message, CreatedSessionPackageEvent.class);

        final String euUuid = event.getUserUuid().toString();
        final String ptUuid = event.getPtUuid().toString();
        final String packageUuid = event.getPackageUuid().toString();

        final InboxMessageEntity entity = InboxMessageEntity.builder()
                .content(Collections.emptyMap())
                .createdDate(event.getSubmittedAt())
                .inboxMessageType(InboxMessageType.NEW_CLIENT)
                .isRead(Boolean.FALSE)
                .packageId(packageUuid)
                .senderId(euUuid)
                .userId(ptUuid)
                .clientInfo(questionAnswerService.getQuestionResponseOfUser(euUuid))
                .build();
        final Optional<InboxMessageEntity> createdInboxResp = 
                inboxService.createInboxMessage(entity);
        if (!createdInboxResp.isPresent()) {
            LOG.error("Could not create inbox message for event {}", event.getEventId());
            return;
        }

        final NotifyChangeSessionStatusEvent notifyEvent = new NotifyChangeSessionStatusEvent();
        notifyEvent.setSubmittedAt(System.currentTimeMillis());
        notifyEvent.setEventId(UUID.randomUUID().toString());
        notifyEvent.setInboxMessageUuid(createdInboxResp.get().getId());
        
        notifyEvent.setEndUserUuid(euUuid);
        notifyEvent.setTrainerUuid(ptUuid);
        notifyEvent.setSessionUuid(null);
        notifyEvent.setSessionDate(null);

        notifyEvent.setNewSessionStatus(SessionStatus.OPEN.toString());
        notifyEvent.setOldSessionStatus(SessionStatus.OPEN.toString());
        notifyHandler.publish(notifyEvent);
        LOG.info("Fired notify event for new client for the session package {}", packageUuid);
        
        LOG.info("Created inbox message for event {}", event.getEventId());
    }
}
