package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * UserGrid's user profile api
 * <p>
 * Created on 10/11/16.
 */
@FeignClient(value = "users", url = "${feign.url}")
public interface UserProfileClient {

    String QUERY = "query";
    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String PUT_USER = "/users/{uuid}?" + SECRET_QUERY;
    String USER_QUERY = "/users?" + SECRET_QUERY + "&ql={query}";

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> activateUser(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> queryUserByUuid(@PathVariable(QUERY) final String query);

    @RequestMapping(value = USER_QUERY + "&limit={limit}&cursor={cursor}", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    PagedUserResponse<UserEntity> getOldDefaultImageUserWithCursor(@PathVariable("query") final String query, @PathVariable("limit") final int limit, @PathVariable("cursor") final String cursor);

    @RequestMapping(value = USER_QUERY + "&limit={limit}", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    PagedUserResponse<UserEntity> getOldDefaultImageUser(@PathVariable("query") final String query, @PathVariable("limit") final int limit);

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateOldAvartaImage(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> confirmEmail(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);
}
