package asia.cmg.f8.common.spec.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.avro.reflect.Nullable;

/**
 * Created by on 10/25/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IDocumentEntity {
    @Nullable
    String getUuid();

    @Nullable
    String getPath();

    @Nullable
    String getDocumentName();

    @Nullable
    String getCategory();

    @Nullable
    String getOwner();
}
