package asia.cmg.f8.user.note.service;

import asia.cmg.f8.user.note.client.NoteClient;
import asia.cmg.f8.user.note.entity.NoteEntity;
import asia.cmg.f8.user.note.entity.NoteRequest;
import asia.cmg.f8.user.note.entity.NoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by on 11/14/16.
 */
@Component
public class NoteService {
    private static final String CONTENT = "content";
    private static final String QUERY_NOTE_BY_PROFILE_ID_TYPE = "select * where profile_uuid='%1$s' and note_type='%2$s'";
    private static final String QUERY_EXIST_PROFILE_ID = "select uuid where uuid='%s'";

    @Autowired
    private NoteClient noteClient;

    public Optional<NoteEntity> getUserNoteByProfileId(final String profileId) {
        return getNoteByProfileIdAndType(profileId, NoteType.USER_NOTE);
    }

    public Optional<NoteEntity> createOrUpdateUserNote(final NoteRequest note) {
        return createOrUpdateNote(note.getProfileId(), NoteType.USER_NOTE, note.getContent());
    }

    public Optional<NoteEntity> getDocumentNoteByProfileId(final String profileId) {
        return getNoteByProfileIdAndType(profileId, NoteType.DOCUMENT_NOTE);
    }

    public Optional<NoteEntity> createOrUpdateDocumentNote(final NoteRequest note) {
        return createOrUpdateNote(note.getProfileId(), NoteType.DOCUMENT_NOTE, note.getContent());
    }

    private Optional<NoteEntity> getNoteByProfileIdAndType(final String profileId, final NoteType noteType) {
        final List<NoteEntity> noteResp =
                noteClient.getNotesByProfile(String.format(QUERY_NOTE_BY_PROFILE_ID_TYPE, profileId, noteType))
                        .getEntities();
        if (noteResp.isEmpty()) {
            return Optional.empty();
        }
        return noteResp.stream().findFirst();
    }

    private Optional<NoteEntity> createOrUpdateNote(final String profileId, final NoteType noteType, final String content) {
        if (noteClient.checkExistUser(String.format(QUERY_EXIST_PROFILE_ID, profileId)).getEntities().isEmpty()) {
            return Optional.empty();
        }

        final Optional<NoteEntity> existingNote = getNoteByProfileIdAndType(profileId, noteType);
        if (existingNote.isPresent()) {
            return noteClient.updateNote(existingNote.get().getUuid(), Collections.singletonMap(CONTENT, content))
                    .getEntities().stream().findFirst();
        }

        final NoteEntity updatedNote = new NoteEntity();
        updatedNote.setContent(content);
        updatedNote.setNoteType(noteType);
        updatedNote.setProfileId(profileId);
        return noteClient.createNote(updatedNote).getEntities().stream().findFirst();
    }
}
