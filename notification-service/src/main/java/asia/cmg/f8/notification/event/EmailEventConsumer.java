package asia.cmg.f8.notification.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.event.email.ResumeRegisterEmailEvent;
import asia.cmg.f8.common.event.email.SubmitDocumentAdminEvent;
import asia.cmg.f8.common.event.user.AdminApprovedDocumentEvent;
import asia.cmg.f8.common.profile.CompleteProfileEvent;
import asia.cmg.f8.notification.service.email.ResumeRegistrationEmailService;
import asia.cmg.f8.notification.service.email.SendApproveDocumentToPtService;
import asia.cmg.f8.notification.service.email.SendSubmitDocumentToAdminService;
import asia.cmg.f8.notification.service.email.TransferSessionPackageConfirmationEmailService;
import asia.cmg.f8.session.TransferSessionPackageEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by tri.bui on 10/25/16.
 */
@Component
@EnableBinding(EmailEventStream.class)
public class EmailEventConsumer {

    private final ResumeRegistrationEmailService resumeRegistrationEmailService;
    private final SendSubmitDocumentToAdminService sendSubmitDocumentToAdminService;
    private final TransferSessionPackageConfirmationEmailService transferSessionSessionService;
    private final SendApproveDocumentToPtService sendApproveDocumentToPtService;

    @Inject
    private AvroSchemaMessageConverter completeProfileEventConverter;

    @Inject
    private AvroSchemaMessageConverter submitDocumentEventConverter;

    @Inject
    private AvroSchemaMessageConverter transferSessionsEventConverter;

    @Inject
    private AvroSchemaMessageConverter approvedDocumentEventConverter;

    @Inject
    public EmailEventConsumer(final ResumeRegistrationEmailService resumeRegistrationEmailService,
                              final SendSubmitDocumentToAdminService sendSubmitDocumentToAdminService,
                              final TransferSessionPackageConfirmationEmailService transferSessionSessionService,
                              final SendApproveDocumentToPtService sendApproveDocumentToPtService) {
        this.resumeRegistrationEmailService = resumeRegistrationEmailService;
        this.sendSubmitDocumentToAdminService = sendSubmitDocumentToAdminService;
        this.transferSessionSessionService = transferSessionSessionService;
        this.sendApproveDocumentToPtService = sendApproveDocumentToPtService;
    }

    /**
     * Handle complete profile event.
     *
     * @param message Consume message
     */
    @StreamListener(EmailEventStream.COMPLETE_PROFILE_CHANNEL)
    public void handleCompleteProfileEvent(final Message<?> message) {
        final CompleteProfileEvent event =
                (CompleteProfileEvent) completeProfileEventConverter.fromMessage(
                        message, ResumeRegisterEmailEvent.class);
        resumeRegistrationEmailService.handle(event);
    }

    /**
     * Handle submit document event.
     *
     * @param message Consume message
     */
    @StreamListener(EmailEventStream.SUBMIT_DOCUMENT_CHANNEL)
    public void handleSubmitDocumentEvent(final Message<?> message) {
        final SubmitDocumentAdminEvent event =
                (SubmitDocumentAdminEvent) submitDocumentEventConverter.fromMessage(
                        message, SubmitDocumentAdminEvent.class);
        sendSubmitDocumentToAdminService.handle(event);
    }

    /**
     * Handle Transfer Sessions complete event.
     *
     * @param message Consume message
     */
    @StreamListener(EmailEventStream.TRANSFER_COMPLETED_CHANNEL)
    public void handleTransferSessionCompletedEvent(final Message<?> message) {
        final TransferSessionPackageEvent event =
                (TransferSessionPackageEvent) transferSessionsEventConverter.fromMessage(
                        message, OrderCompletedEvent.class);
        transferSessionSessionService.handle(event);
    }

    @StreamListener(EmailEventStream.EMAIL_APPROVE_DOCUMENT_CHANNEL)
    public void handleEmailApproveDocument(final Message<?> message) {
        final AdminApprovedDocumentEvent event =
                (AdminApprovedDocumentEvent) approvedDocumentEventConverter
                        .fromMessage(message, AdminApprovedDocumentEvent.class);
        
        sendApproveDocumentToPtService.handle(event);
    }

}
