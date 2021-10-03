package asia.cmg.f8.session.wrapper.dto;

import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.utils.SessionUtil;

public class CreditSessionBookingResponseDTO {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("user_uuid")
    private String userUuid;
	
	@JsonProperty("pt_uuid")
    private String ptUuid;
	
	@JsonProperty("pt_level")
	private String ptLevel;
	
	@JsonProperty("user_info")
	private BasicUserInfo userInfo;
	
	@JsonProperty("trainer_info")
	private BasicUserInfo trainerInfo;
	
	@JsonProperty("status")
	private CreditBookingSessionStatus status;
	
	@JsonProperty("booked_by")
	private String bookedBy;
	
	@JsonProperty("credit_amount")
	private Integer creditAmount;
	
	@JsonProperty("start_time")
	private Long startTime;
	
	@JsonProperty("end_time")
	private Long endTime;
	
	@JsonProperty("booking_studio_uuid")
    private String bookingStudioUuid;
    
	@JsonProperty("booking_studio_name")
    private String bookingStudioName;
    
	@JsonProperty("booking_studio_address")
    private String bookingStudioAddress;
	
	@JsonProperty("training_style")
	private Integer trainingStyle;
	
	public CreditSessionBookingResponseDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public CreditSessionBookingResponseDTO(CreditBookingEntity entity, BasicUserInfo userInfo, BasicUserInfo trainerInfo) {
		CreditSessionBookingEntity sessionBookingEntity = entity.getSessions().get(0);
		this.bookedBy = sessionBookingEntity.getBookedBy();
		this.bookingStudioAddress = sessionBookingEntity.getBookingStudioAddress();
		this.bookingStudioName = sessionBookingEntity.getBookingStudioName();
		this.bookingStudioUuid = sessionBookingEntity.getBookingStudioUuid();
		this.creditAmount = entity.getCreditAmount();
		this.endTime = SessionUtil.convertDateTimeToEpochMiliSecond(entity.getEndTime());
		this.id = entity.getId();
		this.ptLevel = sessionBookingEntity.getPtLevel();
		this.ptUuid = sessionBookingEntity.getPtUuid();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(entity.getStartTime());
		this.status = entity.getStatus() == CreditBookingSessionStatus.DEDUCTED ? CreditBookingSessionStatus.COMPLETED : entity.getStatus();
		this.trainerInfo = trainerInfo;
		this.userInfo = userInfo;
		this.userUuid = sessionBookingEntity.getUserUuid();
		this.trainingStyle = sessionBookingEntity.getTrainingStyle().ordinal();
		this.uuid = sessionBookingEntity.getUuid();
	}
	
	public static CreditSessionBookingResponseDTO initObjectData(CreditBookingEntity entity, BasicUserInfo userInfo, BasicUserInfo trainerInfo) {
		CreditSessionBookingResponseDTO object = new CreditSessionBookingResponseDTO();
		
		CreditSessionBookingEntity sessionBookingEntity = entity.getSessions().get(0);
		object.bookedBy = sessionBookingEntity.getBookedBy();
		object.bookingStudioAddress = sessionBookingEntity.getBookingStudioAddress();
		object.bookingStudioName = sessionBookingEntity.getBookingStudioName();
		object.bookingStudioUuid = sessionBookingEntity.getBookingStudioUuid();
		object.creditAmount = entity.getCreditAmount();
		object.endTime = SessionUtil.convertDateTimeToEpochSecond(entity.getEndTime());
		object.id = entity.getId();
		object.ptLevel = sessionBookingEntity.getPtLevel();
		object.ptUuid = sessionBookingEntity.getPtUuid();
		object.startTime = SessionUtil.convertDateTimeToEpochSecond(entity.getStartTime());
		object.status = entity.getStatus();
		object.trainerInfo = trainerInfo;
		object.userInfo = userInfo;
		object.userUuid = sessionBookingEntity.getUserUuid();
		object.trainingStyle = sessionBookingEntity.getTrainingStyle().ordinal();
		object.uuid = sessionBookingEntity.getUuid();
		
		return object;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getPtLevel() {
		return ptLevel;
	}

	public void setPtLevel(String ptLevel) {
		this.ptLevel = ptLevel;
	}

	public BasicUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(BasicUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public BasicUserInfo getTrainerInfo() {
		return trainerInfo;
	}

	public void setTrainerInfo(BasicUserInfo trainerInfo) {
		this.trainerInfo = trainerInfo;
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

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(Integer trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
}
