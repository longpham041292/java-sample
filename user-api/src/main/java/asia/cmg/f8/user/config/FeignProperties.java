package asia.cmg.f8.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 11/24/16.
 */
@Component
@ConfigurationProperties(prefix = "feign")
public class FeignProperties {

    private Integer connectTimeoutMillis;
    private Integer readTimeoutMillis;

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(final Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(final Integer readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
}
