package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityPostInfo {

    @JsonProperty("uuid")
    private String postUuid;

    public String getPostUuid() {
        return postUuid;
    }

    public void setPostUuid(final String postUuid) {
        this.postUuid = postUuid;
    }
}
