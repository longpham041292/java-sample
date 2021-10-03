package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Created on 12/15/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = AvailabilityEUResponse.class)
@JsonDeserialize(builder = AvailabilityEUResponse.Builder.class)
public abstract class AbstractAvailabilityEUResponse {

    @JsonProperty("date")
    public abstract Long getDate();

    @JsonProperty("time_slots")
    public abstract List<Availability> getAvailability();

    @JsonProperty("is_match_prefer")
    public abstract Boolean isMatchPrefer();

}
