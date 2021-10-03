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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "subscription_types",
		uniqueConstraints = @UniqueConstraint(columnNames = {"level_code", "option_id", "country"}))
public class SubscriptionTypeEntity extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "level_code", nullable = false)
    private LevelEntity level;
	
	@Column(name = "description", length = 256, nullable = true)
	private String description;
	
	@Column(name = "unit_price", nullable = false)
	private double unitPrice;
	
	@Column(nullable = false)
	private double commission;
	
	@Column(name = "country", nullable = false, length = 2)
	private String country;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "option_id", insertable = false, updatable = false)
	private OrderOptionEntity option;
	
	@Column(name = "option_id")
	private Integer optionId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LevelEntity getLevel() {
		return level;
	}

	public void setLevel(LevelEntity level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
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

	public OrderOptionEntity getOption() {
		return option;
	}

	public void setOption(OrderOptionEntity option) {
		this.option = option;
	}

	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}
}
