package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.credit.CreditCouponStatus;

public class CreateOrderCodResponse {

	@JsonProperty("order_id")
	private Long orderId;

	@JsonProperty("coupon_credit")
	private Integer couponCredit;

	@JsonProperty("bonus_credit")
	private Integer bonusCredit;

	@JsonProperty("coupon_amount")
	private Double couponAmount;

	private BasicUserInfo owner;

	@JsonProperty("status")
	private CreditCouponStatus status;

	@JsonProperty("serial")
	private String serial;

	@JsonProperty("coupon_name")
	private String couponName;

	@JsonProperty("credit_expired")
	private Long creditExpired;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getCouponCredit() {
		return couponCredit;
	}

	public void setCouponCredit(Integer couponCredit) {
		this.couponCredit = couponCredit;
	}

	public Double getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(Double couponAmount) {
		this.couponAmount = couponAmount;
	}

	public BasicUserInfo getOwner() {
		return owner;
	}

	public void setOwner(BasicUserInfo owner) {
		this.owner = owner;
	}

	public CreditCouponStatus getStatus() {
		return status;
	}

	public void setStatus(CreditCouponStatus status) {
		this.status = status;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Long getCreditExpired() {
		return creditExpired;
	}

	public void setCreditExpired(Long creditExpired) {
		this.creditExpired = creditExpired;
	}

	public Integer getBonusCredit() {
		return bonusCredit;
	}

	public void setBonusCredit(Integer bonusCredit) {
		this.bonusCredit = bonusCredit;
	}

}
