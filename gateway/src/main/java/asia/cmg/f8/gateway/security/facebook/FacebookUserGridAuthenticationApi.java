package asia.cmg.f8.gateway.security.facebook;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 11/3/16.
 */
@FeignClient(name = "userGridAuth", value = "userGridAuth", url = "${usergrid.baseUrl}", fallback = FacebookUserGridAuthenticationApiFallback.class)
public interface FacebookUserGridAuthenticationApi {

    @RequestMapping(value = "/auth/facebook?ttl={ttl}&fb_access_token={fbToken}", method = GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> authenticate(@RequestParam("fbToken") final String fbToken, @RequestParam("ttl") final long ttl);
}
