package asia.cmg.f8.commerce.entity;

import asia.cmg.f8.commerce.constants.PaymentType;
import asia.cmg.f8.commerce.dto.PaymentTransactionType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "payment_transaction")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SuppressWarnings("squid:S2384")
public class PaymentTransactionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "payment_provider", length = 32)
    private String paymentProvider;

    @Column(name = "request_id")
    private String requestId;
    
    @Column(name = "instrumentId")
    private Long instrumentId;

    @Column(name = "request_token")
    private String requestToken;

    @Column(name = "total_price", scale = 2, nullable = false)
    private Double amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 12, nullable = false)
    private PaymentTransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20)
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL,
            targetEntity = PaymentTransactionEntryEntity.class)
    private final List<PaymentTransactionEntryEntity> entries = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(final String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(final String requestToken) {
        this.requestToken = requestToken;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public PaymentTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final PaymentTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(final OrderEntity order) {
        this.order = order;
    }

    public List<PaymentTransactionEntryEntity> getEntries() {
        return entries;
    }

    public void addTransactionEntry(final PaymentTransactionEntryEntity entry) {
        this.entries.add(entry);
        entry.setTransaction(this);
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final PaymentType paymentType) {
        this.paymentType = paymentType;
    }

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}
    
}
