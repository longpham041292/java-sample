package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

/**
 * Created on 12/21/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings("CheckReturnValue")
public interface AbstractInboxSender {
    @JsonProperty("uuid")
    String getId();

    @JsonProperty("picture")
    String getPicture();

    @JsonProperty("full_name")
    String getFullName();

    @JsonProperty("phone")
    String getPhone();
}
