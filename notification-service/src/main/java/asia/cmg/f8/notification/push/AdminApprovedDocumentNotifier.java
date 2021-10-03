package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.event.user.AdminApprovedDocumentEvent;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 1/9/17.
 */
@Component
public class AdminApprovedDocumentNotifier extends NotificationSender {

    public static final Logger LOG = LoggerFactory.getLogger(AdminApprovedDocumentNotifier.class);

    public static final String DOCUMENT_TYPE = "document";
    public static final String MESSAGE_DOCUMENT_APPROVAL_MSG = "message.document.approval";
    private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();

    @Autowired
    @Qualifier("approvedDocumentEventConverter")
    private MessageConverter messageConverter;

    @StreamListener(NotificationEventStream.ADMIN_APPROVED_DOC_INPUT_CHANNEL)
    public void onEvent(final Message message) {

        final AdminApprovedDocumentEvent event = (AdminApprovedDocumentEvent) messageConverter
                .fromMessage(message, AdminApprovedDocumentEvent.class);

        if (event != null) {
            final String ptUuid = event.getTrainerUuid().toString();
            sendToUser(ptUuid, createMessage(), NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.APPROVE_DOC.name());
            LOG.info("Push notification for The F8 admin documents of Trainer {}", ptUuid);
        }
    }

    private PushMessage createMessage() {
        final PushMessage pushMessage = new PushMessage(DOCUMENT_TYPE);
        pushMessage.setLocalizedMessage(MESSAGE_DOCUMENT_APPROVAL_MSG);
        return pushMessage;
    }
}
