package asia.cmg.f8.user.entity;

import asia.cmg.f8.common.spec.user.IFacebook;
import asia.cmg.f8.user.config.BuildedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@BuildedEntity
@JsonSerialize(as = ChangePassword.class)
@JsonDeserialize(as = ChangePassword.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractChangePassword implements IFacebook {

    @JsonProperty("oldpassword")
    public abstract String getOldPassword();

    @JsonProperty("newpassword")
    public abstract String getNewPassword();
}
