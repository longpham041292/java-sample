package asia.cmg.f8.user.domain.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/5/17.
 */
public class ChangeUsernameRequest {
    private String newUsername;

    @JsonProperty("new_username")
    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(final String newUsername) {
        this.newUsername = newUsername;
    }
}
