package asia.cmg.f8.session.wrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class UserSession {

    private final String uuid;
    private final String avatar;

    @JsonProperty("full_name")
    private final String fullName;

    @JsonProperty("session_burned")
    private final Integer sessionBurned;

    @JsonProperty("session_total")
    private final Integer sessionTotal;

    public UserSession(final String uuid, final String avatar, final String fullName,
            final Integer sessionBurned, final Integer sessionTotal) {
        super();
        this.uuid = uuid;
        this.avatar = avatar;
        this.fullName = fullName;
        this.sessionBurned = sessionBurned;
        this.sessionTotal = sessionTotal;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public Integer getSessionTotal() {
        return sessionTotal;
    }


}
