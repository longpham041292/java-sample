package asia.cmg.f8.user.domain.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/5/17.
 */
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    @JsonProperty("old_password")
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @JsonProperty("new_password")
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
}
