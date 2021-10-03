package asia.cmg.f8.session.entity;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@Entity
@Table(name = "session_order_reconcile_mv", indexes = @Index(name = "IDX_DATE",
        columnList = "rec_date"), uniqueConstraints = @UniqueConstraint(columnNames = { "rec_date",
        "order_uuid" }))
@SuppressWarnings("PMD.TooManyFields")
public class MVOrderReconcile {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDate recDate;
    private String orderUuid;
    private int open;
    private int pending;
    private int confirmed;
    private int eucancelled;
    private int ptcancelled;
    private int cancelled;
    private int burned;
    private int completed;
    private int expired;
    private int transferred;
    private int numOfSession;
    private boolean orderActive;
    private Double commission;
    private Double ptFee;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @CreationTimestamp
    @Column(name = "created_date")
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "rec_date", updatable = false)
    public LocalDate getRecDate() {
        return recDate;
    }

    public void setRecDate(final LocalDate recDate) {
        this.recDate = recDate;
    }

    @Column(name = "order_uuid", updatable = false)
    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(final String orderUuid) {
        this.orderUuid = orderUuid;
    }

    @Column(name = "open")
    public int getOpen() {
        return open;
    }

    public void setOpen(final int open) {
        this.open = open;
    }

    @Column(name = "pending")
    public int getPending() {
        return pending;
    }

    public void setPending(final int pending) {
        this.pending = pending;
    }

    @Column(name = "confirmed")
    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(final int confirmed) {
        this.confirmed = confirmed;
    }

    @Column(name = "eu_cancelled")
    public int getEucancelled() {
        return eucancelled;
    }

    public void setEucancelled(final int eucancelled) {
        this.eucancelled = eucancelled;
    }

    @Column(name = "pt_cancelled")
    public int getPtcancelled() {
        return ptcancelled;
    }

    public void setPtcancelled(final int ptcancelled) {
        this.ptcancelled = ptcancelled;
    }

    @Column(name = "cancelled")
    public int getCancelled() {
        return cancelled;
    }

    public void setCancelled(final int cancelled) {
        this.cancelled = cancelled;
    }

    @Column(name = "burned")
    public int getBurned() {
        return burned;
    }

    public void setBurned(final int burned) {
        this.burned = burned;
    }

    @Column(name = "completed")
    public int getCompleted() {
        return completed;
    }

    public void setCompleted(final int completed) {
        this.completed = completed;
    }

    @Column(name = "expired")
    public int getExpired() {
        return expired;
    }

    public void setExpired(final int expired) {
        this.expired = expired;
    }

    @Column(name = "transferred")
    public int getTransferred() {
        return transferred;
    }

    public void setTransferred(final int transferred) {
        this.transferred = transferred;
    }

    @Column(name = "num_of_session")
    public int getNumOfSession() {
        return numOfSession;
    }

    public void setNumOfSession(final int numOfSession) {
        this.numOfSession = numOfSession;
    }

    @Column(name = "order_active")
    public boolean isOrderActive() {
        return orderActive;
    }

    public void setOrderActive(final boolean orderActive) {
        this.orderActive = orderActive;
    }

    @Column(name = "commission", precision = 10, scale = 2)
    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }

    @Column(name = "pt_fee", precision = 10, scale = 2)
    public Double getPtFee() {
        return ptFee;
    }

    public void setPtFee(final Double ptFee) {
        this.ptFee = ptFee;
    }

}
