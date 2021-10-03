package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.common.spec.user.IUserEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = { JsonIgnoreProperties.class })
@JsonSerialize(as = UserEntity.class)
@JsonDeserialize(builder = UserEntity.Builder.class) 
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractUserEntity implements IUserEntity {

    @Override
    @Nullable
    public abstract Profile getProfile();

    @Override
    @Nullable
    public abstract UserStatus getStatus();

    @Override
    @Nullable
    public abstract Facebook getFacebook();

}
