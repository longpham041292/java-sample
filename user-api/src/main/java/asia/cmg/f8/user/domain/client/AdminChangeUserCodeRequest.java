package asia.cmg.f8.user.domain.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminChangeUserCodeRequest {

	 private String newUserCode;
	    private String uuid;

	    @JsonProperty("new_usercode")
	    public String getNewUserCode() {
	        return newUserCode;
	    }

	    public void setNewUsername(final String newUserCode) {
	        this.newUserCode = newUserCode;
	    }

	    @JsonProperty("uuid")
		public String getUuid() {
			return uuid;
		}

		public void setUuid(final String uuid) {
			this.uuid = uuid;
			
		}			
	
}
