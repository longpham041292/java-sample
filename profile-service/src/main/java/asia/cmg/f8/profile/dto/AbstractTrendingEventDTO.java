package asia.cmg.f8.profile.dto;

import javax.annotation.Nullable;
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
@JsonSerialize(as = TrendingEventDTO.class)
@JsonDeserialize(builder = TrendingEventDTO.Builder.class)
public abstract class AbstractTrendingEventDTO {

	@JsonProperty("id")
	@Nullable
	public abstract Integer getId();
	
	@JsonProperty("image")
	@NotNull
	public abstract String getImage();
	
	@JsonProperty("thumbnail")
	@NotNull
	public abstract String getThumbnail();
	
	@JsonProperty("title")
	@Nullable
	public abstract String getTitle();
	
	@JsonProperty("content")
	@Nullable
	public abstract String getContent();
	
	@JsonProperty("short_content")
	@Nullable
	public abstract String getShortContent();
	
	@JsonProperty("content_type")
	@NotNull
	public abstract String getContentType();
	
	@JsonProperty("order")
	@NotNull
	public abstract int getOrder();
	
	@JsonProperty("language")
	@NotNull
	public abstract String getLanguage();
	
	@JsonProperty("url")
	@Nullable
	public abstract String getUrl();
	
	@JsonProperty("action_id")
	@Nullable
	public abstract Integer getAction();
	
	@JsonProperty("section_id")
	@NotNull
	public abstract int getSection();
	
	@JsonProperty("activated")
	@NotNull
	public abstract Boolean getActivated();
}
