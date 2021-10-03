package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PromotionCodeResponseDTO {
	
	@JsonProperty("promotion_code")
	private String promotionCode;
	
	@JsonProperty("original_price")
	private Double originalPrice = 0d;
	
	@JsonProperty("promotion_price")
	private Double promotionPrice = 0d;
	
	@JsonProperty("discount_percentage")
	private Double discountPercentage = 0d;
	
	@JsonProperty("discount_price")
	private Double discountPrice = 0d;

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public Double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Double getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(Double promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public Double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public Double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}
}
