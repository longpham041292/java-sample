package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.common.spec.order.FreeOrderRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.commerce.dto.FreeOrderRequest.class)
@JsonDeserialize(builder = asia.cmg.f8.commerce.dto.FreeOrderRequest.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractFreeOrderRequest implements FreeOrderRequest {

	@JsonProperty("contract_number")
	@Nullable
	public abstract String getContractNumber();
	
	@JsonProperty("training_style")
	public abstract String getTrainingStyle();
}
