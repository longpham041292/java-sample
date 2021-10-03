package asia.cmg.f8.session.dto;

import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.session.SessionTrainingStyle;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;

public class CreditBookingSessionDTO {

	@JsonProperty("credit_amount")
	private Integer creditAmount = 0;
	
	@JsonProperty("booking_studio_uuid")
	private String bookingStudioUuid;
	
	@JsonProperty("booking_studio_name")
	private String bookingStudioName;
	
	@JsonProperty("booking_studio_address")
	private String bookingStudioAddress;
	
	@JsonProperty("start_time")
	private Long startTime;
	
	@JsonProperty("end_time")
	private Long endTime;
	
	@JsonProperty("training_style")
	private SessionTrainingStyle trainingStyle;
	
	@JsonProperty("status")
	private CreditBookingSessionStatus status;
	
	public CreditBookingSessionDTO(CreditSessionBookingEntity sessionEntity) {
		this.bookingStudioAddress = sessionEntity.getBookingStudioAddress();
		this.bookingStudioName = sessionEntity.getBookingStudioName();
		this.bookingStudioUuid = sessionEntity.getBookingStudioUuid();
		this.creditAmount = sessionEntity.getCreditAmount();
		this.endTime = sessionEntity.getCreditBooking().getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond();
		this.startTime = sessionEntity.getCreditBooking().getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond();
		this.status = sessionEntity.getStatus();
		this.trainingStyle = sessionEntity.getTrainingStyle();
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
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

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public SessionTrainingStyle getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(SessionTrainingStyle trainingStyle) {
		this.trainingStyle = trainingStyle;
	}

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}
}
