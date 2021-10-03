package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.ClubEntity;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.RatingSessionEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.dto.WhosHotConfig;
import rx.Observable;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * UserGrid's user api
 * <p>
 * Created on 10/11/16.
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = UserClientFallbackImpl.class)
public interface UserClient {

    String QUERY = "query";
    String LIMIT = "limit";
    String UUID = "uuid";
    String CURSOR = "cursor";
    String SECRET_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    
    @RequestMapping(value = "/configs?limit={limit}&ql={query}&" + SECRET_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<WhosHotConfig> getWhosHotConfig(@PathVariable(value = "query") String query,@PathVariable(value = LIMIT) int limit);
    
    @RequestMapping(value = "/users/{uuid}?" + SECRET_QUERY, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUser(@PathVariable("uuid") String uuid);

//    @RequestMapping(value = "/users/{uuid}", method = GET, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<UserEntity> getCurrentUser(@PathVariable(UUID) final String uuid);

//    @RequestMapping(value = "/users/{uuid}?access_token={token}", method = PUT, consumes = APPLICATION_JSON_VALUE)
//    UserGridResponse<UserEntity> updateProfile(
//            @RequestBody UserEntity userEntity,
//            @PathVariable(value = UUID) String userId,
//            @PathVariable(value = "token") String token);

    @RequestMapping(value = "/users/{uuid}?" + SECRET_QUERY, method = PUT, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateProfile(
            @RequestBody UserEntity userEntity,
            @PathVariable(value = UUID) String userId);

    @RequestMapping(value = "/users/{uuid}?" + SECRET_QUERY, method = PUT, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateProfileBySystem(
            @RequestBody Map<String, Object> userProperties,
            @PathVariable(value = UUID) String userId);

    @RequestMapping(value = "/users?ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByQuery(@PathVariable(QUERY) final String query);

    @RequestMapping(value = "/users?limit={limit}&ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchUsers(@PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit);

    @RequestMapping(
            value = "/users?limit={limit}&cursor={cursor}&ql={query}&" + SECRET_QUERY,
            method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchUsersWithCursor(@PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);

//    @RequestMapping(value = "/users/me/contracting/users?limit={limit}&ql={query}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
//    PagedUserResponse<UserEntity> getContractingConnection(@RequestParam(ACCESS_TOKEN) final String token, @PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit);

    @RequestMapping(value = "/users/{uuid}/contracting/users?ql={query}&" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> searchContractingUser(@PathVariable(UUID) final String uuid, @PathVariable(QUERY) final String query);

    @RequestMapping(value = "/users/{pt_uuid}/contracting/users/{eu_uuid}?" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> createContractingConnection(@PathVariable("pt_uuid") final String ptUuid, @PathVariable("eu_uuid") final String euUuid);
    
//    @RequestMapping(value = "/users?ql={query}&limit={limit}", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
//    UserGridResponse<UserEntity> getUserByQuery(@RequestParam(ACCESS_TOKEN) final String token,@PathVariable("query") final String query, @PathVariable(LIMIT) final int limit);
    
	@RequestMapping(value = "/users/{pt_uuid}/testimonials/rating_sessions/{session_uuid}?" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	PagedUserResponse<UserEntity> updateRatingOfTheTrainer(@PathVariable("pt_uuid") final String ptUuid,@PathVariable("session_uuid") final String session_uuid);

	@RequestMapping(value = "/users/{pt_uuid}/testimonials/rating_sessions/{session_uuid}?" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	PagedUserResponse<RatingSessionEntity> getRatingSessions(@PathVariable("pt_uuid") final String ptUuid,@PathVariable("session_uuid") final String session_uuid);

	@RequestMapping(value = "/users/{pt_uuid}/testimonials/rating_sessions?limit={limit}&cursor={cursor}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	PagedUserResponse<RatingSessionEntity> getAllRatingSessions(@PathVariable("pt_uuid") final String ptUuid, @PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);
	
	@RequestMapping(value = "/users/{pt_uuid}/testimonials/rating_sessions/{session_uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
	PagedUserResponse<RatingSessionEntity> deleteRatingSessions(@PathVariable("pt_uuid") final String ptUuid,@PathVariable("session_uuid") final String session_uuid);
	
	 @RequestMapping(value = "/rating_sessions?ql={query}&limit={limit}&cursor={cursor}&" + SECRET_QUERY,
	            method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<RatingSessionEntity> getRatingSessions(@PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);
	 
	 @RequestMapping(value = "/users?ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	 Observable<UserGridResponse<UserEntity>> getUserByQueryAvatar(@PathVariable(QUERY) final String query);
	 
	 @RequestMapping(value = "/clubs/{club_uuid}/whoshot/users/{user_uuid}?" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<Object> addUserToClub(@PathVariable("club_uuid") final String club_uuid,@PathVariable("user_uuid") final String user_uuid);
	 
	 @RequestMapping(value = "/users/{user_uuid}/whoshot/clubs/{club_uuid}?" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<Object> addClubToUser(@PathVariable("club_uuid") final String club_uuid,@PathVariable("user_uuid") final String user_uuid);
	 
	 @RequestMapping(value = "/clubs/{club_uuid}/whoshot/users/{user_uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<Object> deleteUserToClub(@PathVariable("club_uuid") final String club_uuid,@PathVariable("user_uuid") final String user_uuid);
	 
	 @RequestMapping(value = "/users/{user_uuid}/whoshot/clubs/{club_uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<Object> deleteClubToUser(@PathVariable("club_uuid") final String club_uuid,@PathVariable("user_uuid") final String user_uuid);
	
	 @RequestMapping(value = "/clubs/{club_uuid}/whoshot/users?limit={limit}&cursor={cursor}&" + SECRET_QUERY,
	            method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	 PagedUserResponse<UserEntity> searchUsersFromClub(@PathVariable("club_uuid") final String club_uuid, @PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);
	 
	 @RequestMapping(value = "/users/{user_uuid}/clubs/{club_uuid}?" + SECRET_QUERY, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	 PagedUserResponse<ClubEntity> addClubsOfOwner(@PathVariable("user_uuid") final String user_uuid, @PathVariable("club_uuid") final String clubUuid);
	 
	 @RequestMapping(value = "/users/{user_uuid}/clubs?" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	 PagedUserResponse<ClubEntity> getClubsFromOwner(@PathVariable("user_uuid") final String user_uuid);
	 
	 @RequestMapping(value = "/users/{user_uuid}/clubs/{club_uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	 PagedUserResponse<ClubEntity> deleteClubOfOwner(@PathVariable("user_uuid") final String user_uuid, @PathVariable("club_uuid") final String clubUuid);
	 
	 @RequestMapping(value = "/users?ql={query}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
     PagedUserResponse<UserEntity> getPTsInfo(@PathVariable(QUERY) final String query);

}
