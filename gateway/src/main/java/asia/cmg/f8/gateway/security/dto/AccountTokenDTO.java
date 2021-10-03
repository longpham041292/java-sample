package asia.cmg.f8.gateway.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountTokenDTO {

	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("expired_at")
	private Long expiredAt = 0L;
	
	@JsonProperty("refresh_expired_at")
	private Long refreshExpiredAt = 0L;
	
	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("password")
	private String password;

	public AccountTokenDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public AccountTokenDTO(String userUuid, long expiredAt, long refreshExpiredAt, String accessToken, String refreshToken ,String password) {
		this.uuid = userUuid;
		this.expiredAt = expiredAt;
		this.refreshExpiredAt = refreshExpiredAt;
		this.accessToken = accessToken;
		this.password = password;
		this.refreshToken = refreshToken;
	}
	
	public AccountTokenDTO(String userUuid, long expiredAt, String accessToken, String refreshToken) {
		this.uuid = userUuid;
		this.expiredAt = expiredAt;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
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

	public Long getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(Long expiredAt) {
		this.expiredAt = expiredAt;
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

	public Long getRefreshExpiredAt() {
		return refreshExpiredAt;
	}

	public void setRefreshExpiredAt(Long refreshExpiredAt) {
		this.refreshExpiredAt = refreshExpiredAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
