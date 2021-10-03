package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

/**
 * Created on 11/4/16.
 */
@SuppressWarnings("PMD")
public interface IFacebook {

    @JsonProperty("id")
    @Nullable
    String id();

    @JsonProperty("accessToken")
    @Nullable
    String accessToken();
}
