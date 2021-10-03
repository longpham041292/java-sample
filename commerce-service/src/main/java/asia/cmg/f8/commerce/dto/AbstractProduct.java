package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.common.spec.commerce.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.commerce.dto.Product.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.Product.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractProduct implements Product {

    @Nullable
    @JsonProperty("discount")
    public abstract Double getDiscount();

    @Nullable
    @JsonProperty("display_unit_price")
    public abstract String getDisplayUnitPrice();

    @Nullable
    @JsonProperty("display_sub_total")
    public abstract String getDisplaySubtotal();

    @Nullable
    @JsonProperty("display_promotion_price")
    public abstract String getDisplayPromotionPrice();
    
    @Nullable
    @JsonProperty("final_total_price")
    public abstract String getFinalTotalPrice();
    
    @Nullable
    @JsonProperty("final_unit_price")
    public abstract String getFinalUnitPrice();
    
    @Nullable
    @JsonProperty("final_total_num_of_session")
    public abstract Integer getFinalTotalNumOfSession();
    
    @Nullable
    @JsonProperty("final_display_promotion_price")
    public abstract String getFinalDisplayPromotionPrice();
    
}
