package asia.cmg.f8.gateway.security.linkaccount;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 1/9/17.
 */
@FeignClient(name = "linkedToken", url = "${usergrid.baseUrl}")
public interface LinkedTokenClient {

    /**
     * Request new access token for a linked user.
     *
     * @param linkedUserUuid the uuid of linked user.
     * @param accessToken    the access token of current logged-in user.
     * @return new access token.
     */
    @RequestMapping(method = POST, path = "/users/{uuid}/token?ttl={ttl}&access_token={accessToken}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    AccessTokenResponse switchUser(@PathVariable("uuid") final String linkedUserUuid, @PathVariable("accessToken") final String accessToken, @PathVariable("ttl") final long ttl);
}
