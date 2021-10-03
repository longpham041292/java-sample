package asia.cmg.f8.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkUserEntity {

    // to constraint that a link between 2 users is unique
    @JsonProperty("name")
    private String name;

    @JsonProperty("linking_user")
    private String linkingUser;

    @JsonProperty("linked_user")
    private String linkedUser;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLinkingUser() {
        return linkingUser;
    }

    public void setLinkingUser(final String linkingUser) {
        this.linkingUser = linkingUser;
    }

    public String getLinkedUser() {
        return linkedUser;
    }

    public void setLinkedUser(final String linkedUser) {
        this.linkedUser = linkedUser;
    }
}
