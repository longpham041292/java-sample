package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

/**
 * Created on 1/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationCounterRequest {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("counters")
    private Map<String, Object> counters;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getCounters() {
        return counters;
    }

    public void setCounters(final Map<String, Object> counters) {
        this.counters = counters;
    }

    /**
     * Convenience method to create counter request.
     *
     * @param userUuid the user's uuid
     * @param count    the count value
     * @return the counter request
     */
    public static NotificationCounterRequest create(final String userUuid, final int count) {
        final NotificationCounterRequest counter = new NotificationCounterRequest();
        counter.setTimestamp(System.currentTimeMillis());
        counter.setCounters(Collections.singletonMap(buildCounterName(userUuid), count));
        return counter;
    }

    public static String buildCounterName(final String uuid) {
        return "unread_notifications_" + uuid;
    }
}
