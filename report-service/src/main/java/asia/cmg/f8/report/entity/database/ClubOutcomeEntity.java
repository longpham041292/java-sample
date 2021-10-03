package asia.cmg.f8.report.entity.database;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "club_outcome")
public class ClubOutcomeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "owner_uuid")
	@JsonProperty(value = "owner_uuid")
	private String owneUuid;

	@Column(name = "credit_amount", columnDefinition = "int not null default 0")
	private Integer creditAmount;

	@Column(name = "total_package", columnDefinition = "int not null default 0")
	private Integer totalPackage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_wallet_trans_id", insertable = false, updatable = false)
	private CreditWalletTransactionEntity fromWalletTransaction;

	@Column(name = "from_wallet_trans_id", columnDefinition = "bigint not null default 0")
	private Long fromWalletTransactionId = 0L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_wallet_trans_id", insertable = false, updatable = false)
	private CreditWalletTransactionEntity toWalletTransaction;

	@Column(name = "to_wallet_trans_id", columnDefinition = "bigint not null default 0")
	private Long toWalletTransactionId = 0L;

	@Column(name = "to_date")
	@JsonProperty("to_date")
	private LocalDateTime toDate;

	@Column(name = "payment_status", columnDefinition = "int not null default 0")
	private OutComePaymentStatus paymentStatus;

	@Column(name = "payment_date")
	@JsonProperty("payment_date")
	private LocalDateTime paymentDate;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime created_date;

	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime modified_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwneUuid() {
		return owneUuid;
	}

	public void setOwneUuid(String owneUuid) {
		this.owneUuid = owneUuid;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public CreditWalletTransactionEntity getFromWalletTransaction() {
		return fromWalletTransaction;
	}

	public void setFromWalletTransaction(CreditWalletTransactionEntity fromWalletTransaction) {
		this.fromWalletTransaction = fromWalletTransaction;
	}

	public Long getFromWalletTransactionId() {
		return fromWalletTransactionId;
	}

	public void setFromWalletTransactionId(Long fromWalletTransactionId) {
		this.fromWalletTransactionId = fromWalletTransactionId;
	}

	public CreditWalletTransactionEntity getToWalletTransaction() {
		return toWalletTransaction;
	}

	public void setToWalletTransaction(CreditWalletTransactionEntity toWalletTransaction) {
		this.toWalletTransaction = toWalletTransaction;
	}

	public Long getToWalletTransactionId() {
		return toWalletTransactionId;
	}

	public void setToWalletTransactionId(Long toWalletTransactionId) {
		this.toWalletTransactionId = toWalletTransactionId;
	}

	public Integer getTotalPackage() {
		return totalPackage;
	}

	public void setTotalPackage(Integer totalPackage) {
		this.totalPackage = totalPackage;
	}

	public OutComePaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(OutComePaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

}
