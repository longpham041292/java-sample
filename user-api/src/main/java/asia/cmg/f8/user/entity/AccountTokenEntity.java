/**
 * 
 */
package asia.cmg.f8.user.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 08/02/20.
 */
@Entity
@Table(name = "account_token", uniqueConstraints = @UniqueConstraint(name = "uuid_UN", columnNames = {"uuid"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccountTokenEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid")
	private String uuid;
	
	@Column(name = "expired_at", columnDefinition = "bigint not null default 0")
	@JsonProperty("expired_at")
	private Long expiredAt = 0L;
	
	@Column(name = "refresh_expired_at", columnDefinition = "bigint not null default 0")
	@JsonProperty("refresh_expired_at")
	private Long refreshExpiredAt = 0L;
	
	@JsonProperty("access_token")
	@Column(name = "access_token", length = 1000)
	private String accessToken;
	
	@Column(name = "refresh_token", length = 1000)
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("password")
	@Column(name = "password")
	private String password;
	
	@JsonProperty("reset_password_token")
	@Column(name = "reset_password_token")
	private String resetPasswordToken;
	
	@JsonProperty("reset_password_expired")
	@Column(name = "reset_password_expired")
	private Long resetPasswordExpired;
	
	@JsonIgnore
	@CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
	@JsonIgnore
    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
	
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

	public Long getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(Long expiredAt) {
		this.expiredAt = expiredAt;
	}
	
	public Long getRefreshExpiredAt() {
		return refreshExpiredAt;
	}

	public void setRefreshExpiredAt(Long refreshExpiredAt) {
		this.refreshExpiredAt = refreshExpiredAt;
	}

	public AccountTokenEntity(String uuid, String accessToken, String refreshToken) {
		super();
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public AccountTokenEntity(String uuid, String accessToken, String refreshToken, long expiredAt) {
		super();
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiredAt = expiredAt;
	}
	public AccountTokenEntity(String uuid, String accessToken, String refreshToken, long expiredAt, long refreshExpiredAt) {
		super();
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiredAt = expiredAt;
		this.refreshExpiredAt = refreshExpiredAt;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public AccountTokenEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Long getResetPasswordExpired() {
		return resetPasswordExpired;
	}

	public void setResetPasswordExpired(Long resetPasswordExpired) {
		this.resetPasswordExpired = resetPasswordExpired;
	}
	
}
