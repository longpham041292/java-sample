package asia.cmg.f8.session.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by on 11/24/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSlot {

    @JsonProperty("start_time")
    @NotNull
    private Long startTime;

    @JsonProperty("end_time")
    @NotNull
    private Long endTime;

    @JsonProperty("is_double")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean conflict;

    @JsonProperty("confirmed")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean confirmed;

    @JsonProperty("session_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    private String sessionId;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(final Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getConflict() {
        return conflict != null && conflict;
    }

    public void setConflict(final Boolean conflict) {
        this.conflict = conflict;
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
