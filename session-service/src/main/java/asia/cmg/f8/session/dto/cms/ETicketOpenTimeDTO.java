package asia.cmg.f8.session.dto.cms;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ETicketOpenTimeDTO {
	
	@JsonProperty("from")
	private Long from;
	
	@JsonProperty("to")
	private Long to;
	
	public ETicketOpenTimeDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public ETicketOpenTimeDTO(Long startTime, Long endTime) {
		this.from = startTime;
		this.to = endTime;
	}

	public Long getFrom() {
		return from;
	}

	public void setFrom(Long from) {
		this.from = from;
	}

	public Long getTo() {
		return to;
	}

	public void setTo(Long to) {
		this.to = to;
	}
}
