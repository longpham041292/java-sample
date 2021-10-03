package asia.cmg.f8.session.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "session_session_daily_mv", indexes = @Index(name = "IDX_SESSION_UUID",
        columnList = "session_created_local_date"), uniqueConstraints = @UniqueConstraint(
        columnNames = { "session_created_local_date", "session_uuid" }))
public class MVSessionDailyEntity {

    private Long id;
    private LocalDate sessionCreatedLocalDate;
    private LocalDateTime sessionCreatedTime;
    private SessionStatus status;
    private String sessionUuid;
    private String ptUuid;
    private String packageUuid;
    private String userUuid;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionStatus status) {
        this.status = status;
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

    @Column(name = "session_created_local_date")
    public LocalDate getSessionCreatedLocalDate() {
        return sessionCreatedLocalDate;
    }

    public void setSessionCreatedLocalDate(final LocalDate sessionCreatedLocalDate) {
        this.sessionCreatedLocalDate = sessionCreatedLocalDate;
    }

    @Column(name = "session_created_time")
    public LocalDateTime getSessionCreatedTime() {
        return sessionCreatedTime;
    }

    public void setSessionCreatedTime(final LocalDateTime sessionCreatedTime) {
        this.sessionCreatedTime = sessionCreatedTime;
    }

}
