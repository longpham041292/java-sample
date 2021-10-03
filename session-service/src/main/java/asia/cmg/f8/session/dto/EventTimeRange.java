package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Schedule event time range
 * 
 * @author tung.nguyenthanh
 *
 */
public class EventTimeRange {

    @JsonProperty(value = "start_time", required = true)
    private Long startTime;

    @JsonProperty(value = "end_time", required = true)
    private Long endTime;

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

}
