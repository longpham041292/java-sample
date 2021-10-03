package asia.cmg.f8.session.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Created on 11/21/16.
 */
@Entity
@Table(name = "session_sessions")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SuppressWarnings("squid:S2384")
public class SessionEntity extends AbstractEntity {

	@Column(name = "user_uuid", length = 36, nullable = false)
    private String userUuid;
	
	@Column(name = "pt_uuid", length = 36, nullable = false)
    private String ptUuid;
	
	@Column(name = "package_uuid", length = 36, nullable = false)
    private String packageUuid;
	
	@Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
	
	@Column(name = "start_time")
    private LocalDateTime startTime; // the current start time. It's equal with start time of the current linked event.
	
	@Column(name = "end_time")
    private LocalDateTime endTime; // the current end time. It's equal with end time of the current linked event.

    /**
     * Latest event id
     */
	@Column(name = "user_event_uuid", length = 36)
    private String userEventId;
	
	@Column(name = "pt_event_uuid", length = 36)
    private String ptEventId;

    /**
     * TODO: Three fields: lastStatus, lastStatusModifiedDate, statusModifiedDate are created for scoped report. Need to rework in the future.
     */
	@Column(name = "last_status")
    @Enumerated(EnumType.STRING)
    private SessionStatus lastStatus;
	
	@Column(name = "last_status_modified_date")
    private LocalDateTime lastStatusModifiedDate;
    
    @Column(name = "status_modified_date")
    private LocalDateTime statusModifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private BasicUserEntity user;
    
    @Column(name = "booked_by", length = 5)
    private String bookedBy; // supported values eu or pt
    
    @Column(name = "crm_synced_message")
    private String crmSyncedMessage;
    
    @Column(name = "booking_club_uuid", nullable = true, length = 100)
    private String bookingClubUuid;
    
    @Column(name = "booking_club_name", nullable = true, length = 255)
    private String bookingClubName;
    
    @Column(name = "booking_club_address", nullable = true, length = 1000)
    private String bookingClubAddress;
    
    @Column(name = "checkin_club_uuid", nullable = true, length = 50)
    private String checkinClubUuid;
    
    @Column(name = "checkin_club_name", nullable = true, length = 255)
    private String checkinClubName;
    
    @Column(name = "checkin_club_address", nullable = true, length = 1000)
    private String checkinClubAddress;
    
    @Column(name = "checkin_date")
    private LocalDateTime checkinDate;
    
    @Column(name = "checkout_date")
    private LocalDateTime checkoutDate;
    
	public String getBookingClubUuid() {
		return bookingClubUuid;
	}

	public void setBookingClubUuid(String bookingClubUuid) {
		this.bookingClubUuid = bookingClubUuid;
	}

	public String getBookingClubName() {
		return bookingClubName;
	}

	public void setBookingClubName(String bookingClubName) {
		this.bookingClubName = bookingClubName;
	}

	public String getBookingClubAddress() {
		return bookingClubAddress;
	}

	public void setBookingClubAddress(String bookingClubAddress) {
		this.bookingClubAddress = bookingClubAddress;
	}

	public String getCheckinClubUuid() {
		return checkinClubUuid;
	}

	public void setCheckinClubUuid(String checkinClubUuid) {
		this.checkinClubUuid = checkinClubUuid;
	}

	public String getCheckinClubName() {
		return checkinClubName;
	}

	public void setCheckinClubName(String checkinClubName) {
		this.checkinClubName = checkinClubName;
	}

	public String getCheckinClubAddress() {
		return checkinClubAddress;
	}

	public void setCheckinClubAddress(String checkinClubAddress) {
		this.checkinClubAddress = checkinClubAddress;
	}

	public LocalDateTime getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(LocalDateTime checkinDate) {
		this.checkinDate = checkinDate;
	}

	public LocalDateTime getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(LocalDateTime checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public String getPackageUuid() {
        return packageUuid;
    }

    public void setPackageUuid(final String packageUuid) {
        this.packageUuid = packageUuid;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getUserEventId() {
        return userEventId;
    }

    public void setUserEventId(final String userEventId) {
        this.userEventId = userEventId;
    }

    public String getPtEventId() {
        return ptEventId;
    }

    public void setPtEventId(final String ptEventId) {
        this.ptEventId = ptEventId;
    }

    public BasicUserEntity getUser() {
        return user;
    }

    public void setUser(final BasicUserEntity user) {
        this.user = user;
    }

    public LocalDateTime getStatusModifiedDate() {
        return statusModifiedDate;
    }

    public void setStatusModifiedDate(final LocalDateTime statusModifiedDate) {
        this.statusModifiedDate = statusModifiedDate;
    }

    public LocalDateTime getLastStatusModifiedDate() {
        return lastStatusModifiedDate;
    }

    public void setLastStatusModifiedDate(final LocalDateTime lastStatusModifiedDate) {
        this.lastStatusModifiedDate = lastStatusModifiedDate;
    }

    public SessionStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(final SessionStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(final String bookedBy) {
        this.bookedBy = bookedBy;
    }

	public String getCrmSyncedMessage() {
		return crmSyncedMessage;
	}

	public void setCrmSyncedMessage(String crmSyncedMessage) {
		this.crmSyncedMessage = crmSyncedMessage;
	}

}
