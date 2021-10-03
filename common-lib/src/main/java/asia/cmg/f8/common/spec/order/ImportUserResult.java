package asia.cmg.f8.common.spec.order;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportUserResult {
	@JsonProperty("error_code")
	private int errorCode;
	
	@JsonProperty("user_code")
	@Nullable
	private String userCode;

	@JsonProperty("level")
	@Nullable
	private String level;
	
	@JsonProperty("name")
	@Nullable
	private String name;
	
	@JsonProperty("mobile")
	@Nullable
	private String mobile;
	
	@JsonProperty("club")
	@Nullable
	private String club;
	
    public int getErrorCode() {
    	return errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
    	this.errorCode = errorCode;
    }
    
    public String getUserCode() {
    	return userCode;
    }
    
    public void setUserCode(final String userCode) {
    	this.userCode = userCode;
    }
    
    public String getLevel() {
    	return level;
    }
    public void setLevel(final String level) {
    	this.level = level;
    }

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getClub() {
		return club;
	}

	public void setClub(final String club) {
		this.club = club;
	}
    
    
}
