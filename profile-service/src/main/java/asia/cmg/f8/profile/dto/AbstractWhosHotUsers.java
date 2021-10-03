package asia.cmg.f8.profile.dto;

import org.apache.avro.reflect.Nullable;
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
@JsonSerialize(as = WhosHotUsers.class)
@JsonDeserialize(as = WhosHotUsers.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractWhosHotUsers {

	@JsonProperty("uuid")
	@Nullable
	abstract String getUuid();
	
	@JsonProperty("name")
	@Nullable
	abstract String getName();

	@JsonProperty("avatar")
	@Nullable
	abstract String getAvatar();

	@JsonProperty("level")
	@Nullable
	abstract  String getLevel();

	@JsonProperty("followStatus")
	@Nullable
	abstract Boolean getFollowStatus();
	
}
