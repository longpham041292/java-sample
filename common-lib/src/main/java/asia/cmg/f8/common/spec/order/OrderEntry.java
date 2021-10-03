package asia.cmg.f8.common.spec.order;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represent a real product or package which IUserEntity can select to buy on mobile application.
 * <p>
 * Created on 11/4/16.
 */
public interface OrderEntry{
    
    @JsonProperty("unit_price")
    Double getUnitPrice();
    
    @JsonProperty("num_of_session")
    Integer getNumOfSessions();
    
    @JsonProperty("expire_limit")
    Integer getExpireLimit();
    
    @JsonProperty("entry_number")
    @Nullable
    Integer getEntryNumber();
    
    @JsonProperty("sub_total")
    Double getSubTotal();
    
    @JsonProperty("total_price")
    Double getTotalPrice();
}
