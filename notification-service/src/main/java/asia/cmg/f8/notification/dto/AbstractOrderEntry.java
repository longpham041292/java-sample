package asia.cmg.f8.notification.dto;

import asia.cmg.f8.common.spec.order.OrderEntry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.notification.dto.OrderEntry.class)
@JsonDeserialize(builder = asia.cmg.f8.notification.dto.OrderEntry.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractOrderEntry implements OrderEntry {

    @Nullable
    @JsonProperty("display_unit_price")
    public abstract String getDisplayUnitPrice();

    @Nullable
    @JsonProperty("display_sub_total")
    public abstract String getDisplaySubTotal();

    @Nullable
    @JsonProperty("display_total_price")
    public abstract String getDisplayTotalPrice();
}
