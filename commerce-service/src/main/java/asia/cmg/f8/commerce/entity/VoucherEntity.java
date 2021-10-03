package asia.cmg.f8.commerce.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "vouchers")
public class VoucherEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	@JsonProperty("title")
	private String title;

	@Column(name = "title_en", nullable = false,  length = 255)
	@JsonIgnore
	private String titleEN;

	@Column(name = "title_vi", nullable = false, length = 255)
	@JsonIgnore
	private String titleVI;

	@Column(name = "credit", columnDefinition = "int not null default 0")
	private Integer credit;

	@Column(name = "amount", columnDefinition = "double not null default 0")
	private Double amount;

	@Column(name = "currency", nullable = false)
	@Enumerated(EnumType.STRING)
	private VoucherCurrency currency;

	@Column(name = "quantity", columnDefinition = "int not null default 0")
	private Integer quantity;

	@Column(name = "expired")
	private LocalDateTime expired;

	@Column(name = "service_level", length = 50)
	@JsonProperty("service_level")
	private String serviceLevel;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "leep_service_id", nullable = false)
	@JsonIgnore
	private LeepServiceEntity leepService;

	@ManyToOne
	@JoinColumn(name = "credit_package_id", nullable = false)
	@JsonIgnore
	private LeepServiceEntity creditPackage;

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

	public VoucherCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(VoucherCurrency currency) {
		this.currency = currency;
	}

	public LocalDateTime getExpired() {
		return expired;
	}

	public void setExpired(LocalDateTime expired) {
		this.expired = expired;
	}

	public LeepServiceEntity getLeepService() {
		return leepService;
	}

	public void setLeepService(LeepServiceEntity leepService) {
		this.leepService = leepService;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public LeepServiceEntity getCreditPackage() {
		return creditPackage;
	}

	public void setCreditPackage(LeepServiceEntity creditPackage) {
		this.creditPackage = creditPackage;
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

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getTitleEN() {
		return titleEN;
	}

	public void setTitleEN(String titleEN) {
		this.titleEN = titleEN;
	}

	public String getTitleVI() {
		return titleVI;
	}

	public void setTitleVI(String titleVI) {
		this.titleVI = titleVI;
	}

}
