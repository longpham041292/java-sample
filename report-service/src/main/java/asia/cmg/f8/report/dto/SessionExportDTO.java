package asia.cmg.f8.report.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import asia.cmg.f8.common.spec.session.SessionStatus;

public class SessionExportDTO implements Serializable {

	private static final long serialVersionUID = -6261248015049775114L;
	
	private Long sessionId;
	private String sessionStartTime;
	private String sessionEndTime;
	private SessionStatus sessionStatus;
	private String userUuid;
	private String userFullname;
	private String trainerFullname;
	private String trainerLevel;
	private String bookingClubName;
	private String bookingClubAddress;
	private String checkingClubName;
	private String checkingClubAddress;
	private String checkinTime;

	public SessionExportDTO() {
	}
	
	public SessionExportDTO(Long sessionId, LocalDateTime sessionStartTime, LocalDateTime sessionEndTime, 
			SessionStatus sessionStatus, String userUuid, String userFullname, String trainerFullname, String trainerLevel,
			String bookingClubName, String bookingClubAddress, String checkingClubName, String checkingClubAddress, LocalDateTime checkinTime) {
		
		final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		
		this.sessionId = sessionId;
		this.sessionStartTime = sessionStartTime == null ? "" : sessionStartTime.format(DATE_FORMATTER);
		this.sessionEndTime = sessionEndTime == null ? "" : sessionEndTime.format(DATE_FORMATTER);
		this.sessionStatus = sessionStatus;
		this.userUuid = userUuid;
		this.userFullname = userFullname;
		this.trainerFullname = trainerFullname;
		this.trainerLevel = trainerLevel;
		this.bookingClubName = bookingClubName;
		this.bookingClubAddress = bookingClubAddress;
		this.checkingClubName = checkingClubName;
		this.checkingClubAddress = checkingClubAddress;
		this.checkinTime = checkinTime == null ? "" : checkinTime.format(DATE_FORMATTER);
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(String sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	public String getSessionEndTime() {
		return sessionEndTime;
	}

	public void setSessionEndTime(String sessionEndTime) {
		this.sessionEndTime = sessionEndTime;
	}

	public SessionStatus getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getUserFullname() {
		return userFullname;
	}

	public void setUserFullname(String userFullname) {
		this.userFullname = userFullname;
	}

	public String getTrainerFullname() {
		return trainerFullname;
	}

	public void setTrainerFullname(String trainerFullname) {
		this.trainerFullname = trainerFullname;
	}

	public String getTrainerLevel() {
		return trainerLevel;
	}

	public void setTrainerLevel(String trainerLevel) {
		this.trainerLevel = trainerLevel;
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

	public String getCheckingClubName() {
		return checkingClubName;
	}

	public void setCheckingClubName(String checkingClubName) {
		this.checkingClubName = checkingClubName;
	}

	public String getCheckingClubAddress() {
		return checkingClubAddress;
	}

	public void setCheckingClubAddress(String checkingClubAddress) {
		this.checkingClubAddress = checkingClubAddress;
	}

	public String getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(String checkinTime) {
		this.checkinTime = checkinTime;
	}
}
