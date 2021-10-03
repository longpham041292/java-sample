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
@Table(name="user_credit_packages_transactions")
public class UserCreditPackageTransactionEntity {
	
	public Long getCreditWalletTransactionId() {
		return creditWalletTransactionId;
	}

	public void setCreditWalletTransactionId(Long creditWalletTransactionId) {
		this.creditWalletTransactionId = creditWalletTransactionId;
	}

	public Long getUserCreditPackageId() {
		return userCreditPackageId;
	}

	public void setUserCreditPackageId(Long userCreditPackageId) {
		this.userCreditPackageId = userCreditPackageId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonIgnore
	private LocalDateTime createdDate;
	
	@Column(name = "owner_uuid", nullable = false, length = 50)
	@JsonProperty("owner_uuid")
	private String ownerUuid;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private LocalDateTime modifiedDate;
	
	@Column(name = "user_credit_package_id", columnDefinition = "int not null default 0")
	@JsonProperty("user_credit_package_id")
	private Long userCreditPackageId;
	
	@Column(name = "credit_wallet_transaction_id", columnDefinition = "int not null default 0")
	@JsonProperty("credit_wallet_transaction_id")
	private Long creditWalletTransactionId;
	
	
	@Column(name = "current_credit", columnDefinition = "int not null default 0")
	@JsonProperty("current_credit")
	private Integer currentCredit = 0;
	
	@Column(name = "used_credit", columnDefinition = "int not null default 0")
	@JsonProperty("used_credit")
	private Integer usedCredit = 0; 

	@Column(name = "remaining_credit", columnDefinition = "int not null default 0")
	@JsonProperty("remaining_credit")
	private Integer remainingCredit = 0; 
	
	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getCurrentCredit() {
		return currentCredit;
	}

	public void setCurrentCredit(Integer currentCredit) {
		this.currentCredit = currentCredit;
	}

	public Integer getUsedCredit() {
		return usedCredit;
	}

	public void setUsedCredit(Integer usedCredit) {
		this.usedCredit = usedCredit;
	}

	public Integer getRemainingCredit() {
		return remainingCredit;
	}

	public void setRemainingCredit(Integer remainingCredit) {
		this.remainingCredit = remainingCredit;
	}
}
