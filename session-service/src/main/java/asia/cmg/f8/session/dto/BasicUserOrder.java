package asia.cmg.f8.session.dto;

import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.utils.ReportUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tung.nguyenthanh
 */
@SuppressWarnings("PMD.TooManyFields")
public class BasicUserOrder {

    private String uuid;

    private String name;

    private String avatar;

    private String city;

    private String country;

    @JsonProperty("session_burned")
    private Integer sessionBurned;
    
    @JsonProperty("session_confirmed")
    private Integer sessionConfirmed;

    @JsonProperty("session_number")
    private Integer sessionNumber;

    @JsonProperty("expired_date")
    private Long expireDate;

    @JsonProperty("display_expired_date")
    private String displayExpiredDate;

    @JsonProperty("package_session_uuid")
    private String packageSessionUuid;

    @JsonProperty("package_session_status")
    private SessionPackageStatus packageSessionstatus;

    @JsonProperty("package_session_trainer_uuid")
    private String currentPTPackageSession;

    private Boolean activated;

    private String email;
    
    private String phone;

    public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@JsonProperty("join_date")
    private Long joinDate;

    @JsonProperty("display_join_date")
    private String displayJoinDate;
    
    @JsonProperty("join_time")
    private String joinTime;
    
   
    private String userName;
    
    @JsonProperty("user_code")
    private String userCode; 
    
    private String memberCode;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getCountry() {
        return ReportUtils.getLocalizeCountry(country);
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getActivated() {
        return activated ? ReportUtils.PACKAGE_ACTIVE_STATUS : ReportUtils.PACKAGE_INACTIVE_STATUS;
    }

    public void setActivated(final Boolean activated) {
        this.activated = activated;
    }

    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public void setSessionBurned(final Integer sessionBurned) {
        this.sessionBurned = sessionBurned;
    }
    
    public Integer getSessionConfirmed() {
        return sessionConfirmed;
    }

    public void setSessionConfirmed(final Integer sessionConfirmed) {
        this.sessionConfirmed = sessionConfirmed;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(final Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(final Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayExpiredDate() {
        return getExpireDate() == null ? ReportUtils.NOT_START : displayExpiredDate;
    }

    public void setDisplayExpiredDate(final String displayExpiredDate) {
        this.displayExpiredDate = displayExpiredDate;
    }

    public String getPackageSessionUuid() {
        return packageSessionUuid;
    }

    public void setPackageSessionUuid(final String packageSessionUuid) {
        this.packageSessionUuid = packageSessionUuid;
    }

    public SessionPackageStatus getPackageSessionstatus() {
        return packageSessionstatus;
    }

    public void setPackageSessionstatus(final SessionPackageStatus packageSessionstatus) {
        this.packageSessionstatus = packageSessionstatus;
    }

    public String getCurrentPTPackageSession() {
        return currentPTPackageSession;
    }

    public void setCurrentPTPackageSession(final String currentPTPackageSession) {
        this.currentPTPackageSession = currentPTPackageSession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(final Long joinDate) {
        this.joinDate = joinDate;
    }

    public String getDisplayJoinDate() {
        return displayJoinDate;
    }

    public void setDisplayJoinDate(final String displayJoinDate) {
        this.displayJoinDate = displayJoinDate;
    }

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(final String joinTime) {
		this.joinTime = joinTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode( final String userCode) {
		this.userCode = userCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(final String memberCode) {
		this.memberCode = memberCode;
	}
	
	

}
