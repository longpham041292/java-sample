package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OutComePaymentStatus {

    @JsonProperty("pending")
    PENDING,
    @JsonProperty("paid")
    PAID,
}
