package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Re-present a required information to link user.
 * <p>
 * Created on 1/23/17.
 */
public class ResetUserPassword {

    private String newPassword;

	@JsonProperty("newpassword")
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
