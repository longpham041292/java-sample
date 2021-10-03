package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkUserRequest {

    @JsonProperty("linking_email")
    private String linkingEmail;

    @JsonProperty("linking_pwd")
    private String linkingPwd;

    @JsonProperty("linking_fb_id")
    private String linkingFbId;

    @JsonProperty("linked_email")
    private String linkedEmail;

    @JsonProperty("linked_pwd")
    private String linkedPwd;

    @JsonProperty("linked_fb_id")
    private String linkedFbId;

    public String getLinkingEmail() {
        return linkingEmail;
    }

    public void setLinkingEmail(final String linkingEmail) {
        this.linkingEmail = linkingEmail;
    }

    public String getLinkingPwd() {
        return linkingPwd;
    }

    public void setLinkingPwd(final String linkingPwd) {
        this.linkingPwd = linkingPwd;
    }

    public String getLinkingFbId() {
        return linkingFbId;
    }

    public void setLinkingFbId(final String linkingFbId) {
        this.linkingFbId = linkingFbId;
    }

    public String getLinkedEmail() {
        return linkedEmail;
    }

    public void setLinkedEmail(final String linkedEmail) {
        this.linkedEmail = linkedEmail;
    }

    public String getLinkedPwd() {
        return linkedPwd;
    }

    public void setLinkedPwd(final String linkedPwd) {
        this.linkedPwd = linkedPwd;
    }

    public String getLinkedFbId() {
        return linkedFbId;
    }

    public void setLinkedFbId(final String linkedFbId) {
        this.linkedFbId = linkedFbId;
    }
}
