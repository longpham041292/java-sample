/**
 * 
 */
package asia.cmg.f8.user.domain.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author khoa.bui
 *
 */
public class AdminChangeUserNameRequest {
    private String newUsername;
    private String uuid;

    @JsonProperty("new_username")
    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(final String newUsername) {
        this.newUsername = newUsername;
    }

    @JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}
    
}