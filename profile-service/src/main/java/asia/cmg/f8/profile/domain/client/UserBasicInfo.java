package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.profile.domain.entity.UserEntity;


public class UserBasicInfo {

    private String uuid;
    private String name;
    private String username;
    private String picture;
    private String userType;
    private Boolean followed;

    public UserBasicInfo(final UserEntity user) {
        this.uuid = user.getUuid();
        this.name = user.getName();
        this.username = user.getUsername();
        this.picture = user.getPicture();
        if(user.getUserType()!=null) {
            this.userType = user.getUserType().name().toLowerCase();
        }
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(final String userType) {
        this.userType = userType;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(final Boolean followed) {
        this.followed = followed;
    }
}
