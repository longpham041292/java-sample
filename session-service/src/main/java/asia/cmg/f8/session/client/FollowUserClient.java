package asia.cmg.f8.session.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "followers", url = "${feign.socialUrl}", path = "/social/follow")
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public interface FollowUserClient {

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}", method = GET)
    Resource<Boolean> checkFollowConnection(@PathVariable("followingUser") String followingUser,
                                            @PathVariable("followedUser") String followedUser);

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}", method = DELETE)
    Resource<Boolean> deleteFollowingConnection(@PathVariable("followingUser") String followingUser,
                                                @PathVariable("followedUser") String followedUser);

}
