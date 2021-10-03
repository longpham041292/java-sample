package asia.cmg.f8.session.dto;

import asia.cmg.f8.session.entity.BasicUserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.utils.SessionUtil;

public class CreditClassBookingResponse {

	private long id;

	@JsonProperty("client_uuid")
	private String clientUuid;

	@JsonProperty("client_name")
	private String clientName;

	@JsonProperty("client_picture")
	private String clientPicture;

	@JsonProperty("class_name")
	private String className;

	@JsonProperty("booking_day")
	private long bookingDay;

	@JsonProperty("start_time")
	private long startTime;

	@JsonProperty("end_time")
	private long endTime;

	private int status;

	@JsonProperty("studio_uuid")
	private String studioUuid;

	@JsonProperty("studio_name")
	private String studioName;

	@JsonProperty("studio_address")
	private String studioAddress;

	@JsonProperty("studio_picture")
	private String studioPicture;

	@JsonProperty("confirmation_code")
	private String confirmationCode;
	
	@JsonProperty("studio_cover")
	private String studioCover;

	public CreditClassBookingResponse() {
		// TODO Auto-generated constructor stub
	}

	public CreditClassBookingResponse(CreditBookingEntity bookingEntity) {
		int complete = 7;
		int deduct = 8;
		this.bookingDay = SessionUtil.convertDateToEpochMiliSecond(bookingEntity.getBookingDay());
		this.className = bookingEntity.getClasses().get(0).getServiceName();
		this.clientUuid = bookingEntity.getClientUuid();
		this.endTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getEndTime());
		this.id = bookingEntity.getId();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getStartTime());
		this.status = bookingEntity.getStatus().ordinal() == deduct ? complete : bookingEntity.getStatus().ordinal();
		this.studioUuid = bookingEntity.getStudioUuid();
		this.studioAddress = bookingEntity.getStudioAddress();
		this.studioName = bookingEntity.getStudioName();
		this.studioPicture = bookingEntity.getStudioPicture();
		this.confirmationCode = bookingEntity.getConfirmationCode();
		this.studioCover = bookingEntity.getStudioCover();
	}

	public CreditClassBookingResponse(CreditBookingEntity bookingEntity, BasicUserEntity clientEntity) {
		this.bookingDay = SessionUtil.convertDateToEpochMiliSecond(bookingEntity.getBookingDay());
		this.className = bookingEntity.getClasses().get(0).getServiceName();
		this.clientUuid = bookingEntity.getClientUuid();
		this.clientPicture = clientEntity.getAvatar();
		this.clientName = clientEntity.getFullName();
		this.endTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getEndTime());
		this.id = bookingEntity.getId();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getStartTime());
		this.status = bookingEntity.getStatus().ordinal();
		this.studioUuid = bookingEntity.getStudioUuid();
		this.studioAddress = bookingEntity.getStudioAddress();
		this.studioName = bookingEntity.getStudioName();
		this.studioPicture = bookingEntity.getStudioPicture();
		this.confirmationCode = bookingEntity.getConfirmationCode();
		this.studioCover = bookingEntity.getStudioCover();
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPicture() {
		return clientPicture;
	}

	public void setClientPicture(String clientPicture) {
		this.clientPicture = clientPicture;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getBookingDay() {
		return bookingDay;
	}

	public void setBookingDay(long bookingDay) {
		this.bookingDay = bookingDay;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public String getStudioAddress() {
		return studioAddress;
	}

	public void setStudioAddress(String studioAddress) {
		this.studioAddress = studioAddress;
	}

	public String getStudioPicture() {
		return studioPicture;
	}

	public void setStudioPicture(String studioPicture) {
		this.studioPicture = studioPicture;
	}

	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public String getStudioCover() {
		return studioCover;
	}

	public void setStudioCover(String studioCover) {
		this.studioCover = studioCover;
	}
}
