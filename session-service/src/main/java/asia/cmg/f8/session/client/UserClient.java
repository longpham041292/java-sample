package asia.cmg.f8.session.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.dto.ActivityPostInfo;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.PagedUserResponse;
import asia.cmg.f8.session.entity.WhosHotConfig;

/**
 * UserGrid's user api
 * <p>
 * Created on 10/11/16.
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = FallbackUserClient.class)
public interface UserClient {

    String LIMIT = "limit";
    String QUERY = "query";
    
    String SECRET_QUERY = "client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}";
    String GET_USER = "/users/{userId}?" + SECRET_QUERY;
    String USER_QUERY_PREFIX = "/users?" + SECRET_QUERY;
    String USER_QUERY = USER_QUERY_PREFIX + "&ql={query}";
    String USER_QUERY_WITH_LIMIT_AND_CURSOR = USER_QUERY +"&limit={limit}&cursor={cursor}";
    

    @RequestMapping(value = GET_USER, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUser(@PathVariable("userId") String userId);
    
    @RequestMapping(value = GET_USER, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateUser(@PathVariable("userId") final String userId,
                                                @RequestBody final UserEntity request);
    
    @RequestMapping(value = "/groups/{groupUuid}/users/{userUuid}?" + SECRET_QUERY, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> addUserToGroup(@PathVariable("userUuid") String userUuid, @PathVariable("groupUuid") String groupUuid);


    @RequestMapping(method = GET, path = "/users/{uuid}?ql=select uuid,language&client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<BasicUserInfo> findUserLanguage(@PathVariable("uuid") final String userUuid);

    @RequestMapping(method = RequestMethod.GET, path = "/activities?ql={query}&client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<ActivityPostInfo> getActivitiesOfPT(@PathVariable("query") final String query);

    @RequestMapping(value = "/configs?limit={limit}&ql={query}&" + SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<WhosHotConfig> getWhosHotConfig(@PathVariable(value = "query") String query,@PathVariable(value = LIMIT) int limit);

    @RequestMapping(value = "/configs?ql={query}&" + SECRET_QUERY, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<WhosHotConfig> updateWhosHotConfig(@PathVariable(value = "query") final String query, @RequestBody final Object question);
    
	 @RequestMapping(value = "/clubs/{club_uuid}/whoshot/users?client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}&limit={limit}&cursor={cursor}&ql=activated=true",
	            method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	 UserGridResponse<UserEntity> searchUsersFromClub(@PathVariable("club_uuid") final String club_uuid, @PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);
	 
	 
	 @RequestMapping(value = "/users/{user_uuid}/following/users/{follower_uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
	    UserGridResponse<UserEntity> deleteFollowingConnectionBySecret(@PathVariable("user_uuid") final String userUuid, @PathVariable("follower_uuid") final String followerUuid);

    @RequestMapping(value = "/users?ql={query}&" + SECRET_QUERY, method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<UserEntity> getUserByQuery(@PathVariable("query") final String query);

    @RequestMapping(value = USER_QUERY_PREFIX, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> createUser(@RequestBody Object user);

    @RequestMapping(method = RequestMethod.GET, path = "/users/{uuid}/feed?" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    PageResponse<ActivityPostInfo> getFeeds(@PathVariable("uuid") String userUuid,
                                            @RequestParam("cursor") String cursor,
                                            @RequestParam("ql") String query,
                                            @RequestParam("limit") Integer limit );

    @RequestMapping(method = RequestMethod.DELETE, path = "/users/{uuid}/feed/{feed_id}?" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    void deleteFeed(@PathVariable("uuid") String userUuid, @PathVariable("feed_id") String feedId);
    
    @RequestMapping(value = "/users?ql={query}&client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}",
            method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> searchUsersByQuery(@PathVariable(value = "query") final String query);
    
    @RequestMapping(value = USER_QUERY_WITH_LIMIT_AND_CURSOR, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUserByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit,
            @RequestParam(value = "cursor", required = false) final String cursor);
    
    @RequestMapping(value = USER_QUERY + "&limit={limit}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUserByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit);
}
