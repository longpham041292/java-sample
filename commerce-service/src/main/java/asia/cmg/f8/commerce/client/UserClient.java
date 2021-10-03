package asia.cmg.f8.commerce.client;

import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.common.util.UserGridResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "users", url = "${feign.url}", fallback = UserClientFallback.class)
public interface UserClient {
    
	String SECRET_QUERY = "client_id=${userGrid.clientId}&client_secret=${userGrid.clientSecret}";
    String GET_USER = "/users/{uuid}?" + SECRET_QUERY;
    String GET_USER_INGROUP = "/groups/{group}/users?" + SECRET_QUERY  + "&ql={query}";
    String UUID = "uuid";
    String QUERY = "query";
    String GROUP = "group";
    
    @RequestMapping(value = GET_USER)
	UserGridResponse<UserEntity> getUser(@PathVariable(UUID) String uuid);
    
    @RequestMapping(value = GET_USER_INGROUP, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserInGroup(@PathVariable(GROUP) final String group, @RequestParam(value = QUERY, required = false) final String query);

}
