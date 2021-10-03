package asia.cmg.f8.notification.push;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2/7/17.
 */
@Component
@ConfigurationProperties(value = "cache.push")
public class CacheProperties {

    private int maximumSize;
    private int expireInSeconds;

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public int getExpireInSeconds() {
        return expireInSeconds;
    }

    public void setExpireInSeconds(final int expireInSeconds) {
        this.expireInSeconds = expireInSeconds;
    }
}
