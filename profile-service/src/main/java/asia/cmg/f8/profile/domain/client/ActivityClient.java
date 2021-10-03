package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.ActivityEntity;
import rx.Observable;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 12/21/16.
 */

@FeignClient(value = "socialPost", url = "${feign.url}", fallback = SocialPostClientFallbackImpl.class)
public interface ActivityClient {
    String USER_ID = "user_id";
    String LIMIT = "limit";
    String CURSOR = "cursor";
    String POST_ID = "post_id";
    String QUERY = "query";
    String QUERY_PARAM = "&ql={query}";

    String SECRET_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    String GET_USER_SOCIAL_POST = "/users/{user_id}/activities?" + SECRET_QUERY + QUERY_PARAM;

    String GET_SOCIAL_POST_BY_QUERY = "/activities?" + SECRET_QUERY;// + QUERY_PARAM;

    String GET_POST_WITH_LIMIT_AND_CURSOR = GET_USER_SOCIAL_POST + "&limit={limit}&cursor={cursor}";
    String USER_LIKE_POST_QUERY = "/users/{user_id}/likes/activities?" + SECRET_QUERY + QUERY_PARAM;
    String GET_POST_BY_UUID = "/activities/{post_id}?" + SECRET_QUERY;

    String USER_LIKE_OR_UNLIKE_POST_ID = "/users/{user_id}/likes/activities/{post_id}?" + SECRET_QUERY;
    String GET_USER_FEED = "/users/{user_id}/feed?limit={limit}&cursor={cursor}&" + SECRET_QUERY + QUERY_PARAM;
    String QUERY_POST = "/activities?" + SECRET_QUERY + QUERY_PARAM;
    String QUERY_POST_ACTIVITY = "/activities?" + SECRET_QUERY + QUERY_PARAM + "&limit={limit}&cursor={cursor}";
    String QUERY_POST_ACTIVITY_LIMIT = "/activities?" + SECRET_QUERY + QUERY_PARAM + "&limit={limit}";

    
    @RequestMapping(value = USER_LIKE_POST_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Observable<UserGridResponse<ActivityEntity>> getUserLikedPostStatusByQuery(@PathVariable(USER_ID) final String userId, @PathVariable(QUERY) final String query);
    
    @RequestMapping(value = QUERY_POST, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<ActivityEntity> getActivityByQuery(@PathVariable(QUERY) final String query);
    
    @RequestMapping(value = QUERY_POST_ACTIVITY_LIMIT, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<ActivityEntity> getActivityByQuery(@PathVariable(QUERY) final String query, @RequestParam(LIMIT) final int limit);
}
