package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentTokenRequestDto {
	@JsonProperty(value = "number")
	private String number;

	@JsonProperty(value = "expire_month")
	private Integer expireMonth;

	@JsonProperty(value = "expire_year")
	private Integer expireYear;

	@JsonProperty(value = "cvv")
	private Integer cvv;

	@JsonProperty(value = "pay_time")
	private String payTime;

	@JsonProperty(value = "sequence_number")
	private Integer sequenceNumber;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public Integer getCvv() {
		return cvv;
	}

	public void setCvv(Integer cvv) {
		this.cvv = cvv;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

}
