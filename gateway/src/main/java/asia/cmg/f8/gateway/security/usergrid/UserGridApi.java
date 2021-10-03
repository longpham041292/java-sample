package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.usergridconfig.UserGridAuthConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 10/19/16.
 */
@FeignClient(value = "users", name = "users", url = "${usergrid.baseUrl}", configuration = UserGridAuthConfig.class, fallback = UserGridApiFallback.class)
public interface UserGridApi {

    @RequestMapping(value = "/token", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> requestAccessToken(@RequestBody final Map<String, Object> body);

    @RequestMapping(value = "/users?client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Map<String, Object>> getUserByQuery(@PathVariable("query") final String query);
    
    @RequestMapping(value = "/users/{uuid}?client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Map<String, Object>> getUserByUuid(@PathVariable("uuid") final String uuid);

}
