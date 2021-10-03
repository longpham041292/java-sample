package asia.cmg.f8.notification.dto;

import asia.cmg.f8.common.spec.order.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.notification.dto.Order.class)
@JsonDeserialize(builder = asia.cmg.f8.notification.dto.Order.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractOrder implements Order {

    @Override
    @JsonProperty("products")
    public abstract List<OrderEntry> getProducts();
    
    @Override
    @JsonProperty("created_date")
    @Nullable
    public abstract Date getCreatedDate();
    
}
