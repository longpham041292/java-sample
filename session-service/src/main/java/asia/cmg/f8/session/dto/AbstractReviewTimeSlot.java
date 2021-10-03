package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC
)
@JsonSerialize(as = ReviewTimeSlot.class)
@JsonDeserialize(as = ReviewTimeSlot.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractReviewTimeSlot {

    @JsonProperty("start_time")
    abstract Long startTime();

    @JsonProperty("end_time")
    abstract Long endTime();

    @JsonProperty("is_double")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    abstract Boolean conflict();

    @JsonProperty("confirmed")
    @Nullable
    abstract Boolean confirmed();

    @JsonProperty("session_id")
    @Nullable
    abstract String sessionId();

    @JsonProperty("user_info")
    @Nullable
    abstract BasicUserInfo userInfo();

}
