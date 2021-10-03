package asia.cmg.f8.session.dto;

import asia.cmg.f8.session.dto.cms.ETicketOpenTimeDTO;
import asia.cmg.f8.session.entity.BasicUserEntity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.utils.SessionUtil;

public class CreditETicketBookingResponse {

	private long id;

	@JsonProperty("client_uuid")
	private String clientUuid;

	@JsonProperty("client_name")
	private String clientName;

	@JsonProperty("client_picture")
	private String clientPicture;

	@JsonProperty("eticket_id")
	private long eticketId;

	@JsonProperty("eticket_name")
	private String eticketName;

	@JsonProperty("booking_day")
	private long bookingDay;

	@JsonProperty("start_time")
	private long startTime;

	@JsonProperty("end_time")
	private long endTime;
	
	@JsonProperty("opening_hours")
	public List<ETicketOpenTimeDTO> openingHours = new ArrayList<ETicketOpenTimeDTO>();

	@JsonProperty("all_day")
	private boolean allDay;

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

	public CreditETicketBookingResponse() {
		// TODO Auto-generated constructor stub
	}

	public CreditETicketBookingResponse(CreditBookingEntity bookingEntity) {
		int complete = 7;
		int deduct = 8;
		this.bookingDay = SessionUtil.convertDateToEpochMiliSecond(bookingEntity.getBookingDay());;
		this.clientUuid = bookingEntity.getClientUuid();
		this.eticketId = bookingEntity.getEtickets().get(0).getServiceId();
		this.eticketName = bookingEntity.getEtickets().get(0).getServiceName();
		this.id = bookingEntity.getId();
		this.status = bookingEntity.getStatus().ordinal() == deduct ? complete : bookingEntity.getStatus().ordinal();
		this.studioUuid = bookingEntity.getStudioUuid();
		this.studioAddress = bookingEntity.getStudioAddress();
		this.studioName = bookingEntity.getStudioName();
		this.studioPicture = bookingEntity.getStudioPicture();
		this.allDay = bookingEntity.getEtickets().get(0).isAllDay();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getStartTime());
		this.endTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getEndTime());
		this.confirmationCode = bookingEntity.getConfirmationCode();
		this.studioCover = bookingEntity.getStudioCover();
		
		OpeningHour openingHour = (OpeningHour)SessionUtil.toObjectList(bookingEntity.getEtickets().get(0).getOpeningHours(), OpeningHour.class);
		if(openingHour != null) {
			this.openingHours = openingHour.getOpeningHours();
		}
	}

	public CreditETicketBookingResponse(CreditBookingEntity bookingEntity, BasicUserEntity clientEntity) {
		this.bookingDay = SessionUtil.convertDateToEpochMiliSecond(bookingEntity.getBookingDay());;
		this.clientUuid = bookingEntity.getClientUuid();
		this.clientPicture = clientEntity.getAvatar();
		this.clientName = clientEntity.getFullName();
		this.eticketId = bookingEntity.getEtickets().get(0).getServiceId();
		this.eticketName = bookingEntity.getEtickets().get(0).getServiceName();
		this.id = bookingEntity.getId();
		this.status = bookingEntity.getStatus().ordinal();
		this.studioUuid = bookingEntity.getStudioUuid();
		this.studioAddress = bookingEntity.getStudioAddress();
		this.studioName = bookingEntity.getStudioName();
		this.studioPicture = bookingEntity.getStudioPicture();
		this.allDay = bookingEntity.getEtickets().get(0).isAllDay();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getStartTime());
		this.endTime = SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getEndTime());
		this.confirmationCode = bookingEntity.getConfirmationCode();
		this.studioCover = bookingEntity.getStudioCover();
		
		OpeningHour openingHour = (OpeningHour)SessionUtil.toObjectList(bookingEntity.getEtickets().get(0).getOpeningHours(), OpeningHour.class);
		if(openingHour != null) {
			this.openingHours = openingHour.getOpeningHours();
		}
	}

	public List<ETicketOpenTimeDTO> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<ETicketOpenTimeDTO> openingHours) {
		this.openingHours = openingHours;
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

	public long getBookingDay() {
		return bookingDay;
	}

	public void setBookingDay(long bookingDay) {
		this.bookingDay = bookingDay;
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

	public long getEticketId() {
		return eticketId;
	}

	public void setEticketId(long eticketId) {
		this.eticketId = eticketId;
	}

	public String getEticketName() {
		return eticketName;
	}

	public void setEticketName(String eticketName) {
		this.eticketName = eticketName;
	}

	public String getStudioPicture() {
		return studioPicture;
	}

	public void setStudioPicture(String studioPicture) {
		this.studioPicture = studioPicture;
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

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
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
