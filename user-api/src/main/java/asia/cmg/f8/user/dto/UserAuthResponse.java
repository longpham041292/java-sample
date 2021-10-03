package asia.cmg.f8.user.dto;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.CommonConstant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created on 1/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("user")
    private Map<String, Object> user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(final Map<String, Object> user) {
        this.user = user;
    }

    public final UserType getUserType() {
        if (user == null) {
            return null;
        }
        final String userType = (String) user.get("userType");
        if (CommonConstant.EU_USER_TYPE.equalsIgnoreCase(userType)) {
            return UserType.EU;
        } else if (CommonConstant.PT_USER_TYPE.equalsIgnoreCase(userType)) {
            return UserType.PT;
        } else {
            return null;
        }
    }
}
