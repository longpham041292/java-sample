package asia.cmg.f8.report.entity.database;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;

@Entity
@Table(name = "orders", indexes = { @Index(name = "IDX_USER_UUID", columnList = "uuid")})
@SuppressWarnings({"PMD", "squid:S2384"})
public class OrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "start_session_date")
    private LocalDateTime startSessionDate;

    @Column(name = "sub_total", scale = 2, nullable = false)
    private Double subTotal = 0d;

    @Column(name = "total_price", scale = 2, nullable = false)
    private Double totalPrice = 0d;

    @Column(name = "discount", scale = 2, nullable = false)
    private Double discount = 0d;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "vn";

    @Column(name = "user_uuid", length = 36, nullable = false)
    private String userUuid;

    @Column(name = "pt_uuid", length = 36, nullable = false)
    private String ptUuid;

    @Column(name = "clubcode")
    private String clubcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 24, nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 24)
    private PaymentStatus paymentStatus;
    
    @Column(name = "couponCode", nullable = true)
	private String couponCode;

	@Column(name = "contract_number", nullable = true)
	private String contractNumber;

	@Column(name = "type", nullable = true)
	private String type = OrderType.PRODUCT.toString();

	@Column(name = "free_order", nullable = false, columnDefinition = "boolean default false")
    private boolean freeOrder;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, targetEntity = OrderEntryEntity.class)
    private final List<OrderEntryEntity> orderProductEntries = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, targetEntity = OrderSubscriptionEntryEntity.class)
    private final List<OrderSubscriptionEntryEntity> orderSubscriptionEntries = new ArrayList<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, targetEntity = OrderCreditEntryEntity.class)
    private final List<OrderCreditEntryEntity> orderCreditPackageEntries = new ArrayList<OrderCreditEntryEntity>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            targetEntity = PaymentTransactionEntity.class)
    private final List<PaymentTransactionEntity> paymentTransactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, targetEntity = OrderCouponEntryEntity.class)
    private final List<OrderCouponEntryEntity> orderCouponEntryEntity = new ArrayList<OrderCouponEntryEntity>();

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(final LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(final Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

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

    @SuppressWarnings("squid:S2384")
    public List<OrderEntryEntity> getOrderProductEntries() {
        return orderProductEntries;
    }

    public void addOrderProductEntry(final OrderEntryEntity entry) {
        this.orderProductEntries.add(entry);
        entry.setOrder(this);
    }

    public List<OrderSubscriptionEntryEntity> getOrderSubscriptionEntries() {
        return orderSubscriptionEntries;
    }

    public void addOrderSubscriptionEntry(final OrderSubscriptionEntryEntity entry) {
        this.orderSubscriptionEntries.add(entry);
        entry.setOrder(this);
    }
    
    public void addOrderCreditEntry(final OrderCreditEntryEntity entry) {
    	this.orderCreditPackageEntries.add(entry);
    	entry.setOrder(this);
    }
   
    @SuppressWarnings("squid:S2384")
    public List<PaymentTransactionEntity> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void addPaymentTransaction(final PaymentTransactionEntity entity) {
        this.paymentTransactions.add(entity);
        entity.setOrder(this);
    }
    
    @SuppressWarnings("squid:S2384")
    public List<OrderCouponEntryEntity> getOrderCouponEntryEntity() {
        return orderCouponEntryEntity;
    }

    public void addOrderCouponEntryEntity(final OrderCouponEntryEntity entity) {
        this.orderCouponEntryEntity.add(entity);
        entity.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getStartSessionDate() {
        return startSessionDate;
    }

    public void setStartSessionDate(final LocalDateTime startSessionDate) {
        this.startSessionDate = startSessionDate;
    }

    public boolean getFreeOrder() {
        return freeOrder;
    }

    public void setFreeOrder(final boolean freeOrder) {
        this.freeOrder = freeOrder;
    }

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

    public String getClubcode() {
        return clubcode;
    }

    public void setClubcode(String clubcode) {
        this.clubcode = clubcode;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<OrderCreditEntryEntity> getOrderCreditPackageEntries() {
		return orderCreditPackageEntries;
	}
}
