package asia.cmg.f8.user.entity;

import asia.cmg.f8.user.config.BuildedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 1/5/17.
 */
@Value.Immutable
@BuildedEntity
@JsonSerialize(as = ChangePasswordResponse.class)
@JsonDeserialize(as = ChangePasswordResponse.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractChangePasswordResponse {

    @JsonProperty("action")
    public abstract String getAction();

    @JsonProperty("timestamp")
    public abstract Long getTimestamp();

    @JsonProperty("duration")
    public abstract Long getDuration();
}
