package asia.cmg.f8.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 1/5/17.
 */
@Component
@ConfigurationProperties(prefix = "notifier")
public class NotifierProperties {

    private String iOsNotifier;
    private String androidNotifier;

    public String getiOsNotifier() {
        return iOsNotifier;
    }

    public void setiOsNotifier(final String iOsNotifier) {
        this.iOsNotifier = iOsNotifier;
    }

    public String getAndroidNotifier() {
        return androidNotifier;
    }

    public void setAndroidNotifier(final String androidNotifier) {
        this.androidNotifier = androidNotifier;
    }
}
