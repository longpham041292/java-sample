package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.credit.CreditPackageType;

public class CreditPackageDTO {
	
	@JsonProperty(value = "title", required = true)
	private String title;
	
	@JsonProperty(value = "price", required = true)
	private Double price;
	
	@JsonProperty(value = "total_price", required = true)
	private Double totalPrice;
	
	@JsonProperty(value = "credit", required = true)
	private Integer credit;
	
	@JsonProperty(value = "bonus_credit", required = true)
	private Integer bonusCredit;
	
	@JsonProperty(value = "total_credit", required = true)
	private Integer totalCredit;
	
	@JsonProperty(value = "country", required = true)
	private String country;
	
	@JsonProperty(value = "currency", required = true)
	private String currency;
	
	@JsonProperty(value = "type", required = true)
	private CreditPackageType type;
	
	@JsonProperty(value = "numberOfExpiredDay", required = true)
	private Integer numberOfExpiredDay;
	
	@JsonProperty(value = "description")
	private String description;
	
	@JsonProperty(value = "active", required = true)
	private Boolean active;

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

	public Integer getCredit() {
		return credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public CreditPackageType getType() {
		return type;
	}

	public void setType(CreditPackageType type) {
		this.type = type;
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

	public Integer getNumberOfExpiredDay() {
		return numberOfExpiredDay;
	}

	public void setNumberOfExpiredDay(Integer numberOfExpiredDay) {
		this.numberOfExpiredDay = numberOfExpiredDay;
	}
}
