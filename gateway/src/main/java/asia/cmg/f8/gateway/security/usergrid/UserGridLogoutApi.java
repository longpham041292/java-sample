package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created on 11/3/16.
 */
@FeignClient(value = "auth", name = "auth", url = "${usergrid.baseUrl}", fallback = UserGridLogoutApiFallback.class)
public interface UserGridLogoutApi {

//	String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
	
    @RequestMapping(value = "/users/{uuid}/revoketokens", method = PUT, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> logout(@RequestParam("uuid") final String uuid);
}
