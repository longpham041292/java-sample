package asia.cmg.f8.session.dto;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

public class ChangeBookingSessionStatusDto {
	
	private Long id;
	private CreditBookingSessionStatus status = CreditBookingSessionStatus.BOOKED;
	  
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CreditBookingSessionStatus getStatus() {
		return status;
	}
	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}
	  
	  
}
