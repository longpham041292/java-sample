package asia.cmg.f8.commerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Created on 11/28/16.
 */
@Entity
@Table(name = "session_users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasicUserEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid")
    private String uuid;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "full_name", length = 255)
	private String fullName;

	@Column(name = "username", length = 255)
	private String userName;

	@Column(name = "activated")
	private Boolean activated;

	@Column(name = "phone")
	private String phone;

	@Column(name = "user_type", length = 5)
	private String userType;

	@Column(name = "level")
	private String level;

	@Column(name = "user_code")
	private String userCode;
	
	@Column(name = "avatar")
	private String avatar;
	
	@Column(name = "verify_phone", columnDefinition = "tinyint(1) not null default 0")
	private Boolean verifyPhone = false;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public Boolean isActivated() {
		return activated;
	}

	public void setActivated(final Boolean activated) {
		this.activated = activated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(final String userType) {
		this.userType = userType;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(final String level) {
		this.level = level;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(final String userCode) {
		this.userCode = userCode;
	}


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}

	public Boolean getActivated() {
		return activated;
	}

	public Boolean getVerifyPhone() {
		return verifyPhone;
	}

	public void setVerifyPhone(Boolean verifyPhone) {
		this.verifyPhone = verifyPhone;
	}
}
