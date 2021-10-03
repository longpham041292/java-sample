package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 11/24/16.
 */
@SuppressWarnings("squid:S2384")
public class AvailabilityRequest {
    @JsonProperty("month")
    @NotNull
    private int month;

    @JsonProperty("year")
    @NotNull
    private int year;

    @JsonProperty("availabilities")
    @NotNull
    private List<TimeSlot> listAvailability;
    
    @JsonProperty("dayoffs")
    @NotNull
    private List<Long> listDayOff;

    public int getMonth() {
        return month;
    }

    public void setMonth(final int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    public List<TimeSlot> getListAvailability() {
        return listAvailability;
    }

    public void setListAvailability(final List<TimeSlot> listAvailability) {
        this.listAvailability = listAvailability;
    }
    
    public List<Long> getListDayOff() {
        return listDayOff;
    }

    public void setListDayOff(final List<Long> listDayOff) {
        this.listDayOff = listDayOff;
    }
}
