package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

/**
 * Created on 12/8/16.
 */
public class CheckInUser {
    @JsonProperty("uuid")
    @Nullable
    private String uuid;

    @JsonProperty("club_id")
    @Nullable
    private String clubId;

    @JsonProperty("user_id")
    @Nullable
    private String userId;

    @JsonProperty("username")
    @Nullable
    private String username;

    @JsonProperty("email")
    @Nullable
    private String email;

    @JsonProperty("full_name")
    @Nullable
    private String fullName;

    @JsonProperty("expired_time")
    @Nullable
    private Long expiredTime;
    
    @JsonProperty("user_type")
    @Nullable
    private String userType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(final String clubId) {
        this.clubId = clubId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(final Long expiredTime) {
        this.expiredTime = expiredTime;
    }
    
    public String getUserType() {
        return userType;
    }

    public void setUserType(final String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
