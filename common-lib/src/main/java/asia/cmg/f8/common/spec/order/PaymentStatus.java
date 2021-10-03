package asia.cmg.f8.common.spec.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
	
	@JsonProperty("notpaid")
    NOTPAID,
    @JsonProperty("waiting_payment")
    WAITING_PAYMENT,
    @JsonProperty("paid")
    PAID,
    @JsonProperty("fail")
    FAIL;
}
