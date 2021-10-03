package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.common.spec.order.OrderHistory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.commerce.dto.OrderHistory.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.OrderHistory.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractOrderHistory implements OrderHistory {

}
