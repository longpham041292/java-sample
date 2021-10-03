package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.common.event.user.AdminApprovedDocumentEvent;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class SendApproveDocumentToPtService {

    private static final Logger LOG = LoggerFactory.getLogger(SendApproveDocumentToPtService.class);

    private static final String APPROVE_DOCUMENT_TEMPLATE = "ApproveDocumentEmailTemplate";
    private static final String APPROVE_DOCUMENT_TITLE = "email.approve.document.title";

    private final MailService mailService;
    private final UserClient userClient;
    private final NotificationProperties notificationProperties;
    private final MailAssetProperties mailAssetProperties;

    public SendApproveDocumentToPtService(
            final MailService mailService,
            final UserClient userClient,
            final NotificationProperties notificationProperties,
            final MailAssetProperties mailAssetProperties) {
        this.mailService = mailService;
        this.userClient = userClient;
        this.notificationProperties = notificationProperties;
        this.mailAssetProperties = mailAssetProperties;
    }

    public void handle(final AdminApprovedDocumentEvent message) {
        LOG.info("Handle send email approve document to PT");
        final String userId = message.getTrainerUuid().toString();

        MailUtil.findUser(userClient, userId).ifPresent(user -> {

            final Context context = MailUtil.createContext(user, notificationProperties, mailAssetProperties);

            mailService.sendEmail(notificationProperties.getMail().getFrom(),
                    user.getEmail(), APPROVE_DOCUMENT_TEMPLATE,
                    APPROVE_DOCUMENT_TITLE, context, false);
        });
    }
}
