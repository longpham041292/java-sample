package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created on 2/10/17.
 */
public final class MailUtil {

    private static final String USER = "user";
    private static final String URL = "url";
    private static final String LOGO_URL = "logoUrl";
    private static final String SPACER_URL = "spacerUrl";

    private MailUtil() {
        // empty
    }

    /**
     * Get locale of given user.
     *
     * @param user            the user
     * @param defaultLanguage the default language to get locale
     * @return {@link Locale}
     */
    public static Locale getUserLocale(final UserEntity user, final String defaultLanguage) {
        final String language = StringUtils.isEmpty(user.getLanguage()) ? defaultLanguage : user.getLanguage();
        return Locale.forLanguageTag(language);
    }

    public static Optional<UserEntity> findUser(final UserClient userClient, final String userId) {

        final UserGridResponse<UserEntity> userResp = userClient.getUserByUuid(userId);
        final List<UserEntity> entities = userResp.getEntities();

        if (entities == null || entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(entities.iterator().next());
    }

    public static Context createContext(final UserEntity user, final NotificationProperties notificationProperties, final MailAssetProperties mailAssetProperties) {

        final Locale locale = MailUtil.getUserLocale(user, notificationProperties.getDefaultLang());
        final Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(URL, notificationProperties.getWebPortal());
        context.setVariable(LOGO_URL, mailAssetProperties.getLogoUrl());
        context.setVariable(SPACER_URL, mailAssetProperties.getSpacerUrl());
        return context;
    }
}
