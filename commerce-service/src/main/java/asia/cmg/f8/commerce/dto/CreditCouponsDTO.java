package asia.cmg.f8.commerce.dto;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.credit.CreditCouponEntity;
import asia.cmg.f8.commerce.entity.credit.CreditCouponStatus;

public class CreditCouponsDTO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("serial")
	private String serial;

	@JsonProperty("code")
	private String code;

	@JsonProperty("name")
	private String name;

	@JsonProperty("credit")
	private Integer credit;

	@JsonProperty("bonus_credit")
	private Integer bonusCredit;

	@JsonProperty("amount")
	private Double amount;

	@JsonProperty("credit_expired_day")
	private Integer creditExpiredDay;

	@JsonProperty("coupon_expired_day")
	private Integer couponExpiredDay;

	@JsonProperty("status")
	private CreditCouponStatus status;

	@JsonProperty("created_date")
	private String createdDate;

	@JsonProperty("qr_code_url")
	private String qrCodeUrl;

	public CreditCouponsDTO(CreditCouponEntity c) {
		final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		this.id = c.getId();
		this.serial = c.getSerial();
		this.code = c.getEncryptedCode();
		this.name = c.getName();
		this.credit = c.getCredit();
		this.bonusCredit = c.getBonusCredit();
		this.amount = c.getAmount();
		this.creditExpiredDay = c.getCreditExpiredDay();
		this.couponExpiredDay = c.getCouponExpiredDay();
		this.status = c.getStatus();
		this.createdDate = DATE_FORMATTER.format(c.getCreatedDate());
		this.qrCodeUrl = c.getQrCodeUrl() == null ? "" : c.getQrCodeUrl();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getBonusCredit() {
		return bonusCredit;
	}

	public void setBonusCredit(Integer bonusCredit) {
		this.bonusCredit = bonusCredit;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public Integer getCreditExpiredDay() {
		return creditExpiredDay;
	}

	public void setCreditExpiredDay(Integer creditExpiredDay) {
		this.creditExpiredDay = creditExpiredDay;
	}

	public Integer getCouponExpiredDay() {
		return couponExpiredDay;
	}

	public void setCouponExpiredDay(Integer couponExpiredDay) {
		this.couponExpiredDay = couponExpiredDay;
	}

	public CreditCouponStatus getStatus() {
		return status;
	}

	public void setStatus(CreditCouponStatus status) {
		this.status = status;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getQrCodeUrl() {
		return qrCodeUrl;
	}

	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}

}
