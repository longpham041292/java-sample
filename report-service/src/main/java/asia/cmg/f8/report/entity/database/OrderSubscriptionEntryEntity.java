package asia.cmg.f8.report.entity.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * This Table stand for Order Subscription Entries
 * @author nguyenpham
 *
 */
@Entity
@Table(name = "order_subscription_entries")
public class OrderSubscriptionEntryEntity extends AbstractEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "subscription_id", nullable = false)
	private SubscriptionEntity subscription;

	@Column(name = "unit_price", scale = 2, nullable = false)
    private Double unitPrice;

	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "price")
	private Double price;

	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SubscriptionEntity getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionEntity subscription) {
		this.subscription = subscription;
	}

}
