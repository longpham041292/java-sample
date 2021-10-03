package asia.cmg.f8.gateway.security.linkaccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkedUserInfo {

    @JsonProperty("linking_user")
    private String linkingUserUuid;

    @JsonProperty("linked_user")
    private String linkedUserUuid;

    public String getLinkingUserUuid() {
        return linkingUserUuid;
    }

    public void setLinkingUserUuid(final String linkingUserUuid) {
        this.linkingUserUuid = linkingUserUuid;
    }

    public String getLinkedUserUuid() {
        return linkedUserUuid;
    }

    public void setLinkedUserUuid(final String linkedUserUuid) {
        this.linkedUserUuid = linkedUserUuid;
    }
}
