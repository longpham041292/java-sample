package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicTrainerEarningStatisticDto {

	@JsonProperty("time")
	private final Long time;
	
	@JsonProperty("amount")
	private final Integer creditAmount;

	public BasicTrainerEarningStatisticDto(long time, Integer creditAmount) {
		super();
		this.time = time;
		this.creditAmount = creditAmount;
	}

	public long getTime() {
		return time;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}	
}
