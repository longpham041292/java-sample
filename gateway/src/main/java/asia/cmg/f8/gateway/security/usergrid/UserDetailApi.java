package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 11/3/16.
 */
@FeignClient(value = "users", name = "users", url = "${usergrid.baseUrl}", fallback = UserDetailApiFallback.class)
public interface UserDetailApi {

    @RequestMapping(value = "/users/me", method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Map<String, Object>> currentUser(@RequestHeader("Authorization") final String accessToken);
}
