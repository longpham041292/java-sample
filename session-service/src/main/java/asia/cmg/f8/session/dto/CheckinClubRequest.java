package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckinClubRequest {
	@JsonProperty("club_uuid")
	private String clubUuid;
	
	@JsonProperty("session_uuid")
	private String sessionUuid;

	public String getClubUuid() {
		return clubUuid;
	}

	public void setClubUuid(String clubUuid) {
		this.clubUuid = clubUuid;
	}

	public String getSessionUuid() {
		return sessionUuid;
	}

	public void setSessionUuid(String sessionUuid) {
		this.sessionUuid = sessionUuid;
	}
}
