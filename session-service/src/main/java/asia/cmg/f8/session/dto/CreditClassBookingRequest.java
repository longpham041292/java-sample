package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditClassBookingRequest {

	@JsonProperty("overlapped_booking_id")
	private Long overlappedBookingId = 0L;
	
	@JsonProperty("client_uuid")
	@NotNull
	private String clientUuid;
	
	@JsonProperty("studio_uuid")
	@NotNull
	private String studioUuid;
	
	@JsonProperty("class_id")
	@NotNull
	private long classId;
	
	@JsonProperty("booking_day")
	@NotNull
	private long bookingDay;

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

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

	public long getBookingDay() {
		return bookingDay;
	}

	public void setBookingDay(long bookingDay) {
		this.bookingDay = bookingDay;
	}

	public Long getOverlappedBookingId() {
		return overlappedBookingId;
	}

	public void setOverlappedBookingId(Long overlappedBookingId) {
		this.overlappedBookingId = overlappedBookingId;
	}
}
