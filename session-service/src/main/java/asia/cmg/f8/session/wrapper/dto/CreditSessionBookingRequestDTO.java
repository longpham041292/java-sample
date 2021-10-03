package asia.cmg.f8.session.wrapper.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.dto.TimeSlot;

public class CreditSessionBookingRequestDTO {

	@JsonProperty("user_uuid")
	@NotNull
	private String userUuid;

	@JsonProperty("pt_uuid")
	@NotNull
	private String ptUuid;

	@JsonProperty("time_slots")
	private Set<TimeSlot> timeSlots;

	@JsonProperty("booking_studio_uuid")
	private String bookingStudioUuid;

	@JsonProperty("booking_studio_name")
	private String bookingStudioName;

	@JsonProperty("booking_studio_address")
	private String bookingStudioAddress;

	@JsonProperty("training_style")
	@NotNull
	private Integer trainingStyle;

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	public Set<TimeSlot> getTimeSlots() {
		return timeSlots;
	}

	public void setTimeSlots(Set<TimeSlot> timeSlots) {
		this.timeSlots = timeSlots;
	}

	public String getBookingStudioUuid() {
		return bookingStudioUuid;
	}

	public void setBookingStudioUuid(String bookingStudioUuid) {
		this.bookingStudioUuid = bookingStudioUuid;
	}

	public String getBookingStudioName() {
		return bookingStudioName;
	}

	public void setBookingStudioName(String bookingStudioName) {
		this.bookingStudioName = bookingStudioName;
	}

	public String getBookingStudioAddress() {
		return bookingStudioAddress;
	}

	public void setBookingStudioAddress(String bookingStudioAddress) {
		this.bookingStudioAddress = bookingStudioAddress;
	}

	public Integer getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(Integer trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
}
