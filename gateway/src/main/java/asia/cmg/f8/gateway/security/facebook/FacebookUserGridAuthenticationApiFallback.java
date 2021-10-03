package asia.cmg.f8.gateway.security.facebook;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created on 11/3/16.
 */
@Component
public class FacebookUserGridAuthenticationApiFallback implements FacebookUserGridAuthenticationApi {

    @Override
    public Map<String, Object> authenticate(final String fbToken, final long ttl) {
        return null;
    }
}
