package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionDto {

    @JsonProperty(value = "number_of_month", required = true)
    private int numberOfMonth;

    @JsonProperty(value = "limit_day", required = true)
    private int limitDay;

    @JsonProperty(value = "price", required = true)
    private Double price;

    @JsonProperty(value = "subscription_type_id", required = true)
    private Long subscriptionTypeId;

    @JsonProperty(value = "status", required = true)
    private String status;

    @JsonProperty(value = "uuid")
    private String uuid;

    @JsonProperty(value = "discount")
    private Double discount;

    @JsonProperty(value = "sub_total")
    private Double subTotal;

    @JsonProperty(value = "level")
    private String level;
    
    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "currency")
    private String currency;

    public int getNumberOfMonth() {
        return numberOfMonth;
    }

    public void setNumberOfMonth(int numberOfMonth) {
        this.numberOfMonth = numberOfMonth;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public void setSubscriptionTypeId(Long subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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
}
