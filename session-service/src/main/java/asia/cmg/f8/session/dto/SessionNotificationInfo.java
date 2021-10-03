package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 4/18/17.
 */
public class SessionNotificationInfo {

    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("pt_name")
    private String ptName;

    @JsonProperty("pt_uuid")
    private String ptUuid;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("session_uuid")
    private String sessionId;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(final String ptName) {
        this.ptName = ptName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
