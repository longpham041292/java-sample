package asia.cmg.f8.session.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 12/2/16.
 */
public class ReservationSlot {

    @JsonProperty("start_time")
    @NotNull
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    @NotNull
    private LocalDateTime endTime;

    @JsonProperty("session_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    private String sessionId;

    @JsonProperty("confirmed")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean confirmed;
    
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(final Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
    
}
