package asia.cmg.f8.user.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EventStream {

    String SUBMIT_DOCUMENT_CHANNEL = "submitDocumentOut";
    String USER_INFO_OUT_CHANNEL = "changeUserOut";
    
     String SIGN_UP_OUT_CHANNEL = "signUpUserOut";
    String SIGN_UP_IN_CHANNEL = "signUpUserIn";

    String USER_ACTIVATED_OUT_CHANNEL = "userActivatedOut";

    String USER_ACTIVE_DEACTIVATE_OUT_CHANNEL = "userActivateOrDeactivateOut";

    String ADMIN_APPROVED_DOC_OUT_CHANNEL = "adminApprovedDocumentOutput";
    
    String UNAPPROVED_DOC_OUT_CHANNEL = "unApprovedDocumentOutput";

    @Output(SUBMIT_DOCUMENT_CHANNEL)
    MessageChannel submitDocument();

    @Output(USER_INFO_OUT_CHANNEL)
    MessageChannel changeUserInfo();

    @Output(SIGN_UP_OUT_CHANNEL)
    MessageChannel signUpUser();

    @Input(SIGN_UP_IN_CHANNEL)
    SubscribableChannel handleSignUpUser();

    @Output(USER_ACTIVATED_OUT_CHANNEL)
    MessageChannel userActivated();

    @Output(USER_ACTIVE_DEACTIVATE_OUT_CHANNEL)
    MessageChannel userActiveOrDeactivate();

    @Output(ADMIN_APPROVED_DOC_OUT_CHANNEL)
    MessageChannel adminApprovedDocument();

    @Output(UNAPPROVED_DOC_OUT_CHANNEL)
    MessageChannel adminUnapprovedDocument();
}
