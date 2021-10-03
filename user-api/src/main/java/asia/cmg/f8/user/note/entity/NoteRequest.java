package asia.cmg.f8.user.note.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by on 11/14/16.
 */
public class NoteRequest {
    @JsonProperty("content")
    @NotNull
    private String content;

    @JsonProperty("profile_uuid")
    @NotNull
    private String profileId;

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }
}
