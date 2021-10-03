package asia.cmg.f8.notification.entity;

import asia.cmg.f8.common.spec.user.IUserEntity;
import asia.cmg.f8.common.spec.user.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonSerialize(as = UserEntityImpl.class)
@JsonDeserialize(builder = UserEntityImpl.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonInclude.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@SuppressWarnings("CheckReturnValue")
public abstract class AbstractUserEntityImpl implements IUserEntity {

    @Nullable
    public abstract UserType getUserType();

    @Nullable
    public abstract Profile getProfile();

    @Nullable
    public abstract UserStatus getStatus();

    @Nullable
    public abstract Facebook getFacebook();
}
