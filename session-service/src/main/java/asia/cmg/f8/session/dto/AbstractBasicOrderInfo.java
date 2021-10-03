package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.spec.order.OrderBasicInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = BasicOrderInfo.class)
@JsonDeserialize(builder = BasicOrderInfo.Builder.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractBasicOrderInfo implements OrderBasicInfo {

}
