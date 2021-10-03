package asia.cmg.f8.report.entity.database;

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

import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;

@Entity
@Table(name = "product")
public class ProductEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_of_sessions", nullable = false)
    private Integer numOfSessions;

    @Column(name = "promotion_price", nullable = false)
    private Double promotionPrice;

    @Column(name = "expire_limit", nullable = false)
    private Integer expireLimit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "level_code", nullable = false)
    private LevelEntity level;

    @Column(name = "active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "visibility", nullable = false, columnDefinition = "boolean default true")
    private Boolean visibility;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "training_style", columnDefinition = "int default 0")
    @Enumerated(EnumType.ORDINAL)
    private ProductTrainingStyle trainingStyle;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getNumOfSessions() {
        return numOfSessions;
    }

    public void setNumOfSessions(final Integer numOfSessions) {
        this.numOfSessions = numOfSessions;
    }

    public Double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(final Double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Integer getExpireLimit() {
        return expireLimit;
    }

    public void setExpireLimit(final Integer expireLimit) {
        this.expireLimit = expireLimit;
    }

    public LevelEntity getLevel() {
        return level;
    }

    public void setLevel(final LevelEntity level) {
        this.level = level;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(final Boolean visibility) {
        this.visibility = visibility;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

	public ProductTrainingStyle getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(ProductTrainingStyle trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
}
