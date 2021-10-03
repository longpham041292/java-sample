package asia.cmg.f8.profile.dto;

import javax.annotation.Nullable;

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
@JsonSerialize(as = ResourceLink.class)
@JsonDeserialize(builder = ResourceLink.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AbstractResourceLink {
	
	@JsonProperty("link")
	@Nullable
	String getLink();
	
	@JsonProperty("thumbnail_link")
	@Nullable
	String getThumbnailLink();
	
	@JsonProperty("ratio")
	@Nullable
	String getRatio();
}
