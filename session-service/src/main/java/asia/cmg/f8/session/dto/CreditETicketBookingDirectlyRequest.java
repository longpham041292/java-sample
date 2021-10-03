package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditETicketBookingDirectlyRequest {

	@JsonProperty("studio_uuid")
	@NotNull
	private String studioUuid;
	
	@JsonProperty("eticket_id")
	@NotNull
	private long eticketId;
	
	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public long getEticketId() {
		return eticketId;
	}

	public void setEticketId(long eticketId) {
		this.eticketId = eticketId;
	}
}
