/**
 * 
 */
package asia.cmg.f8.commerce.dto;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.commerce.Promotion;

/**
 * @author khoa.bui
 *
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = { JsonIgnoreProperties.class })
@JsonSerialize(as = asia.cmg.f8.commerce.dto.Promotion.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.Promotion.Builder.class) 
public abstract class AbstractPromotion implements Promotion {

	@JsonProperty("promtionId")
	@Nullable
	public abstract Long getPromtionId();
	
	@JsonProperty("couponCode")
	@Nullable
	public abstract String getCouponCode();

	@JsonProperty("endDate")
	@Nullable
	public abstract Long getEndDate();

	@JsonProperty("startedDate")
	@Nullable
	public abstract Long getStartedDate();

	@JsonProperty("discount")
	@Nullable
	public abstract Double getDiscount();

	@JsonProperty("freeSession")
	@Nullable
	public abstract Integer getFreeSession();

	@JsonProperty(value = "active", defaultValue = "true")
	@Nullable
	public abstract Boolean getActive();

	@JsonProperty(value = "visibility", defaultValue = "true")
	@Nullable
	public abstract Boolean getVisibility();
	
	@JsonProperty(value = "maxIndividualUsage")
	@Nullable
	public abstract Integer getMaxIndividualUsage();
	
	@JsonProperty(value = "maxTotalUsage")
	@Nullable
	public abstract Integer getMaxTotalUsage();
	
	@JsonProperty(value = "desc")
	@Nullable
	public abstract String getDesc();
	
	@JsonProperty(value = "appliedGroup")
	@Nullable
	public abstract String getAppliedGroup();
	
	@JsonProperty(value = "ptCommission")
	public abstract Double getPtCommission();
}
