package asia.cmg.f8.report.entiy.usergrid;

import asia.cmg.f8.common.spec.user.IFacebook;
import asia.cmg.f8.report.config.BuiltEntity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@BuiltEntity
@JsonSerialize(as = Facebook.class)
@JsonDeserialize(as = Facebook.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractFacebook implements IFacebook {

}
