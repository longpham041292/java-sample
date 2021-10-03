package asia.cmg.f8.profile.domain.entity;

import asia.cmg.f8.common.spec.document.IDocumentEntity;
import asia.cmg.f8.profile.config.BuiltEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.avro.reflect.Nullable;
import org.immutables.value.Value;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@BuiltEntity
@JsonSerialize(as = DocumentEntity.class)
@JsonDeserialize(as = DocumentEntity.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractDocumentEntity implements IDocumentEntity {
    @Nullable
    public abstract Long getCreated();

    @Nullable
    public abstract String getDisplayCreated();
}
