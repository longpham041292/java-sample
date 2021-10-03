package asia.cmg.f8.session.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Created on 11/21/16.
 */
@Entity
@Table(name = "session_events")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventEntity extends AbstractEntity {

	@Column(name = "owner_uuid", length = 36)
    private String ownerId; // uuid of owner
	
	@Column(name = "session_uuid")
    private String sessionUuid;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "booked_by", length = 5)
    private String bookedBy; // supported values eu or pt
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    
    @Column(name = "club_uuid", nullable = true, length = 100)
    private String clubUuid;
    
    @Column(name = "club_name", nullable = true, length = 255)
    private String clubName;
    
    @Column(name = "club_address", nullable = true, length = 1000)
    private String clubAddress;
    
    @Column(name = "checkin_club_uuid", nullable = true, length = 50)
    private String checkinClubUuid;
    
    @Column(name = "checkin_club_name", nullable = true, length = 255)
    private String checkinClubName;
    
    @Column(name = "checkin_club_address", nullable = true, length = 1000)
    private String checkinClubAddress;
    
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

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(final String sessionUuid) {
        this.sessionUuid = sessionUuid;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionStatus status) {
        this.status = status;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(final String bookedBy) {
        this.bookedBy = bookedBy;
    }

	public String getClubUuid() {
		return clubUuid;
	}

	public void setClubUuid(String clubUuid) {
		this.clubUuid = clubUuid;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubAddress() {
		return clubAddress;
	}

	public void setClubAddress(String clubAddress) {
		this.clubAddress = clubAddress;
	}
}
