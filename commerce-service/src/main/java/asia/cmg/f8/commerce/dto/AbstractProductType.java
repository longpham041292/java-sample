package asia.cmg.f8.commerce.dto;


import asia.cmg.f8.common.spec.commerce.ProductType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.commerce.dto.ProductType.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.ProductType.Builder.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractProductType implements ProductType {

    @Nullable
    @JsonProperty("display_unit_price")
    public abstract String getDisplayUnitPrice();
    
    @Nullable
    @JsonProperty("display_commission")
    public abstract String getDisplayCommission();
}
