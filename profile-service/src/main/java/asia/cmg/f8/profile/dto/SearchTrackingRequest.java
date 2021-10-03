package asia.cmg.f8.profile.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchTrackingRequest implements Serializable {
	private static final long serialVersionUID = 1828860975088672674L;
	
	@JsonProperty("user_uuid")
	public String userUuid;
	
	public String name;
	
	public String avatar;
	
	public String username;
	
	@JsonProperty("user_type")
	public String userType;
}
