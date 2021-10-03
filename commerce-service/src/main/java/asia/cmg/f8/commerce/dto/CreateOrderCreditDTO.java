package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.constants.PaymentType;

public class CreateOrderCreditDTO {

	@JsonProperty(value = "credit_package_id", required = true)
	private Long creditPackageId;
	
	@JsonProperty("referal_user_uuid")
	private String referalUserUuid;
	
	@JsonProperty("referal_username")
	private String referalUsername;
	
	@JsonProperty("promotion_code")
	private String promotionCode;
	
	@JsonProperty(value = "payment_type")
	private PaymentType paymentType;
	    
	@JsonProperty(value = "instrument_id")
	private Long instrumentId;
	
	@JsonProperty(value = "accept_term", required = true)
    private boolean acceptTerm;

	public Long getCreditPackageId() {
		return creditPackageId;
	}

	public void setCreditPackageId(Long creditPackageId) {
		this.creditPackageId = creditPackageId;
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

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}
}
