package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDto {

	@JsonProperty(value = "id")
	private String id;

	@JsonProperty(value = "instrument_id")
	private String instrumentId;

	@JsonProperty(value = "number")
	private String number;

	@JsonProperty(value = "icvv")
	private String icvv;

	@JsonProperty(value = "expire_month")
	private Integer expireMonth;

	@JsonProperty(value = "expire_year")
	private Integer expireYear;

	@JsonProperty(value = "state")
	private String state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIcvv() {
		return icvv;
	}

	public void setIcvv(String icvv) {
		this.icvv = icvv;
	}

	public Integer getExpireMonth() {
		return expireMonth;
	}

	public void setExpireMonth(Integer expireMonth) {
		this.expireMonth = expireMonth;
	}

	public Integer getExpireYear() {
		return expireYear;
	}

	public void setExpireYear(Integer expireYear) {
		this.expireYear = expireYear;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
