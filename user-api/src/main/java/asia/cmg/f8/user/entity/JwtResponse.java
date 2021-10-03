package asia.cmg.f8.user.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 08/02/21.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtResponse {

	
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_expires_in")
    private int accessExpiresIn;
    
    @JsonProperty("refresh_expires_in")
    private int refreshExpiresIn;
    
    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("user")
    private UserInfoEntity user;
    
    @JsonProperty("status")
    private String status;
    
	public UserInfoEntity getUser() {
		return user;
	}

	public void setUser(UserInfoEntity user) {
		this.user = user;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getStatus() {
		return status;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}



	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}



	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


	public int getAccessExpiresIn() {
		return accessExpiresIn;
	}

	public void setAccessExpiresIn(int accessExpiresIn) {
		this.accessExpiresIn = accessExpiresIn;
	}

	public int getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(int refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}

	public JwtResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JwtResponse(String accessToken, String refreshToken, int accessExpiresIn, int refreshExpiresIn,
			String status, List<String> roles,UserInfoEntity user) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessExpiresIn = accessExpiresIn;
		this.refreshExpiresIn = refreshExpiresIn;
		this.status = status;
		this.roles = roles;
		this.user = user;
	}




}
