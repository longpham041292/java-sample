package asia.cmg.f8.user.note.api;

import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.note.entity.NoteEntity;
import asia.cmg.f8.user.note.entity.NoteImpl;
import asia.cmg.f8.user.note.entity.NoteRequest;
import asia.cmg.f8.user.note.service.NoteService;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by on 11/14/16.
 */
@RestController
public class NoteApi {
    private final NoteService noteService;
    private final static Logger LOG = Logger.getLogger(NoteApi.class);

    public NoteApi(final NoteService noteService) {
        this.noteService = noteService;
    }

    @RequiredAdminRole
    @RequestMapping(value = "/notes/user/{profile_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Object> getUserNoteByProfile(@PathVariable("profile_uuid") final String profileId) {
        final Optional<NoteEntity> note = noteService.getUserNoteByProfileId(profileId);
        if (!note.isPresent()) {
            return new ResponseEntity<>(NoteImpl.builder().build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(parseNoteEntity(note.get()), HttpStatus.OK);
    }

    @RequiredAdminRole
    @RequestMapping(value = "/notes/user", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Object> createOrUpdateUserNote(@RequestBody final NoteRequest body) {
        final Optional<NoteEntity> modifiedNote = noteService.createOrUpdateUserNote(body);
        if (!modifiedNote.isPresent()) {
            LOG.info("Could not found profile id: " + body.getProfileId());
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(parseNoteEntity(modifiedNote.get()), HttpStatus.OK);
    }

    @RequiredAdminRole
    @RequestMapping(value = "/notes/document/{profile_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Object> getDocumentNoteByProfile(@PathVariable("profile_uuid") final String profileId) {
        final Optional<NoteEntity> note = noteService.getDocumentNoteByProfileId(profileId);
        if (!note.isPresent()) {
            return new ResponseEntity<>(NoteImpl.builder().build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(parseNoteEntity(note.get()), HttpStatus.OK);
    }

    @RequiredAdminRole
    @RequestMapping(value = "/notes/document", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Object> createOrUpdateDocumentNote(@RequestBody final NoteRequest body) {
        final Optional<NoteEntity> modifiedNote = noteService.createOrUpdateDocumentNote(body);
        if (!modifiedNote.isPresent()) {
            LOG.info("Could not found profile id: " + body.getProfileId());
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(parseNoteEntity(modifiedNote.get()), HttpStatus.OK);
    }

    private NoteImpl parseNoteEntity(final NoteEntity entity) {
        return NoteImpl.builder()
                .profileUuid(entity.getProfileId())
                .content(entity.getContent())
                .build();
    }
}
