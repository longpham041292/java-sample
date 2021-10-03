package asia.cmg.f8.common.spec.view;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/22/16.
 */
public interface AvailabilityView {

    @JsonProperty("start_date")
    Long getStartDate();

    @JsonProperty("end_date")
    Long getEndDate();
}
