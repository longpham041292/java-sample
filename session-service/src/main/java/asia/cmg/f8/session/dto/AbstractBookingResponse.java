package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created on 11/21/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC
)
@JsonSerialize(as = BookingResponse.class)
@JsonDeserialize(as = BookingResponse.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractBookingResponse {

    @JsonProperty("success")
    abstract Boolean getResult();

    @JsonProperty("booking_list")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    abstract Set<TimeSlot> getBookingList();

    @JsonProperty("user_info")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    abstract BasicUserInfo getUserInfo();

}
