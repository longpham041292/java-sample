package asia.cmg.f8.profile.api.question;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.profile.api.profile.ProfileApi;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.client.UserBasicInfo;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.UserEntity;




@RestController
public class UserClubApi {

	private static final String USERS_UUID = "users_uuid";
	private static final String SUCCESS = "success";
	private static final String UTF8 = "UTF-8";
	private static final Logger LOG = LoggerFactory.getLogger(UserClubApi.class);
	
	
	private final UserClient userClient;
	private final PagedResourcesAssembler<UserBasicInfo> pagedResourcesAssembler;
	private final UserProfileProperties userProfileProperties;
	
	@Inject
	public UserClubApi(final UserClient userClient, final PagedResourcesAssembler<UserBasicInfo> pagedResourcesAssembler, final UserProfileProperties userProfileProperties){
	this.userClient = userClient;
	this.pagedResourcesAssembler = pagedResourcesAssembler;
	this.userProfileProperties = userProfileProperties;
	}
	
	@RequiredAdminRole
	@RequestMapping(value = "/admin/club/{club_id}/whoshot", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> addUserToclub(@PathVariable("club_id") final String club_id,
			@RequestBody final Map<String, List<String>> userClubRequest, final Account account) {
		
	
		LOG.info("----- >>>>>>>>>>>>> ------------ >>>>>>>>>>>>>>>> .{} ,", club_id);

		if (userClubRequest.containsKey(USERS_UUID)) {

			final List<String> usersList = userClubRequest.get(USERS_UUID);

			for (final String user_uuid : usersList) {
				LOG.info("--- >>> --- , {}",user_uuid);
				userClient.addUserToClub(club_id, user_uuid);
				userClient.addClubToUser(club_id, user_uuid);
			}
		}

		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
	}
	
	
	@RequiredAdminRole
	@RequestMapping(value = "/admin/club/{club_id}/whoshot", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PagedResources<Resource<UserBasicInfo>> searchUserToClub(
			@PathVariable("club_id") final String club_id,
			@RequestParam(required = false) final String cursor,
            @RequestParam(value = "limit", defaultValue = "20") final int size) {
		
		LOG.info("--- >>> - club_id,cursor,limit -- , {} , {} , {}",club_id,cursor,size);
		
        int pageSize = size;
        pageSize = pageSize > userProfileProperties.getContact().getMaxLoad() ? userProfileProperties.getContact().getMaxLoad() : pageSize;
        pageSize = pageSize < 1 ? userProfileProperties.getContact().getSizeLoad() : pageSize;
		
		final PagedUserResponse<UserEntity> response;
		
		response = userClient.searchUsersFromClub(club_id,size,cursor);
		
		final String cursorUri = ControllerLinkBuilder.linkTo(ProfileApi.class).toString()
                + "?size=" + pageSize + "&cursor=" + response.getCursor();
		
		final Link link = new Link(cursorUri, UTF8);
		
		return pagedResourcesAssembler.toResource(
                new PageImpl<>(response.getEntities().stream()
                        .map(UserBasicInfo::new)
                        .collect(Collectors.toList())), link);
	}
	
	
	@RequiredAdminRole
	@RequestMapping(value = "/admin/club/{club_id}/whoshot", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> deleteUserToClub(@PathVariable("club_id") final String club_id,
			@RequestParam("pt_id") final String pt_id) {

		LOG.info("----- ***  , {} ", club_id);
		LOG.info("----- *** , {} ", pt_id);

		userClient.deleteUserToClub(club_id,pt_id);
		userClient.deleteClubToUser(club_id,pt_id);
		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);

	}
	
}
