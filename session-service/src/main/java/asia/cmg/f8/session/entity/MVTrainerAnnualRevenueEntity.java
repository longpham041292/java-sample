package asia.cmg.f8.session.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "session_trainer_revenue_mv",
        indexes = @Index(name = "IDX_YEAR", columnList = "year"),
        uniqueConstraints = @UniqueConstraint(columnNames = { "year", "pt_uuid" }))
public class MVTrainerAnnualRevenueEntity {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime lastRun;
    private int year;
    private String ptUuid;
    private double ptTotalFee;
    private double totalCommission;

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
    @Column(name = "created_date", updatable = false)
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @UpdateTimestamp
    @Column(name = "last_modified_date")
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Column(name = "year", nullable = false, updatable = false)
    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    @Column(name = "pt_uuid", nullable = false, length = 36)
    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    @Column(name = "pt_total_fee", precision = 12, scale = 2)
    public double getPtTotalFee() {
        return ptTotalFee;
    }

    public void setPtTotalFee(final double ptTotalFee) {
        this.ptTotalFee = ptTotalFee;
    }

    @Column(name = "total_commission", precision = 12, scale = 2)
    public double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(final double totalCommission) {
        this.totalCommission = totalCommission;
    }

    @Column(name = "last_run")
    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(final LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

}
