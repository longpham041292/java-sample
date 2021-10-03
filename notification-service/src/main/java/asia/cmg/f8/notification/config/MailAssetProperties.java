package asia.cmg.f8.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 11/30/16.
 */
@Component
@ConfigurationProperties(prefix = "mailasset")
public class MailAssetProperties {
    private String logoUrl;
    private String spacerUrl;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(final String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSpacerUrl() {
        return spacerUrl;
    }

    public void setSpacerUrl(final String spacerUrl) {
        this.spacerUrl = spacerUrl;
    }
}
