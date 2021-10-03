package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.utils.CommerceUtils;

public class PromotionExp {

	private String promoCodes;
	private String endDate;
	private String startDate;
	private String discount;
	private Integer freeSessions;
	private String status;
	private Integer maximumIndividualUsage;
	private Integer maximumTotalUsage;
	private String description;
	private String ptCommission;

	public PromotionExp(final Promotion promotion) {
		super();

		this.promoCodes = promotion.getCouponCode();
		this.endDate = CommerceUtils.setdateFormat(promotion.getEndDate());
		this.startDate = CommerceUtils.setdateFormat(promotion.getStartedDate());
		this.discount = promotion.getDiscount() != null ? promotion.getDiscount() + "%" : "0%";
		this.freeSessions = promotion.getFreeSession();

		if (promotion.getActive()) {
			this.status = "Active";
		} else {
			this.status = "Inactive";
		}

		this.maximumIndividualUsage = promotion.getMaxIndividualUsage();
		this.maximumTotalUsage = promotion.getMaxTotalUsage();
		this.description = promotion.getDesc();
		this.ptCommission = promotion.getPtCommission() + "%";
	}

	@JsonProperty("PtCommission")
	public String getPtCommission() {
		return ptCommission;
	}

	public void setPtCommission(String ptCommission) {
		this.ptCommission = ptCommission;
	}

	@JsonProperty("PromoCodes")
	public String getPromoCodes() {
		return promoCodes;
	}

	public void setPromoCodes(final String promoCodes) {
		this.promoCodes = promoCodes;
	}

	@JsonProperty("EndDate")
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	@JsonProperty("StartDate")
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	@JsonProperty("Discount")
	public String getDiscount() {
		return discount;
	}

	public void setDiscount(final String discount) {
		this.discount = discount;
	}

	@JsonProperty("FreeSessions")
	public Integer getFreeSessions() {
		return freeSessions;
	}

	public void setFreeSessions(final Integer freeSessions) {
		this.freeSessions = freeSessions;
	}

	@JsonProperty("Status")
	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@JsonProperty("MaximumIndividualUsage")
	public Integer getMaximumIndividualUsage() {
		return maximumIndividualUsage;
	}

	public void setMaximumIndividualUsage(final Integer maximumIndividualUsage) {
		this.maximumIndividualUsage = maximumIndividualUsage;
	}

	@JsonProperty("MaximumTotalUsage")
	public Integer getMaximumTotalUsage() {
		return maximumTotalUsage;
	}

	public void setMaximumTotalUsage(final Integer maximumTotalUsage) {
		this.maximumTotalUsage = maximumTotalUsage;
	}

	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
