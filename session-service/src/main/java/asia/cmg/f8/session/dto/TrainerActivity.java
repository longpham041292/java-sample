package asia.cmg.f8.session.dto;

/**
 * Created on 1/8/17.
 */
public class TrainerActivity {
    private String sessionUuid;
    private String productName;
    private String orderUuid;
    private String serviceFee;
    private String status;
    private String purchasedDate;
    private String usedDate;
    private String userUuid;
    private String expiredDate;
    private String purchasedTime;
    private String usedTime;
    private String name;
    private String userName;
    

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public TrainerActivity(final String sessionUuid, final String productName, final String orderUuid, final String serviceFee, final String status, final String purchasedDate,final String purchasedTime, final String usedDate,final String usedTime, final String userUuid, final String expiredDate ,final String name,final String userName) {
        this.sessionUuid = sessionUuid;
        this.productName = productName;
        this.orderUuid = orderUuid;
        this.serviceFee = serviceFee;
        this.status = status;
        this.purchasedDate = purchasedDate;
        this.usedDate = usedDate;
        this.userUuid = userUuid;
        this.expiredDate = expiredDate;
        this.purchasedTime =purchasedTime;
        this.usedTime = usedTime;
        this.name =name;
        this.userName = userName;
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

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(final String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(final String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(final String usedDate) {
        this.usedDate = usedDate;
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

	public String getPurchasedTime() {
		return purchasedTime;
	}

	public void setPurchasedTime(final String purchasedTime) {
		this.purchasedTime = purchasedTime;
	}

	public String getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(final String usedTime) {
		this.usedTime = usedTime;
	}



	public String getName() {
		return name;
	}



	public void setName(final String name) {
		this.name = name;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(final String userName) {
		this.userName = userName;
	}
}
