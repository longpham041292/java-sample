package asia.cmg.f8.profile.dto;

import javax.annotation.Nullable;

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
        of = "new", allParameters = true,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = WhosHotConfig.class)
@JsonDeserialize(as = WhosHotConfig.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public interface AbstractWhosHotConfig {

	
	@JsonProperty("group")
    String getGroup();

    @JsonProperty("key")
    String getKey();

    @JsonProperty("language")
    String getLanguage();

    @JsonProperty("minimum_conditions_value")
    String getMinimumConditions();
    
    @JsonProperty("activated")
    @Nullable
    String getActivated();
    
    @JsonProperty("description")
    @Nullable
    String getDescription();
    
    @JsonProperty("weight_value")
    @Nullable
    String getWeight();
	
}
