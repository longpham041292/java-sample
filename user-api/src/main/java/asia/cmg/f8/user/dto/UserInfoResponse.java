package asia.cmg.f8.user.dto;

import asia.cmg.f8.common.spec.user.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("username")
    private String username;

    @JsonProperty("userType")
    private UserType userType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(final UserType userType) {
        this.userType = userType;
    }
}
