package asia.cmg.f8.session.wrapper.dto;

import java.sql.Timestamp;
import java.util.Date;


/**
 * @author tung.nguyenthanh
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "squid:S2384"})
public class OrderFinancialRecord {

    private final String productName;
    private final String orderUuid;
    private final String productUuid;
    private final Double price;
    private final Double originalPrice;
    private final Double discount;
    private final Timestamp datePurchase;
    private final Timestamp dateExpire;
    private final String ptUuid;
    private final String userUuid;
    private final String userFullName;
    private final String userUserName;
    private final String trainerName;
    private final String trainerUserName;

    private final String orderCode;
    private String contractNumber;
    private final String userClub;
    private final String purchaseOrderClub;
    private final String trainerStaffCode;
    private final String memberBarCode;

    private Integer totalSession;
    private Integer freeSession;
    private Integer burnedSession;
    
    private String couponCode;  
    private boolean onepayTransaction;
    private Integer originalSession;
    

	public OrderFinancialRecord(String productName, String orderUuid, String productUuid, Double price,
			Double originalPrice, Double discount, Date datePurchase, Date dateExpire, String ptUuid, String userUuid,
			String userFullName, String userUserName, String trainerName, String trainerUserName, String orderCode,
			String contractNumber, String userClub, String purchaseOrderClub, String trainerStaffCode,
			String memberBarCode, Integer totalSession, Integer freeSession, Integer burnedSession, String couponCode) {
		this.productName = productName;
        this.orderUuid = orderUuid;
        this.productUuid = productUuid;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.datePurchase = (Timestamp) datePurchase;
        this.dateExpire = (Timestamp) dateExpire;
        this.ptUuid = ptUuid;
        this.userUuid = userUuid;
        this.userFullName =userFullName;
        this.userUserName = userUserName;
        this.trainerName = trainerName;
        this.trainerUserName = trainerUserName;

        this.orderCode = orderCode;
        this.contractNumber = contractNumber;
        this.userClub = userClub;
        this.purchaseOrderClub = purchaseOrderClub;
        this.trainerStaffCode = trainerStaffCode;
        this.memberBarCode = memberBarCode;

        this.totalSession = totalSession;
        this.freeSession = freeSession;
        this.burnedSession = burnedSession;
        this.couponCode = couponCode;
        this.onepayTransaction = freeSession == 0;
        this.originalSession = freeSession == 0 ? totalSession : 0;
    }

    public String getProductName() {
        return productName;
    }


    public String getProductUuid() {
        return productUuid;
    }

    public Double getPrice() {
        return price;
    }

    public Timestamp getDatePurchase() {
        return datePurchase;
    }

    public Timestamp getDateExpire() {
        return dateExpire;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

	public String getUserFullName() {
		return userFullName;
	}

	public String getUserUserName() {
		return userUserName;
	}

	public String getTrainerName() {
		return trainerName;
	}

	public String getTrainerUserName() {
		return trainerUserName;
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

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public Integer getTotalSession() {
        return totalSession;
    }

    public Integer getFreeSession() {
        return freeSession;
    }

    public Integer getBurnedSession() {
        return burnedSession;
    }

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
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
}
