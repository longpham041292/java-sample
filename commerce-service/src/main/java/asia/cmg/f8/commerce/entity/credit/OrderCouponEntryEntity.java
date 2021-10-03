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

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.OrderEntity;

@Entity
@Table(name = "order_coupon_entries")
public class OrderCouponEntryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "credit_coupon_id", nullable = false)
	@JsonProperty("credit_coupon")
	private CreditCouponEntity creditCoupon;

	@Column(name = "quantity", columnDefinition = "int not null default 1")
	private Integer quantity = 1;

	@Column(name = "unit_price", columnDefinition = "double not null default 0")
	@JsonProperty("unit_price")
	private Double unitPrice = 0d;

	@Column(name = "owner_uuid")
	@JsonProperty("owner_uuid")
	private String ownerUuid;

	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	@JsonIgnore
	private LocalDateTime createdDate;

	@CreationTimestamp
	@Column(name = "modified_date", nullable = false)
	@JsonIgnore
	private LocalDateTime modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public CreditCouponEntity getCreditCoupon() {
		return creditCoupon;
	}

	public void setCreditCoupon(CreditCouponEntity creditCoupon) {
		this.creditCoupon = creditCoupon;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

}
