package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import asia.cmg.f8.common.spec.user.UserType;

/**
 * Created on 1/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicUserInfo {

    private String username;
    private String name;
    private String uuid;
    private String language;
    private String picture;
    private UserType userType;

  
     public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public String getPicture() {
		return picture;
	}

	public void setPicture(final String picture) {
		this.picture = picture;
	}


	public UserType getUserType() {
		return userType;
	}


	public void setUserType(final UserType userType) {
		this.userType = userType;
	}

	
    
    
}
