package asia.cmg.f8.notification.entity;

import asia.cmg.f8.common.spec.user.IUserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@JsonSerialize(as = UserStatus.class)
@JsonDeserialize(as = UserStatus.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractUserStatus implements IUserStatus {
}
