package asia.cmg.f8.session.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created on 12/21/16.
 */
@Entity
@Table(name = "session_session_histories", indexes = {
		@Index(name = "IDX_UUID", columnList = "session_uuid"),
		@Index(name = "IDX_CREATED_DATE", columnList = "created_date") })
public class SessionHistoryEntity {
	
    private Long id;
    private LocalDateTime createdDate;
    private SessionStatus oldStatus;
    private SessionStatus newStatus;
    private String sessionUuid;
    private String oldPtUuid;
    private String newPtUuid;
    private String oldPackageUuid;
    private String newPackageUuid;
    private SessionAction action;
    private String userUuid; // uuid of session owner

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

    @Column(name = "old_status")
    @Enumerated(EnumType.STRING)
    public SessionStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(final SessionStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    public SessionStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(final SessionStatus newStatus) {
        this.newStatus = newStatus;
    }

    @Column(name = "session_uuid", length = 36, nullable = false)
    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(final String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    @Column(name = "old_pt_uuid", length = 36, nullable = false)
    public String getOldPtUuid() {
        return oldPtUuid;
    }

    public void setOldPtUuid(final String oldPtUuid) {
        this.oldPtUuid = oldPtUuid;
    }

    @Column(name = "new_pt_uuid", length = 36, nullable = false)
    public String getNewPtUuid() {
        return newPtUuid;
    }

    public void setNewPtUuid(final String newPtUuid) {
        this.newPtUuid = newPtUuid;
    }

    @Column(name = "old_package_uuid", length = 36, nullable = false)
    public String getOldPackageUuid() {
        return oldPackageUuid;
    }

    public void setOldPackageUuid(final String oldPackageUuid) {
        this.oldPackageUuid = oldPackageUuid;
    }

    @Column(name = "new_package_uuid", length = 36, nullable = false)
    public String getNewPackageUuid() {
        return newPackageUuid;
    }

    public void setNewPackageUuid(final String newPackageUuid) {
        this.newPackageUuid = newPackageUuid;
    }

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    public SessionAction getAction() {
        return action;
    }

    public void setAction(final SessionAction action) {
        this.action = action;
    }

    @Column(name = "user_uuid", length = 36, nullable = false)
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }
}
