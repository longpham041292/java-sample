package asia.cmg.f8.session.entity;

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
@Table(name = "session_session_stats_daily_mv", indexes = @Index(name = "IDX_SESSION_UUID",
        columnList = "stats_local_date"), uniqueConstraints = @UniqueConstraint(columnNames = {
        "stats_local_date", "session_uuid" }))
@SuppressWarnings("PMD.TooManyFields")
public class MVSessionStatsDailyEntity {

    private Long id;
    private LocalDate statsLocalDate;
    private LocalDateTime statsDate;
    private String sessionUuid;
    private String userUuid;
    private String ptUuid;
    private String oldPtUuid;
    private String packageUuid;
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

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Column(name = "session_uuid", length = 36, nullable = false)
    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(final String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    @Column(name = "user_uuid", length = 36, nullable = false)
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }


    @Column(name = "pt_uuid", length = 36, nullable = false)
    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    @Column(name = "package_uuid", length = 36, nullable = false)
    public String getPackageUuid() {
        return packageUuid;
    }

    public void setPackageUuid(final String packageUuid) {
        this.packageUuid = packageUuid;
    }

    @Column(name = "stats_local_date")
    public LocalDate getStatsLocalDate() {
        return statsLocalDate;
    }

    public void setStatsLocalDate(final LocalDate statsLocalDate) {
        this.statsLocalDate = statsLocalDate;
    }

    @Column(name = "stats_date")
    public LocalDateTime getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(final LocalDateTime statsDate) {
        this.statsDate = statsDate;
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

    @Column(name = "old_pt_uuid")
    public String getOldPtUuid() {
        return oldPtUuid;
    }

    public void setOldPtUuid(final String oldPtUuid) {
        this.oldPtUuid = oldPtUuid;
    }

}
