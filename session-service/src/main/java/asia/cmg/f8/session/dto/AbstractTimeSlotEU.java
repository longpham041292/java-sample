package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalTime;

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
@JsonSerialize(as = TimeSlotEU.class)
public abstract class AbstractTimeSlotEU {

    abstract LocalTime startTime();

    abstract LocalTime endTime();

}
