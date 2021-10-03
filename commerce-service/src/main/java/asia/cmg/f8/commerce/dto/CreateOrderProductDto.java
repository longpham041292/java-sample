package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.commerce.constants.PaymentType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrderProductDto {

    @JsonProperty(value = "pt_uuid", required = true)
    private String ptUuid;

    @JsonProperty(value = "product_uuid", required = true)
    private String productUuid;

    @JsonProperty(value = "num_of_session", required = true)
    private int numOfSession;

    @JsonProperty(value = "promotion_price", required = true)
    private double promotionPrice;

    @JsonProperty(value = "expire_limit", required = true)
    private int expireLimit;
    
    @JsonProperty(value = "unit_price", required = true)
    private double unitPrice;

    @JsonProperty(value = "accept_term", required = true)
    private boolean acceptTerm;
    
    @JsonProperty(value = "payment_type")
    private PaymentType paymentType;
    
    @JsonProperty(value = "instrument_id")
    private Long instrumentId;

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(final String productUuid) {
        this.productUuid = productUuid;
    }

    public boolean isAcceptTerm() {
        return acceptTerm;
    }

    public void setAcceptTerm(final boolean acceptTerm) {
        this.acceptTerm = acceptTerm;
    }

    public int getNumOfSession() {
        return numOfSession;
    }

    public void setNumOfSession(final int numOfSession) {
        this.numOfSession = numOfSession;
    }

    public double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(final double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public int getExpireLimit() {
        return expireLimit;
    }

    public void setExpireLimit(final int expireLimit) {
        this.expireLimit = expireLimit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final PaymentType paymentType) {
        this.paymentType = paymentType;
    }

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}
}
