package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

/**
 * Created on 1/5/17.
 */
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
public interface AbstractPackageInfoResponse {

    @JsonProperty("package_uuid")
    String getPackageUuid();

    @JsonProperty("number_of_burned")
    Integer getNumberOfBurned();

    @JsonProperty("number_of_session")
    Integer getNumberOfSession();
}
