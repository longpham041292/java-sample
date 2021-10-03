package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {

    @JsonProperty("domestic")
    DOMESTIC, 
    @JsonProperty("international")
    INTERNATIONAL
}
