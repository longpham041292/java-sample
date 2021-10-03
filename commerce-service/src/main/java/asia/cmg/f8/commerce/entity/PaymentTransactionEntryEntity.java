package asia.cmg.f8.commerce.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import asia.cmg.f8.commerce.dto.PaymentTransactionEntryType;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payment_transaction_entry")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SuppressWarnings("PMD.TooManyFields")
public class PaymentTransactionEntryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 12, nullable = false)
    private PaymentTransactionEntryType transactionType;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "request_id", length = 40, nullable = false)
    private String requestId;

    @Column(name = "request_token")
    private String requestToken;
    
    @Column(name = "request_Data", length = 2048)
    private String requestData;
    
    @Column(name = "version_id", length = 2)
    private String versionId;

    @Column(name = "total_price", scale = 2)
    private Double amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "transaction_resp_code", length = 3)
    private String transactionRespCode;
    
    @Column(name = "transaction_no", length = 255)
    private String transactionNo;
    
    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "transaction_status_detail")
    private String transactionStatusDetail;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentTransactionEntity transaction;

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

    public PaymentTransactionEntryType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final PaymentTransactionEntryType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(final LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(final String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionStatusDetail() {
        return transactionStatusDetail;
    }

    public void setTransactionStatusDetail(final String transactionStatusDetail) {
        this.transactionStatusDetail = transactionStatusDetail;
    }

    public PaymentTransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(final PaymentTransactionEntity transaction) {
        this.transaction = transaction;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(final String requestData) {
        this.requestData = requestData;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(final String versionId) {
        this.versionId = versionId;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(final String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionRespCode() {
        return transactionRespCode;
    }

    public void setTransactionRespCode(final String transactionRespCode) {
        this.transactionRespCode = transactionRespCode;
    }

}
