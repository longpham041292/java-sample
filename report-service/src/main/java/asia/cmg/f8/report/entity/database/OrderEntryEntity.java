package asia.cmg.f8.report.entity.database;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * This table stand for Order Product Entries
 * @author nguyenpham
 *
 */
@Entity
@Table(name = "order_entries")
public class OrderEntryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_price", scale = 2, nullable = false)
    private Double unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expire_limit", nullable = false)
    private Integer expireLimit;

    @Column(name = "entry_number")
    private Integer entryNumber;

    @Column(name = "commision", scale = 2, nullable = false)
    private Double commision;

    @Column(name = "sub_total", scale = 2, nullable = false)
    private Double subTotal;

    @Column(name = "total_price", scale = 2, nullable = false)
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getExpireLimit() {
        return expireLimit;
    }

    public void setExpireLimit(final Integer expireLimit) {
        this.expireLimit = expireLimit;
    }

    public Integer getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(final Integer entryNumber) {
        this.entryNumber = entryNumber;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getCommision() {
        return commision;
    }

    public void setCommision(final Double commision) {
        this.commision = commision;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(final Double subTotal) {
        this.subTotal = subTotal;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(final OrderEntity order) {
        this.order = order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(final ProductEntity product) {
        this.product = product;
    }

}
