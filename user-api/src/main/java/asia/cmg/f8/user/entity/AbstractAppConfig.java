package asia.cmg.f8.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;


@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings("CheckReturnValue")
public interface AbstractAppConfig {

    @JsonProperty("name")
    String getName();

    @JsonProperty("value")
    String getValue();
}
