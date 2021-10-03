package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainerProfitSharingReportDTO {
	@JsonProperty("pt_name")
	private String ptName;

	@JsonProperty("pt_username")
	private String ptUsername;

	@JsonProperty("pt_class")
	private String ptClass;

	@JsonProperty("total_amount")
	private Integer totalAmount;

	@JsonProperty("vat_amount")
	private Integer vatAmount;

	@JsonProperty("amount_exclude_vat")
	private Integer amountExcludeVat;

	@JsonProperty("leep_fee")
	private Long leepFee;

	@JsonProperty("pt_coin_earn")
	private Long ptCoinEarn;

	@JsonProperty("pt_gross_income")
	private Long ptGrossIncome;

	@JsonProperty("tax_witholding")
	private Long taxWitholding;

	@JsonProperty("pt_net_income")
	private Long ptNetIncome;

	@JsonProperty("amount_return")
	private Long amountReturn;

	@JsonProperty("full_name")
	private String fullName;

	@JsonProperty("id_number")
	private Integer idNumber;

	@JsonProperty("pit_tax_code")
	private Integer pitTaxCode;

	@JsonProperty("email_address")
	private String emailAddress;

	@JsonProperty("phone_number")
	private String phoneNumber;

	@JsonProperty("location")
	private String location;

	@JsonProperty("bank_account_number")
	private String bankAccountNumber;

	@JsonProperty("bank_name")
	private String bankName;

	@JsonProperty("bank_branch")
	private String bankBranch;

	@JsonProperty("payment_status")
	private String paymentStatus;

	@JsonProperty("to_date")
	private String toDate;

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

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Integer vatAmount) {
		this.vatAmount = vatAmount;
	}

	public Integer getAmountExcludeVat() {
		return amountExcludeVat;
	}

	public void setAmountExcludeVat(Integer amountExcludeVat) {
		this.amountExcludeVat = amountExcludeVat;
	}

	public Long getLeepFee() {
		return leepFee;
	}

	public void setLeepFee(Long leepFee) {
		this.leepFee = leepFee;
	}

	public Long getPtCoinEarn() {
		return ptCoinEarn;
	}

	public void setPtCoinEarn(Long ptCoinEarn) {
		this.ptCoinEarn = ptCoinEarn;
	}

	public Long getPtGrossIncome() {
		return ptGrossIncome;
	}

	public void setPtGrossIncome(Long ptGrossIncome) {
		this.ptGrossIncome = ptGrossIncome;
	}

	public Long getTaxWitholding() {
		return taxWitholding;
	}

	public void setTaxWitholding(Long taxWitholding) {
		this.taxWitholding = taxWitholding;
	}

	public Long getPtNetIncome() {
		return ptNetIncome;
	}

	public void setPtNetIncome(Long ptNetIncome) {
		this.ptNetIncome = ptNetIncome;
	}

	public Long getAmountReturn() {
		return amountReturn;
	}

	public void setAmountReturn(Long amountReturn) {
		this.amountReturn = amountReturn;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
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

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

}
