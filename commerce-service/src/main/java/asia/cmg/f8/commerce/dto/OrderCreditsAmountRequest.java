package asia.cmg.f8.commerce.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.constants.PaymentType;

public class OrderCreditsAmountRequest {

	@JsonProperty("referal_user_uuid")
	private String referalUserUuid;
	
	@JsonProperty("referal_username")
	private String referalUsername;
	
	@NotNull
	@JsonProperty(value = "payment_type")
	private PaymentType paymentType;
	
	@NotNull
	@JsonProperty("credits_amount")
	private Integer creditsAmount;
	
	@JsonProperty(value = "instrument_id")
	private Long instrumentId;
	
	@NotNull
	@JsonProperty(value = "accept_term", required = true)
    private Boolean acceptTerm;

	public Integer getCreditsAmount() {
		return creditsAmount;
	}

	public void setCreditsAmount(Integer creditsAmount) {
		this.creditsAmount = creditsAmount;
	}

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public String getReferalUserUuid() {
		return referalUserUuid;
	}

	public void setReferalUserUuid(String referalUserUuid) {
		this.referalUserUuid = referalUserUuid;
	}

	public String getReferalUsername() {
		return referalUsername;
	}

	public void setReferalUsername(String referalUsername) {
		this.referalUsername = referalUsername;
	}

	public boolean isAcceptTerm() {
		return acceptTerm;
	}

	public void setAcceptTerm(boolean acceptTerm) {
		this.acceptTerm = acceptTerm;
	}
}
