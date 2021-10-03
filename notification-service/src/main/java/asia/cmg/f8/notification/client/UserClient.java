package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author tung.nguyenthanh
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = UserClientFallbackImpl.class)
public interface UserClient {

    String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
    String GET_USER_BY_UUID_QUERY = "/users/{uuid}?" + SECRET_QUERY;
    String GROUP_QUERY_PREFIX = "/roles/admin/users?" + SECRET_QUERY + "&ql={query}";
    String PUT_USER = "/users/{uuid}?" + SECRET_QUERY;

    @RequestMapping(value = GET_USER_BY_UUID_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByUuid(@PathVariable("uuid") String uuid);

    @RequestMapping(value = GROUP_QUERY_PREFIX, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getEmailAdmins(@PathVariable("query") String query);
    
    
    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateLastMsg(@PathVariable("uuid") String uuid, @RequestBody UserEntity newUser);
    
}
