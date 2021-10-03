package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.credit.OutComePaymentStatus;

public class DeductCoinClubDTO {

	@JsonProperty("outcome_id")
	private Long outcomeId;

	@JsonProperty("outcome_todate")
	private Long outcomeTodate;

	@JsonProperty("club_name")
	private String clubName;

	@JsonProperty("club_username")
	private String clubUsername;

	@JsonProperty("club_class")
	private String clubClass;

	@JsonProperty("club_type")
	private String clubType;

	@JsonProperty("business_license")
	private String businessLicense;

	@JsonProperty("business_tax_code")
	private String businessTaxCode;

	@JsonProperty("earn_coin")
	private Integer earnCoin;

	@JsonProperty("top_up")
	private Integer topUp;

	@JsonProperty("used_coin")
	private Integer usedCoin;

	@JsonProperty("balance_coin")
	private Integer balanceCoin;

	@JsonProperty("deduct_coin")
	private Integer deductCoin;

	@JsonProperty("amount_return")
	private Double amountReturn;

	@JsonProperty("beneficiary_name")
	private String beneficiaryName;

	@JsonProperty("bank_account_number")
	private Integer bankAccountNumber;

	@JsonProperty("bank_name")
	private String bankName;

	@JsonProperty("bank_branch")
	private String bankBranch;

	@JsonProperty("transaction_status")
	private String tracsactionStatus;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("payment_status")
	private OutComePaymentStatus paymentStatus;

	@JsonProperty("payment_date")
	private Long paymentDate;

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubUsername() {
		return clubUsername;
	}

	public void setClubUsername(String clubUsername) {
		this.clubUsername = clubUsername;
	}

	public String getClubClass() {
		return clubClass;
	}

	public void setClubClass(String clubClass) {
		this.clubClass = clubClass;
	}

	public String getClubType() {
		return clubType;
	}

	public void setClubType(String clubType) {
		this.clubType = clubType;
	}

	public String getBusinessLicense() {
		return businessLicense;
	}

	public void setBusinessLicense(String businessLicense) {
		this.businessLicense = businessLicense;
	}

	public String getBusinessTaxCode() {
		return businessTaxCode;
	}

	public void setBusinessTaxCode(String businessTaxCode) {
		this.businessTaxCode = businessTaxCode;
	}

	public Integer getEarnCoin() {
		return earnCoin;
	}

	public void setEarnCoin(Integer earnCoin) {
		this.earnCoin = earnCoin;
	}

	public Integer getTopUp() {
		return topUp;
	}

	public void setTopUp(Integer topUp) {
		this.topUp = topUp;
	}

	public Integer getUsedCoin() {
		return usedCoin;
	}

	public void setUsedCoin(Integer usedCoin) {
		this.usedCoin = usedCoin;
	}

	public Integer getBalanceCoin() {
		return balanceCoin;
	}

	public void setBalanceCoin(Integer balanceCoin) {
		this.balanceCoin = balanceCoin;
	}

	public Integer getDeductCoin() {
		return deductCoin;
	}

	public void setDeductCoin(Integer deductCoin) {
		this.deductCoin = deductCoin;
	}

	public Double getAmountReturn() {
		return amountReturn;
	}

	public void setAmountReturn(Double amountReturn) {
		this.amountReturn = amountReturn;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public Integer getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(Integer bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getTracsactionStatus() {
		return tracsactionStatus;
	}

	public void setTracsactionStatus(String tracsactionStatus) {
		this.tracsactionStatus = tracsactionStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public OutComePaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(OutComePaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Long getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Long paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Long getOutcomeId() {
		return outcomeId;
	}

	public void setOutcomeId(Long outcomeId) {
		this.outcomeId = outcomeId;
	}

	public Long getOutcomeTodate() {
		return outcomeTodate;
	}

	public void setOutcomeTodate(Long outcomeTodate) {
		this.outcomeTodate = outcomeTodate;
	}

}
