package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.internal.spec.user.FacebookSpec;
import asia.cmg.f8.common.internal.spec.user.UserStatusSpec;
import asia.cmg.f8.common.spec.user.IUserEntity;
import asia.cmg.f8.common.spec.user.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonSerialize(as = UserEntity.class)
@JsonDeserialize(as = UserEntity.class)
@JsonIgnoreProperties(ignoreUnknown = true)
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
public abstract class AbstractUserEntity implements IUserEntity {

    @SuppressWarnings("PMD")
    @JsonProperty("uuid")
    @Nullable
    public abstract String getUuid();

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract String getEmail();

    @Nullable
    public abstract String getUsername();

    @Nullable
    public abstract Long getCreated();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public abstract String getSkills();

    @Nullable
    public abstract Boolean getActivated();

    @Nullable
    public abstract String getPicture();

    @Nullable
    public abstract String getLanguage();

    @Nullable
    public abstract UserType getUserType();

    @Nullable
    public abstract String getCountry();

    @Nullable
    public abstract Profile getProfile();

    @Nullable
    public abstract UserStatusSpec getStatus();

    @Nullable
    @JsonIgnore
    public abstract FacebookSpec getFacebook();
}
