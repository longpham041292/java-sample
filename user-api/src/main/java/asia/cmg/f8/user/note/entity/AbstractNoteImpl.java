package asia.cmg.f8.user.note.entity;

import asia.cmg.f8.common.spec.misc.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.immutables.value.Value;

/**
 * Created by on 11/14/16.
 */

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractNoteImpl implements Note {
}
