package asia.cmg.f8.profile.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = SponsoredInfo.class)
@JsonDeserialize(builder = SponsoredInfo.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AbstractSponsoredInfo {

	@JsonProperty("start_time")
	Long getFromDate();
	
	@JsonProperty("end_time")
	Long getToDate();
	
}
