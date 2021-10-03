package asia.cmg.f8.profile.domain.entity;

import org.immutables.value.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = UserPTiMatch.class)
@JsonDeserialize(builder = UserPTiMatch.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonSerialize.class,
                JsonDeserialize.class})
public abstract class AbstractUserPTiMatch {

	@JsonProperty("eu_uuid")
	public abstract String getEuUuid();
	
	@JsonProperty("pt_uuid")
	public abstract String getPtUuid();
	
	@JsonProperty("personality")
	public abstract Double getPersonality();
	
	@JsonProperty("training_style")
	public abstract Double getTrainingStyle();
	
	@JsonProperty("interest")
	public abstract Double getInterest();
	
	@JsonProperty("average")
	public abstract Double getAverage();
}
