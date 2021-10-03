package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.dto.PhoneNumberDTO;
import asia.cmg.f8.user.dto.VerifyPhoneNumberDTO;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * UserGrid's user api
 * <p>
 * Created on 10/11/16.
 */
@FeignClient(value = "users", url = "${feign.url}")
public interface UserClient {

	String LIMIT = "limit";
    String QUERY = "query";
    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String USER_QUERY_PREFIX = "/users?" + SECRET_QUERY;
    String GET_USER = "/users/{username}?" + SECRET_QUERY;
    String PUT_USER = "/users/{uuid}?" + SECRET_QUERY;
    String USER_QUERY = USER_QUERY_PREFIX + "&ql={query}";
    String USER_QUERY_WITH_LIMIT_AND_CURSOR = USER_QUERY +"&limit={limit}&cursor={cursor}";
    String GET_USER_BY_TOKEN = "users/{username}";

    @RequestMapping(value = USER_QUERY_PREFIX, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> createUser(@RequestBody Object user);
    
    @RequestMapping(value = "/groups/{groupUuid}/users/{userUuid}?" + SECRET_QUERY, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> addUserToGroup(@PathVariable("userUuid") String userUuid, @PathVariable("groupUuid") String groupUuid);

    @RequestMapping(value = GET_USER, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUser(@PathVariable("username") String username);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByQuery(@PathVariable(QUERY) final String query);
    
    @RequestMapping(value = USER_QUERY_WITH_LIMIT_AND_CURSOR, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUserByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit,
            @RequestParam(value = "cursor", required = false) final String cursor);
    
    @RequestMapping(value = USER_QUERY + "&limit={limit}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUserByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit);
    
    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> checkExistUserWithCondition(@PathVariable(QUERY) final String query);

    @RequestMapping(value = "/auth/facebook?fb_access_token={token}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> loginFacebook(@PathVariable("token") final String token);

    @RequestMapping(value = "/users/resetpassword", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> resetPassword(@RequestBody final Object email);

    @RequestMapping(value = "/users/{email}/reactivate?client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> reActivate(@PathVariable("email") final String email);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<?> checkActivated(@PathVariable(QUERY) final String query);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<?> getUserByFBID(@PathVariable(QUERY) final String query);

    @RequestMapping(value = GET_USER, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByToken(@PathVariable("username") String username);

    @RequestMapping(value = "/users?limit={limit}&cursor={cursor}&ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchUserWithPaging(
            @PathVariable(QUERY) final String query,
            @PathVariable(LIMIT) final int limit,
            @PathVariable("cursor") final String cursor);

    @RequestMapping(value = "/users?limit={limit}&ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchUserWithPaging(
            @PathVariable(QUERY) final String query,
            @PathVariable(LIMIT) final int limit);

    @RequestMapping(value = USER_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByUUID(@PathVariable(QUERY) final String query);

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> approveDocument(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> submitDocument(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateUserEntity(@PathVariable("uuid") final String uuid, @RequestBody final UserEntity newUser);
    
    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updatePhoneNumber(@PathVariable("uuid") String uuid, @RequestBody PhoneNumberDTO phoneDto);
    
    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateVerifyPhoneNumber(@PathVariable("uuid") String uuid, @RequestBody VerifyPhoneNumberDTO phoneDto);
}
