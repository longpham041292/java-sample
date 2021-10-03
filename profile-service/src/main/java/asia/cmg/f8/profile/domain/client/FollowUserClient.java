package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "followers", url = "${feign.socialUrl}", path = "/social/follow")
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public interface FollowUserClient {

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}?", method = POST)
    UserEntity createFollowingConnection(@PathVariable("followingUser") String followingUser,
                                   @PathVariable("followedUser") String followedUser);

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}", method = GET)
    Resource<Boolean> checkFollowConnection(@PathVariable("followingUser") String followingUser,
                                                  @PathVariable("followedUser") String followedUser);

    @RequestMapping(value = "/users/{followingUser}/following/users/{followedUser}", method = DELETE)
    Resource<Boolean> deleteFollowingConnection(@PathVariable("followingUser") String followingUser,
                                             @PathVariable("followedUser") String followedUser);

    @RequestMapping(value = "/users/{userId}/followers/users?limit={limit}&cursor={cursor}", method = GET)
    PagedUserResponse<String> getFollowerConnection(@PathVariable("userId") String userId,
                                            @RequestParam(value = "limit") int pageSize,
                                            @RequestParam(value = "cursor") String cursor);

    @RequestMapping(value = "/users/{userId}/followers/users/count", method = GET)
    Resource<Long> countFollowerConnection(@PathVariable("userId") String userId);

    @RequestMapping(value = "/users/{userId}/following/users?limit={limit}&cursor={cursor}", method = GET)
    PagedUserResponse<String> getFollowingConnection(@PathVariable("userId") String followedUserId,
                                            @RequestParam(value = "limit") int pageSize,
                                            @RequestParam(value = "cursor") String cursor);

    @RequestMapping(value = "/users/{userId}/following/users/entities?limit={limit}&cursor={cursor}", method = GET)
    PagedUserResponse<UserEntity> getFollowingConnectionEntity(@PathVariable("userId") String followedUserId,
                                                @RequestParam(value = "limit") int pageSize,
                                                @RequestParam(value = "cursor") String cursor,
                                                @RequestParam(value = "userType") String userType,
                                                @RequestParam(value = "keyword") String keyword);

    @RequestMapping(value = "/users/{userId}/followers/users/entities?limit={limit}&cursor={cursor}", method = GET)
    PagedUserResponse<UserEntity> getFollowerConnectionEntity(@PathVariable("userId") String followedUserId,
                                                               @RequestParam(value = "limit") int pageSize,
                                                               @RequestParam(value = "cursor") String cursor,
                                                               @RequestParam(value = "userType") String userType,
                                                               @RequestParam(value = "keyword") String keyword);

}
