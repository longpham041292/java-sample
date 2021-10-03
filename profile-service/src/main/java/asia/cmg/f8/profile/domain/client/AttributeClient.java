package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.Attribute;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 11/10/16.
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = AttributeClientFallbackImpl.class)
public interface AttributeClient {
	String SECRET_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
	String LIMIT = "limit";
    
//    @RequestMapping(value = "/lists?limit={limit}&ql={query}&"+ SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<Attribute> getAttributes(
//            @PathVariable(value = "query") String query,
//            @PathVariable(value = LIMIT) int limit,
//            @PathVariable(value = "token") String accessToken);
//
//    @RequestMapping(value = "/lists?limit={limit}&"+ SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<Attribute> getAttributes(
//            @PathVariable(value = LIMIT) int limit,
//            @PathVariable(value = "token") String accessToken);
    
    @RequestMapping(value = "/lists?limit={limit}&ql={query}&" + SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Attribute> getAttributes(
            @PathVariable(value = "query") String query,
            @PathVariable(value = LIMIT) int limit);

    @RequestMapping(value = "/lists?limit={limit}&" + SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Attribute> getAttributes(
            @PathVariable(value = LIMIT) int limit);
}
