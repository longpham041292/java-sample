package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCreditFlowStatisticDto {
	@JsonProperty("time")
	private final Long time;

	@JsonProperty("amount")
	private final Integer creditAmount;

	public UserCreditFlowStatisticDto(long time, Integer creditAmount) {
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
