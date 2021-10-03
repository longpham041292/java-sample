package asia.cmg.f8.user.note.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/24/16.
 */
public enum NoteType {
    @JsonProperty("user_note")
    USER_NOTE,
    @JsonProperty("document_note")
    DOCUMENT_NOTE
}
