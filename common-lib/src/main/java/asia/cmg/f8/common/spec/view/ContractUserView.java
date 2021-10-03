package asia.cmg.f8.common.spec.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

/**
 * Created on 11/21/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ContractUserView {
    @JsonProperty("user_uuid")
    String getUserUuid();

    @JsonProperty("name")
    @Nullable
    String getName();

    @JsonProperty("username")
    @Nullable
    String getUsername();

    @JsonProperty("picture")
    @Nullable
    String getPicture();
}
