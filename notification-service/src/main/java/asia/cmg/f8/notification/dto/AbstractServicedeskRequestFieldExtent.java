package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

import java.util.Map;

/**
 * Created on 12/29/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings("CheckReturnValue")
public interface AbstractServicedeskRequestFieldExtent {
    @JsonProperty("summary")
    String getSummary();

    @JsonProperty("description")
    String getDescription();

    @JsonProperty("customfield_11104")
    String getEmail();

    @JsonProperty("customfield_11304")
    String getUserName();

    @JsonProperty("customfield_11301")
    String getFirstName();

    @JsonProperty("customfield_11302")
    String getLastName();

    @JsonProperty("customfield_11305")
    String getPhoneNumber();

    @JsonProperty("customfield_11402")
    Map<String, Object> getRole();

    @JsonProperty("customfield_11403")
    String getSource();

    @JsonProperty("customfield_11404")
    String getReportedUserId();

    @JsonProperty("customfield_11405")
    String getReportedPostId();
}
