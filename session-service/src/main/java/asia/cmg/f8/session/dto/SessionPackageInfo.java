package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created on 12/15/16.
 */
public class SessionPackageInfo {

    @JsonProperty("session_burned")
    private Integer sessionBurned;

    @JsonProperty("session_number")
    private Integer sessionNumber;

    @Nullable
    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public void setSessionBurned(@Nullable final Integer sessionBurned) {
        this.sessionBurned = sessionBurned;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(final Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }
}
