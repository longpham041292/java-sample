package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11/11/16.
 */
public class UserGridFailFast implements InitializingBean {

    private final UserGridApplicationValidation applicationAccessToken;
    private final UserGridProperties userGridProperties;

    public UserGridFailFast(final UserGridApplicationValidation applicationAccessToken, final UserGridProperties userGridProperties) {
        this.applicationAccessToken = applicationAccessToken;
        this.userGridProperties = userGridProperties;
    }

    @Override
    @SuppressWarnings("PMD")
    public void afterPropertiesSet() throws Exception {
        final Map<String, String> content = new HashMap<>(3);
        content.put("grant_type", "client_credentials");
        content.put("client_id", userGridProperties.getClientId());
        content.put("client_secret", userGridProperties.getClientSecret());

        // try to validate client_id and client secret.
        applicationAccessToken.validateUserGridConfiguration(content);
    }
}
