package asia.cmg.f8.commerce.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {

    @JsonProperty("domestic")
    DOMESTIC, 
    @JsonProperty("international")
    INTERNATIONAL
}
