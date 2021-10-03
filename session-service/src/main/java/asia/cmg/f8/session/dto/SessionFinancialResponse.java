package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionFinancialResponse {

    private String sessionUuid;
    private String productName;
    private String orderUuid;
    private String productUuid;
    private String price;
    private String originalPrice;
    private String discount;
    private String commission;
    private String serviceFee;
    private String commissionUnitPrice;
    private String datePurchased;
    private String dateUpdated;
    private String trainingDate;
    private String ptUuid;
    private String userUuid;
    private String expiredDate;
    private String oldPtUuid;
    private String euName; 
    private String euUserName;
    private String ptName;
    private String ptUserName;

    private String userClub;
    private String posClub;
    private String sessionTime;
    private String orderCode;
    private String contractNumber;
    private String trainerStaffCode;
    private String memberBarCode;

    private Integer totalSession;
    private Integer usedSession;
    private Integer remainingSession;
    private Integer freeSession;

    private String lastStatus;
    
    private String couponCode;  
    private boolean onepayTransaction;
    private Integer originalSession;
    
    private String ptCommissionPercent;
    private String clubCommission;
    private String ptCommission;
    private String ptGrossIncome;
    private String ptNetIncome;
    private String checkinClubName;

	@SuppressWarnings("PMD.ExcessiveParameterList")
    public SessionFinancialResponse(String sessionUuid,
                                    String productName,
                                    String orderUuid,
                                    String productUuid,
                                    
                                    String price,
                                    String originalPrice,
                                    String discount,
                                    String commission,
                                    String serviceFee,
                                    String commissionUnitPrice,
                                    String datePurchased,
                                    String dateUpdated,
                                    
                                    String sessionTime,
                                    String ptUuid,
                                    String userUuid,
                                    String expiredDate,
                                    String oldPtUuid,
                                    
                                    String userFullName,
                                    String userUserName ,
                                    String trainerName,
                                    String trainerUserName,
                                    String orderCode,
                                    String contractNumber,
                                    
                                    String userClub,
                                    String posClub,
                                    String trainerStaffCode,
                                    String memberBarCode,
                                    Integer totalSession,
                                    Integer usedSession,
                                    Integer remainingSession,
                                    String lastStatus) {
        super();
        this.sessionUuid = sessionUuid;
        this.productName = productName;
        this.orderUuid = orderUuid;
        this.productUuid = productUuid;
        
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.commission = commission;
        this.serviceFee = serviceFee;
        this.commissionUnitPrice = commissionUnitPrice;
        this.datePurchased = datePurchased;
        this.dateUpdated = dateUpdated;
        this.trainingDate = dateUpdated;
        
        this.ptUuid = ptUuid;
        this.userUuid = userUuid;
        this.expiredDate = expiredDate;
        this.oldPtUuid = oldPtUuid;
        this.euName =userFullName; 
        this.euUserName =userUserName;
        this.ptName=trainerName;
        this.ptUserName =trainerUserName;

        this.orderCode = orderCode;
        this.contractNumber = contractNumber;
        this.trainerStaffCode = trainerStaffCode;
        this.userClub = userClub;
        this.posClub = posClub;
        this.sessionTime = sessionTime;
        this.memberBarCode = memberBarCode;

        this.totalSession = totalSession;
        this.usedSession = usedSession;
        this.remainingSession = remainingSession;
        this.lastStatus = lastStatus;
    }

    public String getPtGrossIncome() {
		return ptGrossIncome;
	}

	public void setPtGrossIncome(String ptGrossIncome) {
		this.ptGrossIncome = ptGrossIncome;
	}

	public String getPtNetIncome() {
		return ptNetIncome;
	}

	public void setPtNetIncome(String ptNetIncome) {
		this.ptNetIncome = ptNetIncome;
	}

	public String getPtCommissionPercent() {
		return ptCommissionPercent;
	}

	public void setPtCommissionPercent(String ptCommissionPercent) {
		this.ptCommissionPercent = ptCommissionPercent;
	}

	public String getClubCommission() {
		return clubCommission;
	}

	public void setClubCommission(String clubCommission) {
		this.clubCommission = clubCommission;
	}

	public String getPtCommission() {
		return ptCommission;
	}

	public void setPtCommission(String ptCommission) {
		this.ptCommission = ptCommission;
	}

	public String getCheckinClubName() {
		return checkinClubName;
	}

	public void setCheckinClubName(String checkinClubName) {
		this.checkinClubName = checkinClubName;
	}

	public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(final String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(final String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(final String productUuid) {
        this.productUuid = productUuid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(final String commission) {
        this.commission = commission;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(final String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(final String datePurchased) {
        this.datePurchased = datePurchased;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(final String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(final String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getOldPtUuid() {
        return oldPtUuid;
    }

    public void setOldPtUuid(final String oldPtUuid) {
        this.oldPtUuid = oldPtUuid;
    }

    @JsonProperty("EUname")
	public String getEuName() {
		return euName;
	}


	public void setEuName(final String euName) {
		this.euName = euName;
	}

	@JsonProperty("EUuserName")
	public String getEuUserName() {
		return euUserName;
	}


	public void setEuUserName(final String euUserName) {
		this.euUserName = euUserName;
	}

	@JsonProperty("PTname")
	public String getPtName() {
		return ptName;
	}


	public void setPtName(final String ptName) {
		this.ptName = ptName;
	}

	@JsonProperty("PTuserName")
	public String getPtUserName() {
		return ptUserName;
	}


	public void setPtUserName(final String ptUserName) {
		this.ptUserName = ptUserName;
	}

    public String getUserClub() {
        return userClub;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getTrainerStaffCode() {
        return trainerStaffCode;
    }

    public String getMemberBarCode() {
        return memberBarCode;
    }

    public String getPosClub() {
        return posClub;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public Integer getTotalSession() {
        return totalSession;
    }

    public Integer getUsedSession() {
        return usedSession;
    }

    public Integer getRemainingSession() {
        return remainingSession;
    }
    public String getLastStatus() {
		return lastStatus;
	}


	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}


	public String getContractNumber() {
		return contractNumber;
	}


	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}


	public String getCouponCode() {
		return couponCode;
	}


	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}


	public boolean isOnepayTransaction() {
		return onepayTransaction;
	}


	public void setOnepayTransaction(boolean onepayTransaction) {
		this.onepayTransaction = onepayTransaction;
	}


	public Integer getOriginalSession() {
		return originalSession;
	}


	public void setOriginalSession(Integer originalSession) {
		this.originalSession = originalSession;
	}


	public Integer getFreeSession() {
		return freeSession;
	}


	public void setFreeSession(Integer freeSession) {
		this.freeSession = freeSession;
	}

	public String getCommissionUnitPrice() {
		return commissionUnitPrice;
	}

	public void setCommissionUnitPrice(String commissionUnitPrice) {
		this.commissionUnitPrice = commissionUnitPrice;
	}


	public String getTrainingDate() {
		return trainingDate;
	}


	public void setTrainingDate(String trainingDate) {
		this.trainingDate = trainingDate;
	}
}
