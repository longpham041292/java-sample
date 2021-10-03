package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProfileType {
    @JsonProperty("pub")
    PUB,
    @JsonProperty("pri")
    PRI
}
