package asia.cmg.f8.user.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created on 1/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponseUserApi {

    private Map<String, Object> payloads;
    private String state;
    private String receiver;
    private long created;

    public Map<String, Object> getPayloads() {
        return payloads;
    }

    public void setPayloads(final Map<String, Object> payloads) {
        this.payloads = payloads;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(final String receiver) {
        this.receiver = receiver;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(final long created) {
        this.created = created;
    }
    
}
