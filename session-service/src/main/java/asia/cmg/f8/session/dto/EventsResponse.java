package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.SessionType;

/**
 * Created on 11/27/16.
 */
public class EventsResponse {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("time_slot")
    @NotNull
    private TimeSlot timeSlot;

    @JsonProperty("user_info")
    private BasicUserInfo endUser;

    @JsonProperty("trainer_info")
    private BasicUserInfo trainer;

    @JsonProperty("session_status")
    private SessionStatus status;

    @JsonProperty("package_info")
    private SessionPackageInfo packageInfo;

    @JsonProperty("booked_by")
    private String bookedBy;

    @JsonProperty("title")
    private String title;

    @JsonProperty("session_type")
    private SessionType sessionType;
    
    @JsonProperty("expired_date")
    private Long expiredDate;
    
    @JsonProperty("club_info")
    private ClubDto clubInfo;
    
    @JsonProperty("checkin_club")
    private ClubDto checkinClub;

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(final TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionStatus status) {
        this.status = status;
    }

    public BasicUserInfo getEndUser() {
        return endUser;
    }

    public void setEndUser(final BasicUserInfo endUser) {
        this.endUser = endUser;
    }

    public BasicUserInfo getTrainer() {
        return trainer;
    }

    public void setTrainer(final BasicUserInfo trainer) {
        this.trainer = trainer;
    }

    public SessionPackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(final SessionPackageInfo order) {
        this.packageInfo = order;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(final String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public Long getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Long expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getTitle() { return title; }

    public void setTitle(final String title) { this.title = title; }

    public SessionType getSessionType() { return sessionType; }

    public void setSessionType(final SessionType sessionType) { this.sessionType = sessionType; }

	public ClubDto getClubInfo() {
		return clubInfo;
	}

	public void setClubInfo(ClubDto clubInfo) {
		this.clubInfo = clubInfo;
	}

	public ClubDto getCheckinClub() {
		return checkinClub;
	}

	public void setCheckinClub(ClubDto checkinClub) {
		this.checkinClub = checkinClub;
	}
}
