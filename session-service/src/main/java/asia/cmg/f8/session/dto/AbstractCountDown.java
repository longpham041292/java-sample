package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonSerialize(as = CountDown.class)
@JsonDeserialize(as = CountDown.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractCountDown {

    @Nullable
    public abstract String getSessionUUID();

    @Nullable
    public abstract Integer getSessionBurned();

    @Nullable
    public abstract Integer getSessionTotal();

    @Nullable
    public abstract String getUserUUID();

    @Nullable
    public abstract Long getStartTime();

    @Nullable
    public abstract Long getEndTime();

}
