package asia.cmg.f8.notification.push;

import asia.cmg.f8.notification.client.UserLanguageClient;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created on 2/7/17.
 */
@Service
public class DefaultUserInfoService implements UserInfoService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserInfoService.class);

    @Autowired
    private UserLanguageClient userLanguageClient;

    @Override
    @Cacheable(cacheNames = "userLocaleCache", key = "#uuid")
    public Locale getLocale(final String uuid) {

        Locale locale = Locale.ENGLISH;
        final UserGridResponse<BasicUserInfo> userInfo = userLanguageClient.findUserLanguage(uuid);

        if (userInfo != null && userInfo.getEntities() != null && !userInfo.getEntities().isEmpty()) {
            final BasicUserInfo user = userInfo.getEntities().iterator().next();
            final String language = user.getLanguage();
            if (language != null) {
                locale = new Locale(language);

                LOGGER.info("Loaded \"{}\" locale of user {}", locale, uuid);
            }
        }
        return locale;
    }

    @Override
    @CacheEvict(cacheNames = "userLocaleCache", key = "#uuid")
    public void reloadLocale(final String uuid) {
        LOGGER.info("Re-loaded locale of user {}", uuid);
    }
}
