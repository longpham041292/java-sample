package asia.cmg.f8.profile.domain.entity;

import asia.cmg.f8.common.spec.order.FreeOrderRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = asia.cmg.f8.profile.domain.entity.FreeOrderRequest.class)
@JsonDeserialize(builder = asia.cmg.f8.profile.domain.entity.FreeOrderRequest.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractFreeOrderRequest implements FreeOrderRequest {

	@JsonProperty("contract_number")
	public abstract String getContractNumber();
}
