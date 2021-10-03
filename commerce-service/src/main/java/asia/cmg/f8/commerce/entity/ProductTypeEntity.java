package asia.cmg.f8.commerce.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "product_type",
        uniqueConstraints = @UniqueConstraint(columnNames = {"level_code", "country"}))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProductTypeEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "level_code", nullable = false)
    private LevelEntity level;

    @Column(nullable = false)
    private Double commision;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(length = 300)
    private String description;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LevelEntity getLevel() {
        return level;
    }

    public void setLevel(final LevelEntity level) {
        this.level = level;
    }

    public Double getCommision() {
        return commision;
    }

    public void setCommision(final Double commision) {
        this.commision = commision;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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

}
