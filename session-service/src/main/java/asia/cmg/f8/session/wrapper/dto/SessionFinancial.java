package asia.cmg.f8.session.wrapper.dto;

import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("squid:S2384")
public class SessionFinancial {

	private String sessionUuid;
	private String productName;
	private String orderUuid;
	private String productUuid;
	private String status;
	private String lastStatus;
	private Double price;
	private Double commission;
	private Integer numOfSession;
	private Timestamp datePurchased;
	private Timestamp dateUpdated;
	private String ptUuid;
	private String userUuid;
	private Timestamp expiredDate;
	private String oldPtUuid;
	private String userFullName;
	private String userUserName;
	private String trainerName;
	private String trainerUserName;
	private String orderCode;
	private String userClub;
	private String purchaseOrderClub;
	private String trainerStaffCode;
	private String memberBarCode;
	private String sessionTime;
	
	private String couponCode;
	private Double ptCommission = 0d;
	private boolean onepayTransaction;
	private String contractNumber;
	private String checkinClubName;

	public SessionFinancial(String sessionUuid, String productName, String orderUuid, String productUuid, String status,
						String lastStatus, Double price, Double commission, Integer numOfSession, Date datePurchased,
						Date dateUpdated, String ptUuid, String userUuid, Date expiredDate, String userFullName,
						String userUserName, String trainerName, String trainerUserName, String oldPtUuid, String orderCode,
						String userClub, String purchaseOrderClub, String memberBarCode, String trainerStaffCode, String sessionTime,
						String couponCode, Double ptCommission, Integer freeOrder, String contactNumber, String checkinClub) {
		this.sessionUuid = sessionUuid;
		this.productName = productName;
		this.orderUuid = orderUuid;
		this.productUuid = productUuid;
		this.status = status;
		this.lastStatus = lastStatus;
		this.price = price;
		this.commission = commission;
		this.numOfSession = numOfSession;
		this.datePurchased = (Timestamp) datePurchased;
		this.dateUpdated = (Timestamp) dateUpdated;
		this.ptUuid = ptUuid;
		this.userUuid = userUuid;
		this.expiredDate = (Timestamp) expiredDate;
		this.userFullName = userFullName;
		this.userUserName = userUserName;
		this.trainerName = trainerName;
		this.trainerUserName = trainerUserName;
		this.oldPtUuid = oldPtUuid;

		this.orderCode = orderCode;
		this.userClub = userClub;
		this.purchaseOrderClub = purchaseOrderClub;
		this.trainerStaffCode = trainerStaffCode;
		this.memberBarCode = memberBarCode;
		this.sessionTime = sessionTime;

		this.couponCode = couponCode;
		this.ptCommission = ptCommission == null ? 0 : ptCommission;
		this.onepayTransaction = freeOrder == 0;
		this.contractNumber = contactNumber;
		this.checkinClubName = checkinClub;
	}
	
	public SessionFinancial(String sessionUuid, String productName, String orderUuid, String productUuid, String status,
			String lastStatus, Double price, Double commission, Integer numOfSession, Date datePurchased,
			Date dateUpdated, String ptUuid, String userUuid, Date expiredDate, String userFullName,
			String userUserName, String trainerName, String trainerUserName, String oldPtUuid, String orderCode,
			String userClub, String purchaseOrderClub, String memberBarCode, String trainerStaffCode, String sessionTime) {
		this.sessionUuid = sessionUuid;
		this.productName = productName;
		this.orderUuid = orderUuid;
		this.productUuid = productUuid;
		this.status = status;
		this.lastStatus = lastStatus;
		this.price = price;
		this.commission = commission;
		this.numOfSession = numOfSession;
		this.datePurchased = (Timestamp) datePurchased;
		this.dateUpdated = (Timestamp) dateUpdated;
		this.ptUuid = ptUuid;
		this.userUuid = userUuid;
		this.expiredDate = (Timestamp) expiredDate;
		this.userFullName = userFullName;
		this.userUserName = userUserName;
		this.trainerName = trainerName;
		this.trainerUserName = trainerUserName;
		this.oldPtUuid = oldPtUuid;
		
		this.orderCode = orderCode;
		this.userClub = userClub;
		this.purchaseOrderClub = purchaseOrderClub;
		this.trainerStaffCode = trainerStaffCode;
		this.memberBarCode = memberBarCode;
		this.sessionTime = sessionTime;
	}
	
