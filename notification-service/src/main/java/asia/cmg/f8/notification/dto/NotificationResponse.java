package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Created on 1/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse {

    private Map<String, Object> payloads;
    private String state;
    private String receiver;
    private long created;
    private List<SocialUserInfo> tagged_accounts;
    
	public List<SocialUserInfo> getTagged_accounts() {
		return tagged_accounts;
	}

	public void setTagged_accounts(List<SocialUserInfo> tagged_accounts) {
		this.tagged_accounts = tagged_accounts;
	}

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
