package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneNumberDTO {
	@JsonProperty(value = "phone_number")
	private String phoneNumber;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
