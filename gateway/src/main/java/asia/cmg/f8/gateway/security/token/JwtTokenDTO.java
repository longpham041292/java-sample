package asia.cmg.f8.gateway.security.token;

public class JwtTokenDTO {
	private String userUuid;
	private Long issueAt;
	private Long expireAt;
	private Long refreshExpireAt;
	
	public JwtTokenDTO(String userUuid, Long issueAt, Long expireAt, Long refreshExpireAt) {
		this.userUuid = userUuid;
		this.issueAt = issueAt;
		this.expireAt = expireAt;
		this.refreshExpireAt = refreshExpireAt;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Long getIssueAt() {
		return issueAt;
	}

	public void setIssueAt(Long issueAt) {
		this.issueAt = issueAt;
	}

	public Long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Long expireAt) {
		this.expireAt = expireAt;
	}

	public Long getRefreshExpireAt() {
		return refreshExpireAt;
	}

	public void setRefreshExpireAt(Long refreshExpireAt) {
		this.refreshExpireAt = refreshExpireAt;
	}
	
	public boolean isAccessTokenExpired() {
		if(expireAt == null) {
			return true;
		}
		return expireAt <= System.currentTimeMillis() ? true : false;
	}
	
	public boolean isRefreshTokenExpired() {
		if(refreshExpireAt == null) {
			return true;
		}
		return refreshExpireAt <= System.currentTimeMillis() ? true : false;
	}
}
