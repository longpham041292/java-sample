/**
 * 
 */
package asia.cmg.f8.user.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Created on 08/02/20.
 */
@Entity
@Table(name = "account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid")
	private String uuid;
	
	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;
	
	@Column(name = "time_created")
	private Timestamp timeCreated;
	
	@Column(name = "time_modified")
	private Timestamp timeModified;
	
	@OneToOne
    @JoinColumn(name = "uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private UserInfoEntity userInfo;
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "apple_id")
	private String apple_id;
	
	@Column(name = "fb_id")
	private String fbId;
	
	@Column(name = "google_id")
	private String googleId;
	
	@Column(name = "phone_validated")
	private String phoneValidated;
	
	@Column(name = "email_validated")
	private String emailValidated;
	
	@Column(name = "token_access")
	private String tokenAccess;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "roles")
	private int roles;

	public int getRoles() {
		return roles;
	}

	public void setRoles(int roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Timestamp timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Timestamp getTimeModified() {
		return timeModified;
	}

	public void setTimeModified(Timestamp timeModified) {
		this.timeModified = timeModified;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApple_id() {
		return apple_id;
	}

	public void setApple_id(String apple_id) {
		this.apple_id = apple_id;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getPhoneValidated() {
		return phoneValidated;
	}

	public void setPhoneValidated(String phoneValidated) {
		this.phoneValidated = phoneValidated;
	}

	public String getEmailValidated() {
		return emailValidated;
	}

	public void setEmailValidated(String emailValidated) {
		this.emailValidated = emailValidated;
	}

	public String getTokenAccess() {
		return tokenAccess;
	}

	public void setTokenAccess(String tokenAccess) {
		this.tokenAccess = tokenAccess;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UserInfoEntity getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfoEntity userInfo) {
		this.userInfo = userInfo;
	}

	public AccountEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
