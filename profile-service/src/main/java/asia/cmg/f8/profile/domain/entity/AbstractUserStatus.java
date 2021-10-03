package asia.cmg.f8.profile.domain.entity;

import asia.cmg.f8.common.spec.user.IUserStatus;
import asia.cmg.f8.profile.config.BuiltEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@BuiltEntity
@JsonSerialize(as = UserStatus.class)
@JsonDeserialize(as = UserStatus.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractUserStatus implements IUserStatus {

}
