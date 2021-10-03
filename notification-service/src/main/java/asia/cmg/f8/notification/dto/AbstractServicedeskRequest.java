package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 12/29/16.
 */
@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = ServicedeskRequest.class)
@JsonDeserialize(builder = ServicedeskRequest.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractServicedeskRequest<T> {

    @JsonProperty("serviceDeskId")
    public abstract Integer getServicedeskId();

    @JsonProperty("requestTypeId")
    public abstract Integer getRequestTypeId();

    @JsonProperty("requestFieldValues")
    public abstract T getRequestFieldValues();

}
