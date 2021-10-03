package asia.cmg.f8.user.entity;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.user.IFacebook;
import asia.cmg.f8.user.config.BuildedEntity;

@Value.Immutable
@BuildedEntity
@JsonSerialize(as = ChangeUsercode.class)
@JsonDeserialize(as = ChangeUsercode.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractChangeUsercode implements IFacebook {

    @JsonProperty("usercode")
    public abstract String getNewUsercode();
}
