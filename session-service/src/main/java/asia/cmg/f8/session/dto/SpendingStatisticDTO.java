package asia.cmg.f8.session.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpendingStatisticDTO {
	@JsonProperty("time")
	private Long time;
	
	@JsonProperty("amount")
	private Integer creditAmount;

	public SpendingStatisticDTO(LocalDateTime time, Integer creditAmount) {
		super();
		this.time = time.atZone(ZoneId.systemDefault()).toEpochSecond();
		this.creditAmount = creditAmount;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time.atZone(ZoneId.systemDefault()).toEpochSecond();
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}
	
	
}
