package asia.cmg.f8.session.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.BasicUserEntity;

public class UserSubscriptionDto {

	public UserSubscriptionDto(BasicUserEntity user, final LocalDateTime startTime, final LocalDateTime endTime,
			final int numberOfMonth, final int limitDay) {
		this.user = BasicUserInfo.convertFromEntity(user);
		this.startTime = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		this.endTime = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		this.numberOfMonth = numberOfMonth;
		this.limitDay = limitDay;
	}

	@JsonProperty(value = "user")
	private BasicUserInfo user;

	@JsonProperty(value = "start_time")
	private Long startTime;

	@JsonProperty(value = "end_time")
	private Long endTime;

	@JsonProperty(value = "number_of_month")
	private int numberOfMonth;

	@JsonProperty(value = "limit_day")
	private int limitDay;

	public BasicUserInfo getUser() {
		return user;
	}

	public void setUser(BasicUserInfo user) {
		this.user = user;
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

	public int getNumberOfMonth() {
		return numberOfMonth;
	}

	public void setNumberOfMonth(int numberOfMonth) {
		this.numberOfMonth = numberOfMonth;
	}

	public int getLimitDay() {
		return limitDay;
	}

	public void setLimitDay(int limitDay) {
		this.limitDay = limitDay;
	}
	

}
