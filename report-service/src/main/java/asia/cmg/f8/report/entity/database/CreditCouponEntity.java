package asia.cmg.f8.report.entity.database;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "credit_coupons", uniqueConstraints = {
		@UniqueConstraint(name = "serial_code_UN", columnNames = { "serial", "code" }) })
public class CreditCouponEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "serial", nullable = false)
	@JsonProperty("serial")
	private String serial;

	@Column(name = "code", nullable = false)
	@JsonProperty("code")
	private String code;

	@Column(name = "encrypted_code", nullable = false)
	@JsonProperty("encrypted_code")
	private String encryptedCode;

	@Column(name = "name", nullable = false)
	@JsonProperty("name")
	private String name;

	@Column(name = "credit", columnDefinition = "int not null default 0")
	@JsonProperty("credit")
	private Integer credit = 0;

	@Column(name = "bonus_credit", columnDefinition = "int not null default 0")
	@JsonProperty("bonus_credit")
	private Integer bonusCredit = 0;

	@Column(name = "amount", columnDefinition = "double not null default 0")
	@JsonProperty("amount")
	private Double amount = 0d;

	@Column(name = "coupon_expired_day", columnDefinition = "int not null default 0")
	@JsonProperty("coupon_expired_day")
	private Integer couponExpiredDay = 0;

	@Column(name = "coupon_expired_date")
	@JsonProperty("coupon_expired_date")
	private LocalDateTime couponExpiredDate;

	@JsonProperty("credit_expired_day")
	@Column(name = "credit_expired_day", columnDefinition = "int not null default 365")
	private Integer creditExpiredDay = 365; // Default is 365 days

	@Column(name = "status")
	@JsonProperty("status")
	private CreditCouponStatus status;

	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@JsonIgnore
	@UpdateTimestamp
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

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

	public String getEncryptedCode() {
		return encryptedCode;
	}

	public void setEncryptedCode(String encryptedCode) {
		this.encryptedCode = encryptedCode;
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

	public Integer getCouponExpiredDay() {
		return couponExpiredDay;
	}

	public LocalDateTime getCouponExpiredDate() {
		return couponExpiredDate;
	}

	public void setCouponExpiredDate(LocalDateTime couponExpiredDate) {
		this.couponExpiredDate = couponExpiredDate;
	}

	public void setCouponExpiredDay(Integer couponExpiredDay) {
		this.couponExpiredDay = couponExpiredDay;
	}

	public Integer getCreditExpiredDay() {
		return creditExpiredDay;
	}

	public void setCreditExpiredDay(Integer creditExpiredDay) {
		this.creditExpiredDay = creditExpiredDay;
	}

	public CreditCouponStatus getStatus() {
		return status;
	}

	public void setStatus(CreditCouponStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public Integer getBonusCredit() {
		return bonusCredit;
	}

	public void setBonusCredit(Integer bonusCredit) {
		this.bonusCredit = bonusCredit;
	}

}