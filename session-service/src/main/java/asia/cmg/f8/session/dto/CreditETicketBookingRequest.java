package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditETicketBookingRequest {

	@JsonProperty("overlapped_booking_id")
	private Long overlappedBookingId = 0L;
	
	@JsonProperty("client_uuid")
	@NotNull
	private String clientUuid;
	
	@JsonProperty("studio_uuid")
	@NotNull
	private String studioUuid;
	
	@JsonProperty("eticket_id")
	@NotNull
	private long eticketId;
	
	@JsonProperty("booking_day")
	@NotNull
	private long bookingDay;

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

	public long getBookingDay() {
		return bookingDay;
	}

	public void setBookingDay(long bookingDay) {
		this.bookingDay = bookingDay;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public Long getOverlappedBookingId() {
		return overlappedBookingId;
	}

	public void setOverlappedBookingId(Long overlappedBookingId) {
		this.overlappedBookingId = overlappedBookingId;
	}
}
