package asia.cmg.f8.report.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.entiy.usergrid.PagedUserResponse;
import asia.cmg.f8.report.entiy.usergrid.UserEntity;

@FeignClient(value = "users", url = "${feign.url}", fallback = UserClientFallbackImpl.class)
public interface UserClient {

    String QUERY = "query";
    String LIMIT = "limit";
    String UUID = "uuid";
    String CURSOR = "cursor";
    String SECRET_QUERY = "client_id=${userGrid.clientId}&client_secret=${userGrid.clientSecret}";
    
    @RequestMapping(value = "/users/{uuid}?" + SECRET_QUERY, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUser(@PathVariable("uuid") String uuid);

    @RequestMapping(value = "/users?ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> searchUsers(@PathVariable(QUERY) final String query);

    @RequestMapping(value = "/users?limit={limit}&ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchUsers(@PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit);

    @RequestMapping(value = "/users?limit={limit}&cursor={cursor}&ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserGridResponse<UserEntity> searchUsers(@PathVariable(QUERY) String query, @RequestParam(LIMIT) int limit, @PathVariable(CURSOR) String cursor);
}
