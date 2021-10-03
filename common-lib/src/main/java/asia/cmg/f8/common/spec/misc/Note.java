package asia.cmg.f8.common.spec.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

/**
 * Represent a note which is created and used by admin.
 * <p>
 * Created on 11/4/16.
 */
public interface Note {
    @JsonProperty("profile_uuid")
    @Nullable
    String getProfileUuid();

    @JsonProperty("content")
    @Nullable
    String getContent();
}
