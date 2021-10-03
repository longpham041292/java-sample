package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.profile.api.profile.ProfileApi;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.social.FollowingAction;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.client.FollowUserClient;
import asia.cmg.f8.profile.domain.client.UserBasicInfo;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.service.ContactService;
import asia.cmg.f8.profile.domain.service.UserProfileService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@SuppressWarnings("PMD")
public class ContactApi {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileService.class);

    private static final String UTF8 = "UTF-8";
    private static final String SUCCESS = "success";
    private static final String PROFILE_TAG_NAME = "Profile Management";
    private static final String SEARCH_USER_WITH_KEYWORD = "select * where (username = '*%s*' or name = '*%s*') and (userType = 'eu' or (userType = 'pt' and status.document_status = 'approved') or userType = 'club') and (not uuid = '%s') and activated = true order by username asc, name asc";
    private static final String SEARCH_USER = "select * where (userType = 'eu' or (userType = 'pt' and status.document_status = 'approved') or userType = 'club') and activated = true order by username asc, name asc";
    private static final String SEARCH_SAME_LEVEL_TRAINER = "select * where (username = '*%s*' or name = '*%s*' or level = '%s') and userType = 'pt' and (not uuid = '%s') and activated = true order by username asc, name asc";
    private final PagedResourcesAssembler<UserBasicInfo> pagedResourcesAssembler;
    private final UserClient userClient;
    private final UserProfileProperties userProfileProperties;
    private final ContactService contactService;
    private final UserProfileService userProfileService;
    private final FollowUserClient followUserClient;

    @Inject
    public ContactApi(final UserClient userClient,
                      final PagedResourcesAssembler<UserBasicInfo> pagedResourcesAssembler,
                      final UserProfileProperties userProfileProperties,
                      final ContactService contactService,
                      final UserProfileService userProfileService,
                      final FollowUserClient followUserClient) {
        this.userClient = userClient;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.userProfileProperties = userProfileProperties;
        this.contactService = contactService;
        this.userProfileService = userProfileService;
        this.followUserClient = followUserClient;
    }


    @ApiOperation(value = "Get following connections by user type", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/me/following", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public PagedResources<Resource<UserBasicInfo>> getFollowingByType(@RequestParam(name = "type", required = false) final String reqUserType,
                                                                      @RequestParam(required = false, defaultValue = "20") final int size,
                                                                      @RequestParam(required = false) final String keyword,
                                                                      @RequestParam(required = false) String cursor, final Account account)
            throws UnsupportedEncodingException {

        int pageSize = size;

        pageSize = pageSize > userProfileProperties.getContact().getMaxLoad() ? userProfileProperties.getContact().getMaxLoad() : pageSize;
        pageSize = pageSize < 1 ? userProfileProperties.getContact().getSizeLoad() : pageSize;

        PagedUserResponse<UserEntity> response = followUserClient.getFollowingConnectionEntity(account.uuid(), size,
                cursor, reqUserType, keyword);
      
        cursor = response.getCursor();
        
        String cursorUri = ControllerLinkBuilder.linkTo(ProfileApi.class).toString()
                + "?type=" + reqUserType + "&size=" + pageSize 
                + "&cursor=" + (!StringUtils.isEmpty(cursor) ? cursor : "0")
                + "&keyword=" + (!StringUtils.isEmpty(keyword) ? URLEncoder.encode(keyword, UTF8) : "");
        Link link = new Link(cursorUri);

    	PagedResources<Resource<UserBasicInfo>> result =  pagedResourcesAssembler.toResource(
                 new PageImpl<>(response.getEntities().stream().filter(e -> e != null)
                         .map(UserBasicInfo::new)
                         .collect(Collectors.toList())), link);
    	if(StringUtils.isEmpty(cursor)){
        	result.removeLinks();
    	}
    	return result;

    }
    
    @ApiOperation(value = "Get all followers connections", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/profile/{uuid}/follower", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public PagedResources<Resource<UserBasicInfo>> getAllFollowers(@PathVariable("uuid") final String uuid,
    																  @RequestParam(required = false, defaultValue = "20") final int size,
                                                                      @RequestParam(required = false) final String keyword,
                                                                      @RequestParam(required = false) final String cursor, final Account account)
            throws UnsupportedEncodingException {

        int pageSize = (size < 1) ? userProfileProperties.getContact().getSizeLoad() : size;

        PagedUserResponse<UserEntity> followerUsers = followUserClient.getFollowerConnectionEntity(uuid, pageSize, cursor, null, keyword);

        Collection<String> followingUserIdsOfCurrentUser = followUserClient.getFollowingConnection(uuid, pageSize, cursor)
                .getEntities();
        final String cursorUri = ControllerLinkBuilder.linkTo(ProfileApi.class).toString()
                + "?size=" + pageSize + "&cursor=" + followerUsers.getCursor()
                + (!StringUtils.isEmpty(keyword) ? "&keyword=" + URLEncoder.encode(keyword, UTF8) : "");
        final Link link = new Link(cursorUri);

        return pagedResourcesAssembler.toResource(
                new PageImpl<>(followerUsers.getEntities().stream()
                        .map(entity -> {
                            UserBasicInfo userBasicInfo = new UserBasicInfo(entity);
                            userBasicInfo.setFollowed(followingUserIdsOfCurrentUser.contains(entity.getUuid()));
                            return userBasicInfo;
                        })
                        .collect(Collectors.toList())), link);
    }

    @ApiOperation(value = "Create a new connection", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/me/following", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public UserBasicInfo createFollowingByType(
            @RequestBody final Map<String, String> body, final Account account) {
        final Optional<String> followerUuid = Optional.ofNullable(body.get("follower_uuid"));
        if (!followerUuid.isPresent()) {
            throw new IllegalArgumentException("Follower Uuid is required for this action.");
        }

        if (followerUuid.get().equalsIgnoreCase(account.uuid())) {
            throw new IllegalArgumentException("Can't follow by yourself.");
        }
        
        UserEntity userEntity = followUserClient.createFollowingConnection(account.uuid(), followerUuid.get());
        contactService.fireFollowingEvent(account.uuid(), followerUuid.get(),
                FollowingAction.FOLLOW, userProfileService.getCurrentUser(account));
        return new UserBasicInfo(userEntity);
    }

    @ApiOperation(value = "Remove a connection", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/me/following/{follower_uuid}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    public Map<String, Boolean> deleteFollowingByType(
            @PathVariable("follower_uuid") final String followerUuid, final Account account) {

        Boolean deleteFollowingResult = followUserClient.deleteFollowingConnection(account.uuid(), followerUuid).getContent();
        return Collections.singletonMap(SUCCESS, deleteFollowingResult);
    }

    @ApiOperation(value = "Search user - end user and personal trainer", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public PagedResources<Resource<UserBasicInfo>> searchUserByUsernameOrName(
            @RequestParam(name = "keyword", required = false) final String keyword,
            @RequestParam(required = false, defaultValue = "20") final int size,
            @RequestParam(required = false) final String cursor, final Account account) throws UnsupportedEncodingException {

    	final String query = StringUtils.isEmpty(keyword)? SEARCH_USER : SEARCH_USER_WITH_KEYWORD;
        int pageSize = size;
        pageSize = pageSize > userProfileProperties.getContact().getMaxLoad() ? userProfileProperties.getContact().getMaxLoad() : pageSize;
        pageSize = pageSize < 1 ? userProfileProperties.getContact().getSizeLoad() : pageSize;

        final PagedUserResponse<UserEntity> response;

        if (StringUtils.isEmpty(cursor)) {
            response = userClient.searchUsers(
                    String.format(query, keyword, keyword, (null != account) ? account.uuid() : "a61a8687-c460-11e7-b114-02420a010407"), pageSize);
        } else {
            response = userClient.searchUsersWithCursor(
                    String.format(query, keyword, keyword, (null != account) ? account.uuid() : "a61a8687-c460-11e7-b114-02420a010407"), pageSize,
                    URLDecoder.decode(cursor, UTF8));
        }

        final String cursorUri = ControllerLinkBuilder.linkTo(ProfileApi.class).toString()
                + "?size=" + pageSize + "&cursor=" + response.getCursor()
                + (!StringUtils.isEmpty(keyword) ? "&keyword=" + URLEncoder.encode(keyword, UTF8) : "");
        final Link link = new Link(cursorUri, UTF8);
        return pagedResourcesAssembler.toResource(
                new PageImpl<>(response.getEntities().stream()
                        .map(UserBasicInfo::new)
                        .collect(Collectors.toList())), link);
    }

    @ApiOperation(value = "Search same level trainer", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/search/trainers/{trainerId}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public PagedResources<Resource<UserBasicInfo>> searchSameLevelTrainerByUsernameOrName(
            @RequestParam("keyword") final String keyword,
            @PathVariable("trainerId") final String trainerId,
            @RequestParam(required = false, defaultValue = "20") final int size,
            @RequestParam(required = false) final String cursor, final Account account) throws UnsupportedEncodingException {

        if (StringUtils.isEmpty(keyword)) {
            throw new IllegalArgumentException("Missing search keyword");
        }

        if (StringUtils.isEmpty(trainerId)) {
            throw new IllegalArgumentException("Missing trainer Uuid");
        }

        final UserGridResponse<UserEntity> trainerResponse = userClient.getUser(trainerId);
        if (trainerResponse.getEntities().isEmpty()) {
            throw new IllegalArgumentException("The trainer is not exist in the system");
        }

        final UserEntity trainer = trainerResponse.getEntities().get(0);

        if (trainer.getUserType() == UserType.EU) {
            throw new IllegalArgumentException("The provided UUID is not trainer.");
        }

        int pageSize = size;
        pageSize = pageSize > userProfileProperties.getContact().getMaxLoad() ? userProfileProperties.getContact().getMaxLoad() : pageSize;
        pageSize = pageSize < 1 ? userProfileProperties.getContact().getSizeLoad() : pageSize;

        final PagedUserResponse<UserEntity> response;

        if (StringUtils.isEmpty(cursor)) {
            response = userClient.searchUsers(
                    String.format(SEARCH_SAME_LEVEL_TRAINER, keyword, keyword, trainer.getLevel(), trainerId), pageSize);
        } else {
            response = userClient.searchUsersWithCursor(
                    String.format(SEARCH_SAME_LEVEL_TRAINER, keyword, keyword, trainer.getLevel(), trainer.getUuid()), pageSize,
                    URLDecoder.decode(cursor, UTF8));
        }

        final String cursorUri = ControllerLinkBuilder.linkTo(ProfileApi.class).toString()
                + "?size=" + pageSize + "&cursor=" + response.getCursor()
                + (!StringUtils.isEmpty(keyword) ? "&keyword=" + URLEncoder.encode(keyword, UTF8) : "");
        final Link link = new Link(cursorUri, UTF8);
        return pagedResourcesAssembler.toResource(
                new PageImpl<>(response.getEntities().stream()
                        .map(UserBasicInfo::new)
                        .collect(Collectors.toList())), link);
    }

}
