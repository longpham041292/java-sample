package asia.cmg.f8.session.dto;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.utils.SessionUtil;

public class CMSCreditBookingResponse {
	@JsonProperty("studio_uuid")
	private String studioUuid;

	@JsonProperty("studio_name")
	private String studioName;

	@JsonProperty
	private long id;

	@JsonProperty
	private CreditBookingSessionStatus status;

	@JsonProperty("booking_type")
	private BookingServiceType bookingType;

	@JsonProperty("start_time")
	private long startTime;

	@JsonProperty("created_time")
	private long createdTime;

	@JsonProperty("credit_amount")
	private Integer creditAmount;

	@JsonProperty
	private HashMap<String, Object> client;

	@JsonProperty("service_id")
	private String serviceId;

	@JsonProperty("service_name")
	private String serviceName;

	@JsonProperty("checkin_time")
	private long checkinTime = 0;

	public long getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(long checkinTime) {
		this.checkinTime = checkinTime;
	}

	public Integer getcreditAmount() {
		return creditAmount;
	}

	public void setcreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}

	public BookingServiceType getBookingType() {
		return bookingType;
	}

	public void setBookingType(BookingServiceType bookingType) {
		this.bookingType = bookingType;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public CMSCreditBookingResponse(CreditBookingEntity entity) {
		this.status = entity.getStatus();
		this.studioUuid = entity.getStudioUuid();
		this.studioName = entity.getStudioName();
		this.id = entity.getId();
		this.bookingType = entity.getBookingType();
		this.startTime = SessionUtil.convertDateTimeToEpochMiliSecond(entity.getStartTime());
		this.createdTime = SessionUtil.convertDateTimeToEpochMiliSecond(entity.getCreatedDate());
		this.creditAmount = entity.getCreditAmount();
		this.buildClient(entity.getClient());
		this.detectService(entity);
		if (entity.getCheckinTransaction() != null) {
			this.checkinTime =
					SessionUtil.convertDateTimeToEpochMiliSecond(entity.getCheckinTransaction().getCreatedDate());
		}
	}

	private void buildClient(BasicUserEntity entity) {
		client = new HashMap<>();
		client.put("username", entity.getUserName());
		client.put("fullname", entity.getFullName());
	}

	private void detectService(CreditBookingEntity entity) {
		if(entity.getBookingType() == BookingServiceType.CLASS) {
			serviceId = String.valueOf(entity.getClasses().get(0).getServiceId());
			serviceName = entity.getClasses().get(0).getServiceName();
		} else if (entity.getBookingType() == BookingServiceType.ETICKET) {
			serviceId = String.valueOf(entity.getEtickets().get(0).getServiceId());
			serviceName = entity.getEtickets().get(0).getServiceName();
		} else {
//			serviceId = entity.getSessions().get(0).getPtUuid();
//			serviceName = entity.getSessions().get(0).getTrainerInfo().getUserName();
		}
	}
}
