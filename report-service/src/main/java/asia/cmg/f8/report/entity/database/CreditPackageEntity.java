package asia.cmg.f8.report.entity.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "credit_packages")
public class CreditPackageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@Column(name = "title_vi", nullable = false, length = 255)
	@JsonIgnore
	private String titleVI;
	
	@Column(name = "title_en", nullable = false, length = 255)
	@JsonIgnore
	private String titleEN;
	
	@Transient
	@JsonProperty("title")
	private String title;
	
	@Column(name = "price", columnDefinition = "double not null default 0")
	private Double price;
	
	@Column(name = "total_price", columnDefinition = "double not null default 0")
	@JsonProperty("total_price")
	private Double totalPrice;
	
	@Column(name = "country", nullable = false, length = 5)
    private String country;
	
	@Column(name = "currency", nullable = false, length = 5)
	private String currency;
	
	@Column(name = "credit", columnDefinition = "int not null default 0")
	private Integer credit;
	
	@Column(name = "bonus_credit", columnDefinition = "int not null default 0")
	@JsonProperty("bonus_credit")
	private Integer bonusCredit;
	
	@JsonProperty("total_credit")
	@Column(name = "total_credit")
	private Integer totalCredit;
	
	@Column(name = "type", columnDefinition = "int not null default 0")
	@Enumerated(EnumType.ORDINAL)
	@JsonProperty("type")
	private CreditPackageType creditType;
	
	@Column(name = "active", columnDefinition = "boolean not null default 1")
    private Boolean active;
	
	@Column(name = "number_of_expired_day", columnDefinition = "int not null default 0")
	@JsonProperty("number_of_expired_day")
	private Integer numberOfExpiredDay;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;
	
	@Column(name = "description", length = 2000)
	@JsonIgnore
	private String description;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "creditPackage", targetEntity = VoucherEntity.class)
	private List<VoucherEntity> vouchers = new ArrayList<VoucherEntity>();
	
	@Transient
	@JsonProperty("total_voucher_amount")
	private Double totalVoucherAmount = 0d;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public CreditPackageType getCreditType() {
		return creditType;
	}

	public void setCreditType(CreditPackageType creditType) {
		this.creditType = creditType;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getBonusCredit() {
		return bonusCredit;
	}

	public void setBonusCredit(Integer bonusCredit) {
		this.bonusCredit = bonusCredit;
	}

	public Integer getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}

	public List<VoucherEntity> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<VoucherEntity> vouchers) {
		this.vouchers = vouchers;
	}

	public Integer getNumberOfExpiredDay() {
		return numberOfExpiredDay;
	}

	public void setNumberOfExpiredDay(Integer numberOfExpiredDay) {
		this.numberOfExpiredDay = numberOfExpiredDay;
	}

	public Double getTotalVoucherAmount() {
		double total = 0d;
		for (VoucherEntity voucher : vouchers) {
			total += voucher.getAmount();
		}
		this.totalVoucherAmount = total;
		
		return totalVoucherAmount;
	}

	public String getTitleVI() {
		return titleVI;
	}

	public void setTitleVI(String titleVI) {
		this.titleVI = titleVI;
	}

	public String getTitleEN() {
		return titleEN;
	}

	public void setTitleEN(String titleEN) {
		this.titleEN = titleEN;
	}
}
