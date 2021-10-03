package asia.cmg.f8.commerce.dto;

import java.time.LocalDateTime;

import asia.cmg.f8.commerce.entity.credit.CreditCouponEntity;

public class CreditCouponEmailDTO {

	private String serial;
	private String name;
	private Integer credit;
	private Double amount;
	private LocalDateTime createdDate;
	private Integer couponExpireDay;
	private LocalDateTime couponExpireDate;

	public CreditCouponEmailDTO(CreditCouponEntity entity) {
		this.serial = entity.getSerial();
		this.name = entity.getName();
		this.credit = entity.getCredit();
		this.amount = entity.getAmount();
		this.createdDate = entity.getCreatedDate();
		this.couponExpireDay = entity.getCouponExpiredDay();
		this.couponExpireDate = entity.getCouponExpiredDate();
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getCouponExpireDay() {
		return couponExpireDay;
	}

	public void setCouponExpireDay(Integer couponExpireDay) {
		this.couponExpireDay = couponExpireDay;
	}

	public LocalDateTime getCouponExpireDate() {
		return couponExpireDate;
	}

	public void setCouponExpireDate(LocalDateTime couponExpireDate) {
		this.couponExpireDate = couponExpireDate;
	}

}
