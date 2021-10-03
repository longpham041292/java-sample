package asia.cmg.f8.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/22/16.
 */
public class AdminActivateRequest {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("active")
    private boolean active;

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
}
