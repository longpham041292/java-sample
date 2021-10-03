package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 11/29/16.
 */
@SuppressWarnings("squid:S2384")
public class DateSlot {
    @JsonProperty("date")
    @NotNull
    private Long date;

    @JsonProperty("time_slots")
    @NotNull
    private List<TimeSlot> listTimeSlots;

    @JsonProperty("confirmed_double")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean confirmed;

    public Long getDate() {
        return date;
    }

    public void setDate(final Long date) {
        this.date = date;
    }

    public List<TimeSlot> getListTimeSlots() {
        return listTimeSlots;
    }

    public void setListTimeSlots(final List<TimeSlot> listTimeSlots) {
        this.listTimeSlots = listTimeSlots;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(final boolean confirmed) {
        this.confirmed = confirmed;
    }
}
