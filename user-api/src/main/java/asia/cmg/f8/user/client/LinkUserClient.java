package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.dto.UserAuthResponse;
import asia.cmg.f8.user.dto.UserInfoResponse;
import asia.cmg.f8.user.entity.LinkUserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 1/10/17.
 */
@FeignClient(value = "linkUsers", url = "${feign.url}", fallback = LinkUserClientFallback.class)
public interface LinkUserClient {

    @RequestMapping(method = GET, value = "/link_users?client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}&ql={query}", produces = APPLICATION_JSON_VALUE)
    UserGridResponse<LinkUserEntity> findByQuery(@PathVariable("query") final String query);

    @RequestMapping(method = POST, value = "/link_users?client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<LinkUserEntity> createLink(@RequestBody final List<LinkUserEntity> entities);

    @RequestMapping(method = DELETE, value = "/link_users?client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}&ql={query}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<LinkUserEntity> unLink(@PathVariable("query") final String query);

    @RequestMapping(method = GET, value = "/users?client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}&ql={query}", produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserInfoResponse> findUserByQuery(@PathVariable("query") final String query);

    @RequestMapping(method = POST, value = "/token?ttl=1", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserAuthResponse doAuth(final Map<String, Object> body);
}
