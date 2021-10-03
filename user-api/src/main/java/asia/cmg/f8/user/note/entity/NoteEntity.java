package asia.cmg.f8.user.note.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created on 11/28/16.
 */
public class NoteEntity {
    @JsonProperty("uuid")
    @Nullable
    private String uuid;

    @JsonProperty("profile_uuid")
    @Nullable
    private String profileId;

    @JsonProperty("content")
    @Nullable
    private String content;

    @JsonProperty("note_type")
    @Nullable
    private NoteType noteType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(final NoteType noteType) {
        this.noteType = noteType;
    }
}
