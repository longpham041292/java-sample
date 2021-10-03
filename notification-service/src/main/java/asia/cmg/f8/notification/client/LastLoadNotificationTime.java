package asia.cmg.f8.notification.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent a last load notification time of given user.
 * <p>
 * Created on 1/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastLoadNotificationTime {

    public static final String LAST_LOAD_NOTIFICATION_TIME = "last_load_notification_time";
    public static final String FIND_QUERY = "select uuid,activated,last_load_notification_time";

    private String uuid;
    private boolean activated;

    @JsonProperty(LAST_LOAD_NOTIFICATION_TIME)
    private Long lastTime;

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(final Long lastTime) {
        this.lastTime = lastTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }
}
