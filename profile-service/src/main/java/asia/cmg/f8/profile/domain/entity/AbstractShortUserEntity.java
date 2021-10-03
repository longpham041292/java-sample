package asia.cmg.f8.profile.domain.entity;


import org.immutables.value.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created on 6/1/20.
 */
@Value.Immutable
@JsonSerialize(as = ShortUserEntity.class)
@JsonDeserialize(builder = ShortUserEntity.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractShortUserEntity {

    @JsonProperty("uuid")
    public abstract String getUuid();
    
    @JsonProperty("avatar")
    public abstract String getAvatar();

    @JsonProperty("username")
    public abstract String getUsername();

    @JsonProperty("fullname")
    public abstract String getFullname();
    
    @JsonProperty("bio")
    public abstract String getBio();
    

    @JsonProperty("level")
    public abstract String getLevel();

    @JsonProperty("rated")
    public abstract Double getRated();

}
