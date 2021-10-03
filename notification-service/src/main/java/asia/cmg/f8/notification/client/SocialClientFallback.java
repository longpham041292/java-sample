package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 1/13/17.
 */
@Component
public class SocialClientFallback implements SocialClient {

    @Override
    public UserGridResponse<SocialUserInfo> getFollowers(
            @PathVariable("uuid") final String uuid,
            @PathVariable("query") final String query) {
        return null;
    }
}
