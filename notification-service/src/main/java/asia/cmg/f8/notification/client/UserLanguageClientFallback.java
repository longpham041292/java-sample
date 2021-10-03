package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 2/7/17.
 */
@Component
public class UserLanguageClientFallback implements UserLanguageClient {

    @Override
    public UserGridResponse<BasicUserInfo> findUserLanguage(@PathVariable("uuid") final String userUuid) {
        return null;
    }
}
