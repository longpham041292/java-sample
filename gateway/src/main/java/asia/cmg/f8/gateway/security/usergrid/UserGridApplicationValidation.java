package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.security.exception.InValidClientIdAndSecretException;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * API to invoke a new access token using clientId and clientSecret.
 * <p>
 * Created on 11/11/16.
 */
@FeignClient(name = "appAuth", value = "appAuth", url = "${usergrid.baseUrl}")
public interface UserGridApplicationValidation {

    @RequestMapping(value = "/token", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    void validateUserGridConfiguration(final Map<String, String> content) throws InValidClientIdAndSecretException;
}
