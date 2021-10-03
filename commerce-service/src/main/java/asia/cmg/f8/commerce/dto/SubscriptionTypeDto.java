package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionTypeDto {

    @JsonProperty(value = "level_code", required = true)
    private String levelCode;

    @JsonProperty(value = "commission", required = true)
    private Double commission;

    @JsonProperty(value = "unit_price", required = true)
    private double unitPrice;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "uuid")
    private String uuid;

    @JsonProperty(value = "subscription_type_id")
    private String subscriptionTypeId;
    
    @JsonProperty(value = "option_id")
    private Integer optionId;

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public void setSubscriptionTypeId(String subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }

	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}
}
