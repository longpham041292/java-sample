package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.common.profile.CompleteProfileEvent;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class ResumeRegistrationEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeRegistrationEmailService.class);
    private static final String RESUME_REGISTRATION_TEMPLATE = "PTResumeRegistration";
    private static final String RESUME_REGISTRATION_TITLE_PT = "email.registration.resume.title.pt";
    private static final String RESUME_REGISTRATION_TITLE_CLIENT = "email.registration.resume.title.client";

    private final MailService mailService;
    private final UserClient userClient;
    private final NotificationProperties notificationProperties;
    private final MailAssetProperties mailAssetProperties;

    public ResumeRegistrationEmailService(final MailService mailService,
                                          final UserClient userClient,
                                          final NotificationProperties notificationProperties,
                                          final MailAssetProperties mailAssetProperties) {
        this.mailService = mailService;
        this.userClient = userClient;
        this.notificationProperties = notificationProperties;
        this.mailAssetProperties = mailAssetProperties;
    }

    public void handle(final CompleteProfileEvent message) {

        final String userId = message.getUserId().toString();
        LOGGER.info("Handle CompleteProfileEvent for user {}", userId);
        MailUtil.findUser(userClient, userId).ifPresent(user -> {
            final Context context = MailUtil.createContext(user, notificationProperties, mailAssetProperties);
            
            final String title = user.getUserType() == UserType.PT ? RESUME_REGISTRATION_TITLE_PT : RESUME_REGISTRATION_TITLE_CLIENT;
            
            mailService.sendEmail(notificationProperties.getMail().getFrom(), user.getEmail(),
                    RESUME_REGISTRATION_TEMPLATE, title, context, false);
            LOGGER.info("Sent email to user {}", userId);
        });
    }
}
