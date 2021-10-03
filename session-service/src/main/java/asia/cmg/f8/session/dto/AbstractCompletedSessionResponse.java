package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.spec.view.CompletedSessionView;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

import org.immutables.value.Value;

/**
 * Created on 12/14/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractCompletedSessionResponse implements CompletedSessionView {

	@JsonProperty("location_name")
	@Nullable
	abstract String getLocationName();
	
	@JsonProperty("location_address")
	@Nullable
	abstract String getLocationAddress();
}
