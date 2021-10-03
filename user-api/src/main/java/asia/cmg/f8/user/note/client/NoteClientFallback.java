package asia.cmg.f8.user.note.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.note.entity.NoteEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by on 11/14/16.
 */
@Component
public class NoteClientFallback implements NoteClient {
    @Override
    public UserGridResponse<NoteEntity> getNotesByProfile(@PathVariable("query") final String query) {
        return null;
    }

    @Override
    public UserGridResponse<NoteEntity> createNote(@RequestBody final NoteEntity note) {
        return null;
    }

    @Override
    public UserGridResponse<NoteEntity> updateNote(@PathVariable("uuid") final String uuid,
                                                   @RequestBody final Object note) {
        return null;
    }

    @Override
    public UserGridResponse<UserEntity> checkExistUser(@PathVariable("query") final String query) {
        return null;
    }
}
