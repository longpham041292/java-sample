package asia.cmg.f8.common.spec.commerce;

import asia.cmg.f8.common.spec.Identifiable;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Represent a definition of {@link ProductType}.
 * <p>
 * Created on 11/4/16.
 */
public interface ProductType extends Identifiable {

    @JsonProperty("level")
    @Nullable
    String getLevelCode();

    @JsonProperty("commision")
    @Nullable
    Double getCommision();

    @JsonProperty("unit_price")
    @Nullable
    Double getUnitPrice();

    @JsonProperty("description")
    @Nullable
    String getDescription();
}
