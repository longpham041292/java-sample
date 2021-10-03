package asia.cmg.f8.report.dto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.BasicUserEntity;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;
import asia.cmg.f8.report.entity.database.CreditClassBookingEntity;
import asia.cmg.f8.report.entity.database.CreditSessionBookingEntity;


public class BookingServiceDTO {
	
	@JsonProperty(value = "pt_uuid")
	private String ptUuid;
	
	@JsonProperty(value = "pt_name")
	private String ptName;
	
	@JsonProperty(value = "pt_avatar")
	private String ptAvatar;
	
	@JsonProperty(value = "pt_username")
	private String ptUsername;
	
	@JsonProperty(value = "pt_phone")
	private String ptPhone;
	
	@JsonProperty(value = "studio_uuid")
	private String studioUuid;
	
	@JsonProperty(value = "studio_name")
	private String studioName;
	
	@JsonProperty(value = "studio_avatar")
	private String studioAvatar;
	
	@JsonProperty(value = "class_service")
	private String classService;
	
	@JsonProperty(value = "client_name")
	private String clientName;
	
	@JsonProperty(value = "client_uuid")
	private String clientUuid;
	
	@JsonProperty(value = "booking_time")
	private Long bookingTime;
	
	@JsonProperty(value = "created_time")
	private Long createdTime;
	
	@JsonProperty(value = "location")
	private String location;
	
	@JsonProperty(value = "credit")
	private Integer credit;
	
	@JsonProperty(value = "status")
	private String status;
	
	@JsonProperty(value = "booking_time_format")
	private String bookingTimeFormat;
	
	@JsonProperty(value = "create_time_format")
	private String createdTimeFormat;

	
	public BookingServiceDTO() {
		super();
	}
	
	public BookingServiceDTO(CreditSessionBookingEntity entity, BasicUserEntity pt, BasicUserEntity eu) {
		final String DATE_PATTERN = "dd/MM/yyyy hh:mm:ss a";
		if(pt != null) {
			this.ptAvatar = pt.getAvatar();
			this.ptName = pt.getFullName();
			this.ptPhone = pt.getPhone();
			this.ptUsername = pt.getUserName();
			this.ptUuid = pt.getUuid();
		}
		if(eu != null) {
			this.clientName = eu.getFullName();
			this.clientUuid = eu.getUuid();
		}
		this.bookingTime = entity.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		this.createdTime = entity.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		
		
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		this.bookingTimeFormat = entity.getStartTime() == null ? ""
				: DATE_FORMATTER.format(entity.getStartTime());
		this.createdTimeFormat = entity.getCreatedDate() == null ? ""
				: DATE_FORMATTER.format(entity.getCreatedDate());
		
		this.location = entity.getBookingStudioAddress();
		this.credit = entity.getCreditAmount();
		this.status = entity.getStatus().toString();
	}
	
	public BookingServiceDTO(CreditBookingEntity entity, BasicUserEntity eu) {
		final String DATE_PATTERN = "dd/MM/yyyy hh:mm:ss a";
		if(entity != null) {
			this.studioAvatar = entity.getStudioPicture();
			this.studioName = entity.getStudioName();
			this.studioUuid = entity.getStudioUuid();
		}
		if(eu != null) {
			this.clientName = eu.getFullName();
			this.clientUuid = eu.getUuid();
		}
		if(!entity.getClasses().isEmpty()) {
			CreditClassBookingEntity classEntity = entity.getClasses().get(0);
			this.classService = classEntity.getServiceName();
		}
		this.bookingTime = entity.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		this.createdTime = entity.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		
		
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		this.bookingTimeFormat = entity.getStartTime() == null ? ""
				: DATE_FORMATTER.format(entity.getStartTime());
		this.createdTimeFormat = entity.getCreatedDate() == null ? ""
				: DATE_FORMATTER.format(entity.getCreatedDate());
		
		this.location = entity.getStudioAddress();
		this.credit = entity.getCreditAmount();
		this.status = entity.getStatus().toString();
	}

	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	public String getPtName() {
		return ptName;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	public String getPtAvatar() {
		return ptAvatar;
	}

	public void setPtAvatar(String ptAvatar) {
		this.ptAvatar = ptAvatar;
	}

	public String getPtUsername() {
		return ptUsername;
	}

	public void setPtUsername(String ptUsername) {
		this.ptUsername = ptUsername;
	}

	public String getPtPhone() {
		return ptPhone;
	}

	public void setPtPhone(String ptPhone) {
		this.ptPhone = ptPhone;
	}

	public String getClientName() {
		return clientName;
	}

	public void setUserName(String userFullname) {
		this.clientName = userFullname;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setUserUuid(String userUuid) {
		this.clientUuid = userUuid;
	}

	public Long getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Long bookingTime) {
		this.bookingTime = bookingTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer leepCoin) {
		this.credit = leepCoin;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public String getBookingTimeFormat() {
		return bookingTimeFormat;
	}

	public void setBookingTimeFormat(String bookingTimeFormat) {
		this.bookingTimeFormat = bookingTimeFormat;
	}

	public String getCreatedTimeFormat() {
		return createdTimeFormat;
	}

	public void setCreatedTimeFormat(String createTimeFormat) {
		this.createdTimeFormat = createTimeFormat;
	}

	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public String getStudioAvatar() {
		return studioAvatar;
	}

	public void setStudioAvatar(String studioAvatar) {
		this.studioAvatar = studioAvatar;
	}

	public String getClassService() {
		return classService;
	}

	public void setClassService(String classService) {
		this.classService = classService;
	}
	
}
