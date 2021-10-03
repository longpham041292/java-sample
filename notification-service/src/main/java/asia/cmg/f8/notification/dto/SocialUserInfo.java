package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created on 1/13/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialUserInfo {

    private String uuid;
    private String name;
    private String avatar;
    
    public SocialUserInfo() {
		// TODO Auto-generated constructor stub
	}
    
    public SocialUserInfo(String uuid, String name) {
		this.name = name;
		this.uuid = uuid;
	}
    
    public SocialUserInfo(String uuid, String name, String picture) {
		this.name = name;
		this.uuid = uuid;
		this.avatar = picture;
	}
    
    public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
	
	public static SocialUserInfo.Builder builder() {
		return new SocialUserInfo.Builder();
	}
	
	public static final class Builder {
		
		private String uuid;
		private String name;
		private String picture;
		
		public Builder uuid(String uuid) {
			this.uuid = uuid;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder picture(String picture) {
			this.picture = picture;
			return this;
		}
		
		public SocialUserInfo build() {
			return new SocialUserInfo(uuid, name, picture);
		}
	}
}
