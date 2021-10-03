package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.credit.OutComePaymentStatus;

public class DeductCoinTrainerDTO {

	@JsonProperty("outcome_id")
	private Long outcomeId;

	@JsonProperty("outcome_todate")
	private Long outcomeTodate;

	@JsonProperty("pt_name")
	private String ptName;

	@JsonProperty("pt_username")
	private String ptUsername;

	@JsonProperty("pt_class")
	private String ptClass;

	@JsonProperty("id_number")
	private Integer idNumber;

	@JsonProperty("pit_tax_code")
	private Integer pitTaxCode;

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

	@JsonProperty("exchange")
	private Double exchange;

	@JsonProperty("beneficiary_name")
	private String beneficiaryName;

	@JsonProperty("bank_account_number")
	private Integer bankAccountNumber;

	@JsonProperty("bank_name")
	private String bankName;

	@JsonProperty("bank_branch")
	private String bankBranch;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("payment_status")
	private OutComePaymentStatus paymentStatus;

	@JsonProperty("payment_date")
	private Long paymentDate;

	public String getPtName() {
		return ptName;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	public String getPtUsername() {
		return ptUsername;
	}

	public void setPtUsername(String ptUsername) {
		this.ptUsername = ptUsername;
	}

	public String getPtClass() {
		return ptClass;
	}

	public void setPtClass(String ptClass) {
		this.ptClass = ptClass;
	}

	public Integer getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(Integer idNumber) {
		this.idNumber = idNumber;
	}

	public Integer getPitTaxCode() {
		return pitTaxCode;
	}

	public void setPitTaxCode(Integer pitTaxCode) {
		this.pitTaxCode = pitTaxCode;
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

	public Double getExchange() {
		return exchange;
	}

	public void setExchange(Double exchange) {
		this.exchange = exchange;
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
