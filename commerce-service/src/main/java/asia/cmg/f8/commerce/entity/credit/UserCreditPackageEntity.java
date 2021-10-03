package asia.cmg.f8.commerce.entity.credit;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.OrderEntity;

@Entity
@Table(name = "user_credit_packages", uniqueConstraints = @UniqueConstraint(name = "ownerUuid_orderId_UN", columnNames = {"owner_uuid", "order_id"}))
public class UserCreditPackageEntity {

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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "owner_uuid", nullable = false, length = 50)
	@JsonProperty("owner_uuid")
	private String ownerUuid;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", nullable = false)
	@JsonProperty("order_id")
	private OrderEntity order;
	
//	@ManyToOne(fetch = FetchType.LAZY, targetEntity = OrderCreditEntryEntity.class)
//	@JoinColumn(name = "order_credit_entry_id", nullable = false)
//	@JsonProperty("order_credit_entry_id")
//	private OrderCreditEntryEntity orderCreditEntry;
//	
//	@ManyToOne(fetch = FetchType.LAZY, targetEntity = CreditPackageEntity.class)
//	@JoinColumn(name = "credit_package_id", nullable = false)
//	@JsonProperty("credit_package_id")
//	private CreditPackageEntity creditPackage;
	
	@Column(name = "expired_date")
	@JsonProperty("expired_date")
	private LocalDateTime expiredDate;
	
	@Column(name = "total_credit", columnDefinition = "int not null default 0")
	@JsonProperty("total_credit")
	private Integer totalCredit;
	
	@Column(name = "used_credit", columnDefinition = "int not null default 0")
	@JsonProperty("used_credit")
	private Integer usedCredit = 0;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonIgnore
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private LocalDateTime modifiedDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

//	public OrderCreditEntryEntity getOrderCreditEntry() {
//		return orderCreditEntry;
//	}
//
//	public void setOrderCreditEntry(OrderCreditEntryEntity orderCreditEntry) {
//		this.orderCreditEntry = orderCreditEntry;
//	}
//
//	public CreditPackageEntity getCreditPackage() {
//		return creditPackage;
//	}
//
//	public void setCreditPackage(CreditPackageEntity creditPackage) {
//		this.creditPackage = creditPackage;
//	}

	public LocalDateTime getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(LocalDateTime expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Integer getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}

	public Integer getUsedCredit() {
		return usedCredit;
	}

	public void setUsedCredit(Integer usedCredit) {
		this.usedCredit = usedCredit;
	}
	
	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public String toString() {
		return String.format("{id: %s, owner_uuid: %s, total_credit: %s, used_credit: %s, expired_date: %s}", id, ownerUuid, totalCredit, usedCredit, expiredDate.toString());
	}
}
