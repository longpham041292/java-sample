package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditClassBookingDirectlyRequest {

	@JsonProperty("overlapped_booking_id")
	private Long overlappedBookingId = 0L;
	
	@JsonProperty("studio_uuid")
	@NotNull
	private String studioUuid;
	
	@JsonProperty("class_id")
	@NotNull
	private long classId;
	
	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public long getClassId() {
		return classId;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}

	public Long getOverlappedBookingId() {
		return overlappedBookingId;
	}

	public void setOverlappedBookingId(Long overlappedBookingId) {
		this.overlappedBookingId = overlappedBookingId;
	}
}
