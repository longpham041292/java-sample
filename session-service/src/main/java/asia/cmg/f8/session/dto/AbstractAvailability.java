package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.spec.view.AvailabilityView;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/22/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = Availability.class)
@JsonDeserialize(builder = Availability.Builder.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractAvailability implements AvailabilityView {

}
