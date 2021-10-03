package asia.cmg.f8.session.dto;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/21/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
public interface AbstractTrainerClient {
    
    @JsonProperty("user_uuid")
    String getUserUuid();

    @JsonProperty("name")
    String getName();

    @JsonProperty("picture")
    @Nullable
    String getPicture();

    @JsonProperty("session_burned")
    Integer getSessionBurned();
    
    @JsonProperty("session_confirmed")
    Integer getSessionConfirmed();

    @JsonProperty("session_number")
    Integer getSessionNumber();

    @JsonProperty("bought_date")
    @Nullable
    Long getBoughtDate();

    @JsonProperty("expired_date")
    @Nullable
    Long getExpiredDate();
    
    @JsonProperty("package_uuid")
    String getPackageUuid();
}
