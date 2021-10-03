package asia.cmg.f8.notification.entity;


import asia.cmg.f8.common.spec.user.UserType;

/**
 * @author tung.nguyenthanh
 */
public class UserEntity {

    private String uuid;
    private String name;
    private String email;
    private String username;
    private String picture;
    private String language;
    private UserType userType;
    private String lastMsg;
    private String lastMsgOwner;
    private String lastMsgReceiver;
    private String lastMsgType;
    private String lastMsgId;
    private long lastMsgTime;

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(final UserType userType) {
        this.userType = userType;
    }
    
    public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(final String lastMsg) {
		this.lastMsg = lastMsg;
	}

    public void setLastMsgOwner(String lastMsgOwner) {
        this.lastMsgOwner = lastMsgOwner;
    }

    public String getLastMsgOwner() {
        return lastMsgOwner;
    }

	public String getLastMsgType() {
		return lastMsgType;
	}

	public void setLastMsgType(String lastMsgType) {
		this.lastMsgType = lastMsgType;
	}

	public String getLastMsgReceiver() {
		return lastMsgReceiver;
	}

	public void setLastMsgReceiver(String lastMsgReceiver) {
		this.lastMsgReceiver = lastMsgReceiver;
	}

    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }
}
