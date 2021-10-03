package asia.cmg.f8.common.spec.commerce;

import javax.annotation.Nullable;

import asia.cmg.f8.common.spec.Identifiable;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represent a real product or package which IUserEntity can select to buy on mobile application.
 * <p>
 * Created on 11/4/16.
 */
public interface Product extends Identifiable {

    @JsonProperty("level")
    @Nullable
    String getLevelCode();

    @JsonProperty("num_of_session")
    Integer getNumOfSessions();

    @JsonProperty("promotion_price")
    Double getPromotionPrice();

    @JsonProperty("expire_limit")
    Integer getExpireLimit();

    @JsonProperty("commision")
    @Nullable
    Double getCommision();

    @JsonProperty("unit_price")
    @Nullable
    Double getUnitPrice();

    @JsonProperty(value="active", defaultValue="true")
    @Nullable
    Boolean getActive();
    
    @JsonProperty(value="visibility", defaultValue="true")
    @Nullable
    Boolean getVisibility();
    
    @JsonProperty(value = "training_style", defaultValue = "OFFLINE")
    String getTrainingStyle();
}
