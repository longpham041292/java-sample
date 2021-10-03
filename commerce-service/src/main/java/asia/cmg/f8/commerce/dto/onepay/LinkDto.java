package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkDto {
	
	@JsonProperty(value = "information_update")
	private PaymentInfoUpdateDto infoUpdate;
	
	@JsonProperty(value = "approval")
	private PaymentInfoUpdateDto approval;

	public PaymentInfoUpdateDto getInfoUpdate() {
		return infoUpdate;
	}

	public void setInfoUpdate(PaymentInfoUpdateDto infoUpdate) {
		this.infoUpdate = infoUpdate;
	}

	public PaymentInfoUpdateDto getApproval() {
		return approval;
	}

	public void setApproval(PaymentInfoUpdateDto approval) {
		this.approval = approval;
	}
}
