package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.common.event.email.SubmitDocumentAdminEvent;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SendSubmitDocumentToAdminService {

    private static final Logger LOG = LoggerFactory.getLogger(SendSubmitDocumentToAdminService.class);

    private static final String SUBMIT_DOCUMENT_TEMPLATE = "SubmitDocumentEmailTemplate";
    private static final String SUBMIT_DOCUMENT_TITLE = "email.submit.document.title";
    private static final String QUERY_ADMIN_EMAIL = "select email";

    private final MailService mailService;
    private final UserClient userClient;
    private final NotificationProperties notificationProperties;
    private final MailAssetProperties mailAssetProperties;

    public SendSubmitDocumentToAdminService(
            final MailService mailService,
            final UserClient userClient,
            final NotificationProperties notificationProperties,
            final MailAssetProperties mailAssetProperties) {
        this.mailService = mailService;
        this.userClient = userClient;
        this.notificationProperties = notificationProperties;
        this.mailAssetProperties = mailAssetProperties;
    }

    public void handle(final SubmitDocumentAdminEvent message) {
        LOG.info("Handle submit document message");
        final String userId = message.getUserId().toString();

        MailUtil.findUser(userClient, userId).ifPresent(user -> {

            final Context context = MailUtil.createContext(user, notificationProperties, mailAssetProperties);

            final UserGridResponse<UserEntity> listAdminResp = userClient.getEmailAdmins(QUERY_ADMIN_EMAIL);
            if (listAdminResp.getEntities().isEmpty()) {
                LOG.error("Could not found any Admin!");
                return;
            }

            final List<String> listEmailAdmin =
                    listAdminResp.getEntities()
                            .stream().map(UserEntity::getEmail).collect(Collectors.toList());

            mailService.sendEmail(notificationProperties.getMail().getFrom(),
                    StringUtils.join(listEmailAdmin, ","), SUBMIT_DOCUMENT_TEMPLATE,
                    SUBMIT_DOCUMENT_TITLE, context, false);
        });
    }
}