	public String getCheckinClubName() {
		return checkinClubName;
	}

	public void setCheckinClubName(String checkinClubName) {
		this.checkinClubName = checkinClubName;
	}



	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Double getPtCommission() {
		return ptCommission;
	}

	public void setPtCommission(Double ptCommission) {
		this.ptCommission = ptCommission;
	}

	public boolean isOnepayTransaction() {
		return onepayTransaction;
	}

	public void setOnepayTransaction(boolean onepayTransaction) {
		this.onepayTransaction = onepayTransaction;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getSessionUuid() {
		return sessionUuid;
	}

	public SessionFinancial setSessionUuid(final String sessionUuid) {
		this.sessionUuid = sessionUuid;
		return this;
	}

	public String getProductName() {
		return productName;
	}

	public SessionFinancial setProductName(final String productName) {
		this.productName = productName;
		return this;
	}

	public String getOrderUuid() {
		return orderUuid;
	}

	public SessionFinancial setOrderUuid(final String orderUuid) {
		this.orderUuid = orderUuid;
		return this;
	}

	public String getProductUuid() {
		return productUuid;
	}

	public SessionFinancial setProductUuid(final String productUuid) {
		this.productUuid = productUuid;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public SessionFinancial setStatus(final String status) {
		this.status = status;
		return this;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Double getPrice() {
		return price;
	}

	public SessionFinancial setPrice(final Double price) {
		this.price = price;
		return this;
	}

	public Double getCommission() {
		return commission;
	}

	public SessionFinancial setCommission(final Double commission) {
		this.commission = commission;
		return this;
	}

	public Timestamp getDatePurchased() {
		return datePurchased;
	}

	public SessionFinancial setDatePurchased(final Timestamp datePurchased) {
		this.datePurchased = datePurchased;
		return this;
	}

	public String getPtUuid() {
		return ptUuid;
	}

	public SessionFinancial setPtUuid(final String ptUuid) {
		this.ptUuid = ptUuid;
		return this;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public SessionFinancial setUserUuid(final String userUuid) {
		this.userUuid = userUuid;
		return this;
	}

	public Timestamp getExpiredDate() {
		return expiredDate;
	}

	public SessionFinancial setExpiredDate(final Timestamp expiredDate) {
		this.expiredDate = expiredDate;
		return this;
	}

	public Timestamp getDateUpdated() {
		return dateUpdated;
	}

	public SessionFinancial setDateUpdated(final Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	public Integer getNumOfSession() {
		return numOfSession;
	}

	public SessionFinancial setNumOfSession(final Integer numOfSession) {
		this.numOfSession = numOfSession;
		return this;
	}

	public String getOldPtUuid() {
		return oldPtUuid;
	}

	public SessionFinancial setOldPtUuid(final String oldPtUuid) {
		this.oldPtUuid = oldPtUuid;
		return this;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public SessionFinancial setUserFullName(final String userFullName) {
		this.userFullName = userFullName;
		return this;
	}

	public String getUserUserName() {
		return userUserName;
	}

	public SessionFinancial setUserUserName(final String userUserName) {
		this.userUserName = userUserName;
		return this;
	}

	public String getTrainerName() {
		return trainerName;
	}

	public SessionFinancial setTrainerName(final String trainerName) {
		this.trainerName = trainerName;
		return this;
	}

	public String getTrainerUserName() {
		return trainerUserName;
	}

	public SessionFinancial setTrainerUserName(final String trainerUserName) {
		this.trainerUserName = trainerUserName;
		return this;
	}

	public String getUserClub() {
		return userClub;
	}

	public String getTrainerStaffCode() {
		return trainerStaffCode;
	}

	public String getMemberBarCode() {
		return memberBarCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public String getPurchaseOrderClub() {
		return purchaseOrderClub;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}
}
