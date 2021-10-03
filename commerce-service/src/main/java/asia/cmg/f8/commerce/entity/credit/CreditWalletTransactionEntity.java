package asia.cmg.f8.commerce.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "credit_wallet_transactions")
public class CreditWalletTransactionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "credit_wallet_id", nullable = false)
	private Long creditWalletId;
	
	@Column(name = "booking_id", columnDefinition = "bigint not null default 0")
	private Long bookingId = 0L;
	
	@Column(name = "owner_uuid", nullable = false, length = 50)
	private String ownerUuid;
	
	@Column(name = "owner_image", length = 255)
	private String ownerImage;

	@Column(name = "partner_uuid", length = 50)
	private String partnerUuid;

	@Column(name = "partner_name", length = 50)
	private String partnerName;
	
	@Column(name = "partner_code", length = 50)
	private String partnerCode;
	
	@Column(name = "partner_image", length = 255)
	private String partnerImage;
	
	@Column(name = "old_credit_balance", columnDefinition = "int not null default 0")
	private Integer oldCreditBalance = 0;
	
	@Column(name = "new_credit_balance", columnDefinition = "int not null default 0")
	private Integer newCreditBalance = 0;
	
	@Column(name = "credit_amount", columnDefinition = "int not null default 0")
	private Integer creditAmount;
	
	// Refer to enum CreditTransactionType
	@Column(name = "transaction_type", columnDefinition = "int not null default 0")
	@Enumerated(EnumType.ORDINAL)
	private CreditTransactionType transactionType = CreditTransactionType.UNDEFINED;
	
	// Refer to enum CreditTransactionStatus
	@Column(name = "transaction_status", columnDefinition = "int not null default 1")
	@Enumerated(EnumType.ORDINAL)
	private CreditTransactionStatus transactionStatus = CreditTransactionStatus.COMPLETED;
	
	@Column(name = "withdrawed", columnDefinition = "int not null default 0")
	@Enumerated(EnumType.ORDINAL)
	private WithdrawalStatus withdrawed = WithdrawalStatus.PENDING;

	@Column(name = "description")
	private String description;
	
	@Column(name = "description_params", length = 255)
	private String descriptionParams;
	
	@CreationTimestamp
	@JsonIgnore
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@JsonIgnore
	@Column(name = "modified_date")
	private LocalDateTime modified_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreditWalletId() {
		return creditWalletId;
	}

	public void setCreditWalletId(Long creditWalletId) {
		this.creditWalletId = creditWalletId;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getPartnerUuid() {
		return partnerUuid;
	}

	public void setPartnerUuid(String partnerUuid) {
		this.partnerUuid = partnerUuid;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public CreditTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(CreditTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public CreditTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(CreditTransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOldCreditBalance() {
		return oldCreditBalance;
	}

	public void setOldCreditBalance(Integer oldCreditBalance) {
		this.oldCreditBalance = oldCreditBalance;
	}

	public Integer getNewCreditBalance() {
		return newCreditBalance;
	}

	public void setNewCreditBalance(Integer newCreditBalance) {
		this.newCreditBalance = newCreditBalance;
	}
	

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getPartnerImage() {
		return partnerImage;
	}

	public void setPartnerImage(String partnerImage) {
		this.partnerImage = partnerImage;
	}

	public LocalDateTime getModified_date() {
		return modified_date;
	}

	public String getOwnerImage() {
		return ownerImage;
	}

	public void setOwnerImage(String ownerImage) {
		this.ownerImage = ownerImage;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public String getDescriptionParams() {
		return descriptionParams;
	}

	public void setDescriptionParams(String descriptionParams) {
		this.descriptionParams = descriptionParams;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	
	public WithdrawalStatus getWithdrawed() {
		return withdrawed;
	}

	public void setWithdrawed(WithdrawalStatus withdrawed) {
		this.withdrawed = withdrawed;
	}
		
}
