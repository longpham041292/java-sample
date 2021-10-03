package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Set;

import javax.validation.constraints.NotNull;

@SuppressWarnings("squid:S2384")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleEventRequest {

    @JsonProperty(required = true)
    @NotNull
    private String title;

    @JsonProperty(value = "available_to_train", defaultValue = "false")
    private boolean availableToTrain;

    @JsonProperty("schedule")
    @NotNull
    @JsonUnwrapped
    private Set<EventTimeRange> schedule;


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isAvailableToTrain() {
        return availableToTrain;
    }

    public void setAvailableToTrain(final boolean availableToTrain) {
        this.availableToTrain = availableToTrain;
    }

    public Set<EventTimeRange> getSchedule() {
        return schedule;
    }

    public void setSchedule(final Set<EventTimeRange> schedule) {
        this.schedule = schedule;
    }

}
