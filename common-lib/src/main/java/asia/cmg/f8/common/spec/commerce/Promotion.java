/**
 * 
 */
package asia.cmg.f8.common.spec.commerce;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.Identifiable;

/**
 * @author khoa.bui
 *
 */

public interface Promotion extends Identifiable {
	
	@JsonProperty("couponCode")
    @Nullable
    String getCouponCode();
	
	@JsonProperty("endDate")
    @Nullable
    Long getEndDate();
	
	@JsonProperty("startedDate")
    @Nullable
    Long getStartedDate();
	
	@JsonProperty("discount")
    @Nullable
    Double getDiscount();
	
	@JsonProperty("freeSession")
    @Nullable
    Integer getFreeSession();
	
	@JsonProperty(value="active", defaultValue="true")
    @Nullable
    Boolean getActive();
    
    @JsonProperty(value="visibility", defaultValue="true")
    @Nullable
    Boolean getVisibility();
    
    @JsonProperty(value="appliedGroup")
    @Nullable
    String getAppliedGroup();
}
