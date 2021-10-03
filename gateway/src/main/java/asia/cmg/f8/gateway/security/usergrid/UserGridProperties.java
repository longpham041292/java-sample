package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 11/11/16.
 */
@ConfigurationProperties("usergrid")
public class UserGridProperties {

    private String clientId;
    private String clientSecret;
    private Long accessTokenTtl; // Access token time-to-live in milliseconds

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(final Long accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }
}
