package asia.cmg.f8.session.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.entity.ScheduleEvent;

public class ScheduleEventResponse {

	@JsonProperty(value = "title")
	private String title;
	
	@JsonProperty(value = "owner_uuid")
	private String ownerUuid;
	
	@JsonProperty(value = "available_to_train")
	private boolean availableToTrain;
	
	@JsonProperty(value = "started_time")
	private Long startedTime;
	
	@JsonProperty(value = "ended_time")
	private Long endedTime;

	public ScheduleEventResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public ScheduleEventResponse(String title, String ownerUuid, boolean availableToTrain, LocalDateTime startedTime, LocalDateTime endedTime) {
		this.title = title;
		this.ownerUuid = ownerUuid;
		this.availableToTrain = availableToTrain;
		this.startedTime = ZoneDateTimeUtils.convertToSecondUTC(startedTime);
		this.endedTime = ZoneDateTimeUtils.convertToSecondUTC(endedTime);
	}
	
	public ScheduleEventResponse(ScheduleEvent se) {
		this.title = se.getTitle();
		this.ownerUuid = se.getOwnerUuid();
		this.availableToTrain = se.isAvailableToTrain();
		this.startedTime = ZoneDateTimeUtils.convertToSecondUTC(se.getStartedTime());
		this.endedTime = ZoneDateTimeUtils.convertToSecondUTC(se.getEndedTime());
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public boolean isAvailableToTrain() {
		return availableToTrain;
	}

	public void setAvailableToTrain(boolean availableToTrain) {
		this.availableToTrain = availableToTrain;
	}

	public Long getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(Long startedTime) {
		this.startedTime = startedTime;
	}

	public Long getEndedTime() {
		return endedTime;
	}

	public void setEndedTime(Long endedTime) {
		this.endedTime = endedTime;
	}
}
