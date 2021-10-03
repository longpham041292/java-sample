package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferBookedSessionRequest {
	
	@JsonProperty("session_id")
	private Long sessionId;
	
	@JsonProperty("new_trainer_uuid")
	private String newTrainerUuid;

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getNewTrainerUuid() {
		return newTrainerUuid;
	}

	public void setNewTrainerUuid(String newTrainerUuid) {
		this.newTrainerUuid = newTrainerUuid;
	}
}
