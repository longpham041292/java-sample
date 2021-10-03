package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by on 11/8/16.
 */
public enum DocumentStatusType {
    @JsonProperty("approved")
    APPROVED,
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("onboard")
    ONBOARD,
}
