package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAddressDto {
	
	@JsonProperty(value = "line1")
	private String line1;
	
	@JsonProperty(value = "state")
	private String state;
	
	@JsonProperty(value = "city")
	private String city;
	
	@JsonProperty(value = "country_code")
	private String countryCode;

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
