package asia.cmg.f8.common.internal.spec.user;

import asia.cmg.f8.common.spec.user.IUserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 12/15/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "UserSpec",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonInclude.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@JsonDeserialize
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IUserEntitySpec extends IUserEntity {
}
