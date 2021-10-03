package asia.cmg.f8.session.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.session.SessionTrainingStyle;
import asia.cmg.f8.session.entity.AbstractEntity;
import asia.cmg.f8.session.entity.BasicUserEntity;

@Entity
@Table(name = "credit_session_bookings", uniqueConstraints = @UniqueConstraint(name = "uuid_UNI", columnNames = {"uuid"}))
public class CreditSessionBookingEntity extends AbstractEntity {

	@ManyToOne
	@JoinColumn(name = "credit_booking_id")
	private CreditBookingEntity creditBooking;
	
	@Column(name = "user_uuid", length = 36, nullable = false)
    private String userUuid;
	
	@Column(name = "pt_uuid", length = 36, nullable = false)
    private String ptUuid;
	
	@Column(name = "pt_level", length = 20)
	private String ptLevel;
	
	@Column(name = "credit_amount", columnDefinition = "int not null default 0")
	private Integer creditAmount = 0;
	
	@JsonProperty("service_fee")
	@Column(name = "service_fee", columnDefinition = "double not null default 0")
	private Double serviceFee = 0d;
	
	@JsonProperty("service_burned_fee")
	@Column(name = "service_burned_fee", columnDefinition = "double not null default 0")
	private Double serviceBurnedFee = 0d;
	
	@Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private CreditBookingSessionStatus status = CreditBookingSessionStatus.BOOKED;
	
	@Column(name = "booked_by", length = 36)
    private String bookedBy; // supported values eu or pt
	
	@Column(name = "start_time")
    private LocalDateTime startTime; // the current start time. It's equal with start time of the current linked event.
	
	@Column(name = "end_time")
    private LocalDateTime endTime; // the current end time. It's equal with end time of the current linked event.
	
	@Column(name = "booking_studio_uuid", nullable = true, length = 100)
    private String bookingStudioUuid;
    
    @Column(name = "booking_studio_name", nullable = true, length = 255)
    private String bookingStudioName;
    
    @Column(name = "booking_studio_address", nullable = true, length = 1000)
    private String bookingStudioAddress;
    
    @Column(name = "training_style", columnDefinition = "int not null default 0")
    @Enumerated(EnumType.ORDINAL)
    private SessionTrainingStyle trainingStyle = SessionTrainingStyle.OFFLINE;
    
//    @ManyToOne
//    @JoinColumn(name = "pt_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
//    BasicUserEntity trainerInfo;

//	public BasicUserEntity getTrainerInfo() {
//		return trainerInfo;
//	}

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

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
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

	public String getPtLevel() {
		return ptLevel;
	}

	public void setPtLevel(String ptLevel) {
		this.ptLevel = ptLevel;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public SessionTrainingStyle getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(SessionTrainingStyle trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
	
	public CreditBookingEntity getCreditBooking() {
		return creditBooking;
	}

	public void setCreditBooking(CreditBookingEntity creditBooking) {
		this.creditBooking = creditBooking;
	}

	public Double getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(Double serviceFee) {
		this.serviceFee = serviceFee;
	}
	
	public Double getServiceBurnedFee() {
		return serviceBurnedFee;
	}

	public void setServiceBurnedFee(Double serviceBurnedFee) {
		this.serviceBurnedFee = serviceBurnedFee;
	}

	public String toString() {
		return String.format("{userUuid: %s, ptUuid: %s, bookedBy: %s, creditAmount: %s, startTime: %s, endTime: %s}", 
				userUuid,
				ptUuid,
				bookedBy,
				creditAmount,
				startTime,
				endTime);
	}
}
