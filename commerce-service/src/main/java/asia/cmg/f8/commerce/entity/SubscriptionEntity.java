package asia.cmg.f8.commerce.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "price")
	private Double price;

	@Column(name = "number_of_month")
	private int numberOfMonth;

	@Column(name = "limit_day")
	private int limitDay;

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "subscription_type_id", insertable = false, updatable = false)
	private SubscriptionTypeEntity subscriptionType;

	@Column(name = "subscription_type_id")
	private Long subscriptionTypeId;

	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, targetEntity = OrderSubscriptionEntryEntity.class)
	private final List<OrderSubscriptionEntryEntity> orderSubscriptionEntries = new ArrayList<>();

	@Column(name = "status", length = 256, nullable = false)
	private String status;

	@Column(name = "country", nullable = false, length = 2)
	private String country;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	public SubscriptionTypeEntity getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionTypeEntity subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public List<OrderSubscriptionEntryEntity> getOrderSubscriptionEntries() {
		return orderSubscriptionEntries;
	}

	public void addOrderSubscriptionEntry(final OrderSubscriptionEntryEntity entry) {
		this.orderSubscriptionEntries.add(entry);
		entry.setSubscription(this);
	}

	public int getLimitDay() {
		return limitDay;
	}

	public void setLimitDay(int limitDay) {
		this.limitDay = limitDay;
	}

	public int getNumberOfMonth() {
		return numberOfMonth;
	}

	public void setNumberOfMonth(int numberOfMonth) {
		this.numberOfMonth = numberOfMonth;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubscriptionTypeId() {
		return subscriptionTypeId;
	}

	public void setSubscriptionTypeId(Long subscriptionTypeId) {
		this.subscriptionTypeId = subscriptionTypeId;
	}
}
