package asia.cmg.f8.gateway.security.linkaccount;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 1/9/17.
 */
@Component
public class LinkedTokenClientFallback implements LinkedTokenClient {

    @Override
    public AccessTokenResponse switchUser(@PathVariable("uuid") final String linkedUserUuid, @PathVariable("accessToken") final String accessToken, @PathVariable("ttl") final long ttl) {
        return null;
    }
}
