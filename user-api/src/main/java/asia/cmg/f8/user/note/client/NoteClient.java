package asia.cmg.f8.user.note.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.note.entity.NoteEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by on 11/14/16.
 */
@FeignClient(value = "conversations", url = "${feign.url}", fallback = NoteClientFallback.class)
public interface NoteClient {
    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String NOTE_QUERY_PREFIX = "/notes?" + SECRET_QUERY;
    String USER_QUERY_PREFIX = "/users?" + SECRET_QUERY;
    String NOTE_QUERY = NOTE_QUERY_PREFIX + "&ql={query}";
    String UPDATE_NOTE_QUERY = "/notes/{uuid}?" + SECRET_QUERY;
    String USER_QUERY = USER_QUERY_PREFIX + "&ql={query}";

    @RequestMapping(value = NOTE_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<NoteEntity> getNotesByProfile(@PathVariable("query") final String query);

    @RequestMapping(value = NOTE_QUERY_PREFIX, method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<NoteEntity> createNote(@RequestBody final NoteEntity note);

    @RequestMapping(value = UPDATE_NOTE_QUERY, method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<NoteEntity> updateNote(@PathVariable("uuid") final String uuid,
                                            @RequestBody final Object note);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> checkExistUser(@PathVariable("query") final String query);

}
