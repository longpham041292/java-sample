package asia.cmg.f8.user.entity;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.user.IUserEntity;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = UserEntity.class)
@JsonDeserialize(builder = UserEntity.Builder.class)
@SuppressWarnings("CheckReturnValue")
public abstract class AbstractUserEntity implements IUserEntity {

    @Override
    @SuppressWarnings("PMD")
    @JsonProperty("uuid")
    @Nullable
    public abstract String getUuid();

    @Override
    @Nullable
    public abstract Profile getProfile();

    @Override
    @Nullable
    public abstract UserStatus getStatus();

    @Override
    @Nullable
    public abstract Facebook getFacebook();

    @Nullable
    public abstract String getCity();

    @Nullable
    public abstract Long getApprovedDate();

    @JsonProperty("confirmed")
    @Nullable
    public abstract Boolean getConfirmed();
    
    @Override
	@Nullable
	public abstract String getPicture();

    @JsonProperty("lastMsgOwner")
    @Nullable
    public abstract String getLastMsgOwner();
    
    @JsonProperty("lastMsgReceiver")
    @Nullable
    public abstract String getLastMsgReceiver();
    
    @JsonProperty("lastMsgType")
    @Nullable
    public abstract String getLastMsgType();

    @Nullable
    public abstract Long getModified();

    @Nullable
    public abstract Long getLastMsgTime();

    @Nullable
    public abstract String getLastMsgId();
    
    @JsonProperty("last_load_notification_time")
    @Nullable
    public abstract Long getLastTimeLoadNotification();

}