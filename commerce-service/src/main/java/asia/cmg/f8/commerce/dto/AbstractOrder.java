package asia.cmg.f8.commerce.dto;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.order.Order;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.commerce.dto.Order.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.Order.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractOrder implements Order {

    @Override
    @JsonProperty("products")
    public abstract List<OrderEntry> getProducts();
    
	@JsonProperty("couponCode")
 	@Nullable
 	public abstract String getCouponCode();

	@JsonProperty("contract_number")
	@Nullable
	public abstract String getContractNumber();
    
}
