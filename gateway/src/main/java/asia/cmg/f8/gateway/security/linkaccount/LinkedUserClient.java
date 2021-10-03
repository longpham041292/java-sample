package asia.cmg.f8.gateway.security.linkaccount;

import asia.cmg.f8.gateway.security.usergrid.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 1/9/17.
 */
@FeignClient(name = "linkedUsers", url = "${usergrid.baseUrl}", fallback = LinkedUserClientFallback.class)
public interface LinkedUserClient {

    @RequestMapping(method = GET, path = "/link_users?access_token={accessToken}&ql={query}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<LinkedUserInfo> findLinkedAccounts(@PathVariable("query") final String query, @PathVariable("accessToken") final String accessToken);
}
