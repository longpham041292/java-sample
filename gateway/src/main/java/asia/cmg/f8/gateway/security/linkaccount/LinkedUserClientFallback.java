package asia.cmg.f8.gateway.security.linkaccount;

import asia.cmg.f8.gateway.security.usergrid.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 1/9/17.
 */
@Component
public class LinkedUserClientFallback implements LinkedUserClient {

    @Override
    public UserGridResponse<LinkedUserInfo> findLinkedAccounts(@PathVariable("query") final String query, @PathVariable("accessToken") final String accessToken) {
        return null;
    }
}
