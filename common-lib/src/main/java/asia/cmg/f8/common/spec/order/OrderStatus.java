package asia.cmg.f8.common.spec.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
	@JsonProperty("created")
    CREATED,
    @JsonProperty("completed")
    COMPLETED,
    @JsonProperty("cancelled")
    CANCELLED
}
