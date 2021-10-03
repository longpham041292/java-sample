package asia.cmg.f8.session.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 12/14/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = CompletedSessionEntity.class)
@JsonDeserialize(as = CompletedSessionEntity.class)
@SuppressWarnings("CheckReturnValue")
public interface AbstractCompletedSessionEntity {
    @JsonProperty("uuid")
    @Nullable
    String getId();
    
    @JsonProperty("name")
    @Nullable
    String getName();
    
    @JsonProperty("session_id")
    @Nullable
    String getSessionId();

    @JsonProperty("session_date")
    @Nullable
    Long getSessionDate();

    @JsonProperty("session_status")
    @Nullable
    String getSessionStatus();

    @JsonProperty("trainer_id")
    @Nullable
    String getTrainerId();

    @JsonProperty("end_user_id")
    @Nullable
    String getEndUserId();

    @JsonProperty("trainer_rated")
    @Nullable
    Boolean isTrainerRated();

    @JsonProperty("end_user_rated")
    @Nullable
    Boolean isEndUserRated();
}
