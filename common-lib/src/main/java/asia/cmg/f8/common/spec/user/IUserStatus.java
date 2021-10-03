package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.avro.reflect.Nullable;

/**
 * Created on 11/4/16.
 */
public interface IUserStatus {
    @JsonProperty("document_status")
    @Nullable
    DocumentStatusType documentStatus();
}
