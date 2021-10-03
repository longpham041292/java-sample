package asia.cmg.f8.commerce.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "followers", url = "${feign.socialUrl}", path = "/social/follow")
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public interface FollowUserClient {

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}?", method = POST)
    void createFollowingConnection(@PathVariable("followingUser") String followingUser,
                                   @PathVariable("followedUser") String followedUser);
}
