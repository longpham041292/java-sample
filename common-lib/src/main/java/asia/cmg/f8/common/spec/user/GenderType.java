package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GenderType {
    @JsonProperty("M")
    MALE,
    @JsonProperty("F")
    FEMAIL,
    @JsonProperty("O")
    OTHER
}
