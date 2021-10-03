package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClubProfitSharingReportDTO {

	@JsonProperty("club_name")
	private String clubName;

	@JsonProperty("club_username")
	private String clubUserName;

	@JsonProperty("club_class")
	private String clubClass;

	@JsonProperty("club_type")
	private String clubType;

	@JsonProperty("business_license")
	private String businessLicense;

	@JsonProperty("business_tax_code")
	private String businessTaxCode;

	@JsonProperty("total_amount")
	private Integer totalAmount;

	@JsonProperty("vat_amount")
	private Integer vatAmount;

	@JsonProperty("amount_exclude_vat")
	private Integer amountExcludeVat;

	@JsonProperty("leep_fee")
	private Long leepFee;

	@JsonProperty("club_coin_earn")
	private Long clubCoinEarn;

	@JsonProperty("club_gross_income")
	private Long clubGrossIncome;

	@JsonProperty("tax_witholding")
	private Long taxWitholding;

	@JsonProperty("club_net_income")
	private Long clubNetIncome;

	@JsonProperty("representative_office")
	private String representativeOffice;

	@JsonProperty("club_address")
	private String clubAddress;

	@JsonProperty("email_address")
	private String emailAddress;

	@JsonProperty("phone_number")
	private String phoneNumber;

	@JsonProperty("beneficiary_name")
	private String beneficiaryName;

	@JsonProperty("bank_account_number")
	private String bankAccountNumber;

	@JsonProperty("bank_name")
	private String bankName;

	@JsonProperty("bank_branch")
	private String bankBranch;

	@JsonProperty("transaction_confirm_status")
	private String transactionConfirmStatus;

	@JsonProperty("payment_status")
	private String paymentStatus;

	@JsonProperty("to_date")
	private String toDate;

	public ClubProfitSharingReportDTO() {
		super();
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubUserName() {
		return clubUserName;
	}

	public void setClubUserName(String clubUserName) {
		this.clubUserName = clubUserName;
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

	public Long getClubCoinEarn() {
		return clubCoinEarn;
	}

	public void setClubCoinEarn(Long clubCoinEarn) {
		this.clubCoinEarn = clubCoinEarn;
	}

	public Long getClubGrossIncome() {
		return clubGrossIncome;
	}

	public void setClubGrossIncome(Long clubGrossIncome) {
		this.clubGrossIncome = clubGrossIncome;
	}

	public Long getTaxWitholding() {
		return taxWitholding;
	}

	public void setTaxWitholding(Long taxWitholding) {
		this.taxWitholding = taxWitholding;
	}

	public Long getClubNetIncome() {
		return clubNetIncome;
	}

	public void setClubNetIncome(Long clubNetIncome) {
		this.clubNetIncome = clubNetIncome;
	}

	public String getRepresentativeOffice() {
		return representativeOffice;
	}

	public void setRepresentativeOffice(String representativeOffice) {
		this.representativeOffice = representativeOffice;
	}

	public String getClubAddress() {
		return clubAddress;
	}

	public void setClubAddress(String clubAddress) {
		this.clubAddress = clubAddress;
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

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
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

	public String getTransactionConfirmStatus() {
		return transactionConfirmStatus;
	}

	public void setTransactionConfirmStatus(String transactionConfirmStatus) {
		this.transactionConfirmStatus = transactionConfirmStatus;
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
