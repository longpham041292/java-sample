package asia.cmg.f8.gateway.security.dto;

import java.util.Map;

/**
 * Created on 2/14/17.
 */
public class UserInfo {

    private String uuid;
    private boolean activated;
    private boolean confirmed;
    private String email;
    private String username;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(final boolean confirmed) {
        this.confirmed = confirmed;
    }

    public static UserInfo create(final Map<String, Object> data) {
        final UserInfo userInfo = new UserInfo();
        userInfo.setUuid((String) data.get("uuid"));
        userInfo.setActivated(getBooleanProperty(data, "activated"));
        userInfo.setConfirmed(getBooleanProperty(data, "confirmed"));
        userInfo.setEmail((String) data.get("email"));
        userInfo.setUsername((String) data.get("username"));
        return userInfo;
    }

    private static boolean getBooleanProperty(final Map<String, Object> data, final String property) {
        final Boolean value = (Boolean) data.get(property);
        if (value != null) {
            return value;
        }
        return false;
    }
}
