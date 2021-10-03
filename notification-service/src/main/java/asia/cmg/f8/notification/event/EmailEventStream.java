package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 10/21/16.
 */
public interface EmailEventStream {

    String COMPLETE_PROFILE_CHANNEL = "completeProfileIn";
    String SUBMIT_DOCUMENT_CHANNEL = "submitDocumentIn";
    String TRANSFER_COMPLETED_CHANNEL = "transferSessionCompletedInput";
    String EMAIL_APPROVE_DOCUMENT_CHANNEL = "emailAdminApprovedDocumentInput";

    @Input(COMPLETE_PROFILE_CHANNEL)
    SubscribableChannel resumeRegEmail();

    @Input(SUBMIT_DOCUMENT_CHANNEL)
    SubscribableChannel consumeSubmitDocument();

    @Input(TRANSFER_COMPLETED_CHANNEL)
    SubscribableChannel transferSessionCompleted();
    
    @Input(EMAIL_APPROVE_DOCUMENT_CHANNEL)
    SubscribableChannel emailApproveDocument();
}
