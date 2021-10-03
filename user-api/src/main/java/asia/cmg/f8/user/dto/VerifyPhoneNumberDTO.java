package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyPhoneNumberDTO {
	@JsonProperty(value = "phone_validated")
	private Boolean verifyPhone;
	
	public VerifyPhoneNumberDTO(Boolean verifyPhone) {
		super();
		this.verifyPhone = verifyPhone;
	}

	public Boolean getVerifyPhone() {
		return verifyPhone;
	}

	public void setVerifyPhone(Boolean verifyPhone) {
		this.verifyPhone = verifyPhone;
	}
}
