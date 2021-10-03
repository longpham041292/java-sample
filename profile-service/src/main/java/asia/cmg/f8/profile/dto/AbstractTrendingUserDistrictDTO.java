package asia.cmg.f8.profile.dto;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = TrendingUserDistrictDTO.class)
@JsonDeserialize(builder = TrendingUserDistrictDTO.Builder.class)
public abstract class AbstractTrendingUserDistrictDTO {
	
	@JsonProperty("city_key")
	@NotNull
	public abstract String getCityKey();
	
	@JsonProperty("district_key")
	@NotNull
	public abstract String getDistrictKey();
	
	@JsonProperty("order")
	@NotNull
	public abstract Integer getOrder();
	
	@JsonProperty("user_uuid")
	@NotNull
	public abstract String getUserUuid();
	
	@JsonProperty("user_type")
	@NotNull
	public abstract String getUserType();
}
