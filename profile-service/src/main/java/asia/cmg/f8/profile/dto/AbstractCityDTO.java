package asia.cmg.f8.profile.dto;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.eventbus.AllowConcurrentEvents;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = CityDTO.class)
@JsonDeserialize(builder = CityDTO.Builder.class)
public abstract class AbstractCityDTO {

	@JsonProperty("key")
	abstract String getKey();
	
	@JsonProperty("name")
	abstract String getName();
	
	@JsonProperty("language")
	abstract String getLanguage();
	
	@JsonProperty("sequence")
	abstract Integer getSequence();
	
	@JsonProperty("latitude")
	@Nullable()
	abstract Double getLatitude();
	
	@Nullable
	@JsonProperty("longitude")
	abstract Double getLongitude();
}
