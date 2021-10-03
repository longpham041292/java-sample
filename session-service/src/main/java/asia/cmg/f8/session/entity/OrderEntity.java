package asia.cmg.f8.session.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;

/**
 * Created on 12/21/16.
 */
@Entity
@Table(name = "session_orders", uniqueConstraints = {
        @UniqueConstraint(name = "user_per_order", columnNames = {"user_uuid", "pt_uuid", "uuid"})
})
public class OrderEntity extends AbstractEntity {

	@Column(name = "price", updatable = false)
    private Double price;
	
	@Column(name = "original_price")
    private Double originalPrice;
	
	@Column(name = "discount")
    private Double discount;
	
	@Column(name = "commission", updatable = false)
    private Double commission;
    
    @Column(name = "user_uuid", length = 36, nullable = false)
    private String userUuid;
    
    @Column(name = "pt_uuid", length = 36, nullable = false)
    private String ptUuid;
    
    @Column(name = "currency", updatable = false)
    private String currency;
    
    @Column(name = "order_code", updatable = false, unique = true)
    private String orderCode;
    
    @Column(name = "number_of_limit_day")
    private Integer numberOfLimitDay;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "expired_date")
    private LocalDateTime expiredDate;
    
    @Column(name = "product_uuid")
    private String productUuid;
    
    @Column(name = "product_name", length = 12)
    private String productName;
    
    @Column(name = "contract_number")
	private String contractNumber;
    
    @Column(name = "order_clubcode")
    private String orderClubcode;

	// It's counted based on the current order. It's not at session package level.
    @Column(name = "num_of_burned")
    private int numOfBurned;
    
    @Column(name = "num_of_sessions")
    private int numOfSessions;

    @Column(name = "free_order", nullable = false, columnDefinition = "boolean default false")
    private boolean freeOrder;
    
    @Column(name = "training_style", columnDefinition = "int default 0")
    @Enumerated(EnumType.ORDINAL)
    private ProductTrainingStyle trainingStyle = ProductTrainingStyle.OFFLINE;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public int getNumOfSessions() {
        return numOfSessions;
    }

    public void setNumOfSessions(final int numOfSessions) {
        this.numOfSessions = numOfSessions;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    public int getNumOfBurned() {
        return numOfBurned;
    }

    public void setNumOfBurned(final int numOfBurned) {
        this.numOfBurned = numOfBurned;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(final LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getNumberOfLimitDay() {
        return numberOfLimitDay;
    }

    public void setNumberOfLimitDay(final Integer numberOfLimitDay) {
        this.numberOfLimitDay = numberOfLimitDay;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(final LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(final String productUuid) {
        this.productUuid = productUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public boolean isFreeOrder() {
        return freeOrder;
    }

    public void setFreeOrder(final boolean freeOrder) {
        this.freeOrder = freeOrder;
    }

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

    public String getOrderClubcode() {
        return orderClubcode;
    }

    public void setOrderClubcode(String orderClubcode) {
        this.orderClubcode = orderClubcode;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

	public ProductTrainingStyle getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(ProductTrainingStyle trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
}
