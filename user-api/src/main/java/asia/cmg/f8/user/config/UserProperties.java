package asia.cmg.f8.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "userapi")
public class UserProperties {

    private String userGridClientId;
    private String userGridClientSecret;
    private String activeSuccessText;
    private String dateTimeFormat;
    private boolean passActivated;
    private String defaultProfilePicture;
    private boolean allowUpdatePictureServiceRunning;
    private String activatedUserUrl;
    private String defaultGroupId;
    private String euGroupId;
    private String ptGroupId;
    private String clubGroupId;
    
    public String getClubGroupId() {
		return clubGroupId;
	}

	public void setClubGroupId(String clubGroupId) {
		this.clubGroupId = clubGroupId;
	}

	private final Admin admin = new Admin();

    public String getUserGridClientId() {
        return userGridClientId;
    }

    public void setUserGridClientId(final String userGridClientId) {
        this.userGridClientId = userGridClientId;
    }

    public String getUserGridClientSecret() {
        return userGridClientSecret;
    }

    public void setUserGridClientSecret(final String userGridClientSecret) {
        this.userGridClientSecret = userGridClientSecret;
    }

    public Admin getAdmin() {
        return admin;
    }

    public String getActiveSuccessText() {
        return activeSuccessText;
    }

    public void setActiveSuccessText(final String activeSuccessText) {
        this.activeSuccessText = activeSuccessText;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(final String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public boolean isPassActivated() {
        return passActivated;
    }

    public void setPassActivated(final boolean passActivated) {
        this.passActivated = passActivated;
    }
    
    public String getDefaultGroupId() {
		return defaultGroupId;
	}

	public void setDefaultGroupId(String defaultGroupId) {
		this.defaultGroupId = defaultGroupId;
	}

	public String getEuGroupId() {
		return euGroupId;
	}

	public void setEuGroupId(String euGroupId) {
		this.euGroupId = euGroupId;
	}

	public String getPtGroupId() {
		return ptGroupId;
	}

	public void setPtGroupId(String ptGroupId) {
		this.ptGroupId = ptGroupId;
	}

	public boolean isAllowUpdatePictureServiceRunning() {
		return allowUpdatePictureServiceRunning;
	}

	public void setAllowUpdatePictureServiceRunning(final boolean allowUpdatePictureServiceRunning) {
		this.allowUpdatePictureServiceRunning = allowUpdatePictureServiceRunning;
	}

	public String getDefaultProfilePicture() { return defaultProfilePicture; }

    public void setDefaultProfilePicture(final String defaultProfilePicture) {
        this.defaultProfilePicture = defaultProfilePicture;
    }

    public String getActivatedUserUrl() {
		return activatedUserUrl;
	}

	public void setActivatedUserUrl(final String activatedUserUrl) {
		this.activatedUserUrl = activatedUserUrl;
	}

	public static class Admin {
        private int userLoad;
        private int maxLoad;

        public int getUserLoad() {
            return userLoad;
        }

        public void setUserLoad(final int userLoad) {
            this.userLoad = userLoad;
        }

        public int getMaxLoad() {
            return maxLoad;
        }

        public void setMaxLoad(final int maxLoad) {
            this.maxLoad = maxLoad;
        }
    }
}
