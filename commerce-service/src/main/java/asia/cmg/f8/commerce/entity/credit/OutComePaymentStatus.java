package asia.cmg.f8.commerce.entity.credit;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OutComePaymentStatus {

    @JsonProperty("pending")
    PENDING,
    @JsonProperty("paid")
    PAID,
}
