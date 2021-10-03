package asia.cmg.f8.user.domain.client;

/**
 * Created on 1/5/17.
 */
public class ResetUserPasswordRequest {
	private String uuid;
	private String password;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
