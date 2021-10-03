package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

/**
 * Created on 12/29/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings("CheckReturnValue")
public interface AbstractServicedeskRequestFieldValue {

    @JsonProperty("summary")
    String getSummary();

    @JsonProperty("customfield_11104")
    String getEmail();

    @JsonProperty("customfield_11304")
    String getUserName();

    @JsonProperty("customfield_11301")
    String getFirstName();

    @JsonProperty("customfield_11302")
    String getLastName();

}
