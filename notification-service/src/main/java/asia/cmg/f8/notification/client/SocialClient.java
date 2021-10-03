package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 1/13/17.
 */
@FeignClient(value = "socials", url = "${feign.url}", fallback = SocialClientFallback.class)
public interface SocialClient {

    String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";

    /**
     * Get followers of user uuid
     *
     * @param uuid  user uuid
     * @param query query string
     * @return list of user that are following user uuid
     */
    @RequestMapping(method = GET, path = "/users/{uuid}/followers?" + SECRET_QUERY
            + "&ql={query}&limit=${notification.maxSearchResult}",
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<SocialUserInfo> getFollowers(@PathVariable("uuid") final String uuid,
                                                  @PathVariable("query") final String query);
}
