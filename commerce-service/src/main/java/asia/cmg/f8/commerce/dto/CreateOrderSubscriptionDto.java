package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.constants.PaymentType;

public class CreateOrderSubscriptionDto {

	@JsonProperty(value = "pt_uuid", required = true)
	private String ptUuid;

	@JsonProperty(value = "subscription_uuid", required = true)
	private String subscriptionUuid;
	
	@JsonProperty(value = "payment_type")
	private PaymentType paymentType;
	    
	@JsonProperty(value = "instrument_id")
	private Long instrumentId;
	
	@JsonProperty(value = "accept_term", required = true)
    private boolean acceptTerm;

	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	public String getSubscriptionUuid() {
		return subscriptionUuid;
	}

	public void setSubscriptionUuid(String subscriptionUuid) {
		this.subscriptionUuid = subscriptionUuid;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}

	public boolean isAcceptTerm() {
		return acceptTerm;
	}

	public void setAcceptTerm(boolean acceptTerm) {
		this.acceptTerm = acceptTerm;
	}
	
}
