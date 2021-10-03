package asia.cmg.f8.profile.api.profile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import asia.cmg.f8.common.dto.ApiRespListObject;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.Media;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.database.entity.LevelEntity;
import asia.cmg.f8.profile.database.entity.QuestionEntity;
import asia.cmg.f8.profile.database.entity.SearchTrackingEntity;
import asia.cmg.f8.profile.domain.client.ProfileRequest;
import asia.cmg.f8.profile.domain.entity.Attribute;
import asia.cmg.f8.profile.domain.entity.ClubEntity;
import asia.cmg.f8.profile.domain.entity.LocalizationEntity;
import asia.cmg.f8.profile.domain.entity.Profile;
import asia.cmg.f8.profile.domain.entity.ProfileVersion;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.entity.UserStatus;
import asia.cmg.f8.profile.domain.repository.LevelEntityRepository;
import asia.cmg.f8.profile.domain.service.QuestionPTiMatchService;
import asia.cmg.f8.profile.domain.service.QuestionQuery;
import asia.cmg.f8.profile.domain.service.SearchTrackingService;
import asia.cmg.f8.profile.domain.service.UserProfileService;
import asia.cmg.f8.profile.domain.service.UserPtiMatchService;
import asia.cmg.f8.profile.dto.ClubOwnerInfoRequest;
import asia.cmg.f8.profile.dto.QuestionPTiMatch;
import asia.cmg.f8.profile.dto.SearchTrackingRequest;
import asia.cmg.f8.profile.dto.UserPTiMatchDTO;
import asia.cmg.f8.profile.exception.ProfileUpdateException;
import io.swagger.annotations.ApiOperation;

/**
 * Created on 11/2/16.
 */
@RestController
public class ProfileApi {

    private static final String SUCCESS = "success";
    private static final String PROFILE_TAG_NAME = "Profile Management";
    private static final String UUID = "uuid";
    private static final String PROFILE_URL = "profileUrl";
    private static final int RECENT_SEARCH_LIMIT = 10;
    private static final boolean SHOULD_SEND_EVENT = true;
    //private static final String LEVEL = "userLevel";
    
    private final UserProfileService userProfileService;
    private static final Logger LOG = LoggerFactory.getLogger(ProfileApi.class);
    private Gson gson = new Gson();
    
    @Autowired
    private QuestionPTiMatchService questionsPTIService;
    
    @Autowired
    private SearchTrackingService searchTrackingService;
    
    @Autowired
    private UserPtiMatchService userPtiMatchService;
    
    @Autowired
    private LevelEntityRepository levelRepo; 

    @Inject
    public ProfileApi(final UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @ApiOperation(value = "Get attributes by language.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/attributes/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<Attribute> attributeList(
            @PathVariable(value = "language") final String language,
            final Account account) {
        return userProfileService.getAttributeLists(language, account);
    }
    
    @ApiOperation(value = "Get localization by categories language.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/mobile/v1/localization/{language}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLocalizationByCategoriesAndLanguage(@RequestBody Map<String, List<String>> body,
													            @PathVariable(value = "language") final String language,
													            final Account account) {
    	ApiRespListObject<LocalizationEntity> apiResponse = new ApiRespListObject<LocalizationEntity>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
    	LOG.info("[getLocalizationByCategoriesAndLanguage] Logged request params with body [{}] and language [{}]", gson.toJson(body), language);
    	
    	try {
    		List<String> categories = body.get("categories");
    		List<LocalizationEntity> data = new ArrayList<LocalizationEntity>();
        	if(categories == null || categories.isEmpty()) {
        		data = userProfileService.getLocalizationByLanguage(language);
        	} else {
        		data = userProfileService.getLocalizationByCategoryAndLanguage(categories, language);
        	}
        	apiResponse.setData(data);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOG.error("[getLocalizationByCategoriesAndLanguage] Calling failed with error detail [{}]", e.getMessage());
		}
    	
        return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Get attributes by language.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/attributes", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<Attribute> attributeList(
            final Account account) {
        return userProfileService.getAttributeLists(account);
    }

    @ApiOperation(value = "Set tagline for trainer.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/tagline", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateTagline(
            @RequestBody final Map<String, Object> body,
            final Account account) {

        final Optional<String> tagline = Optional.ofNullable((String) body.get("tagline"));

        if (!tagline.isPresent()) {
            throw new IllegalArgumentException("Tagline is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateTagline(tagline.get(), account));
    }
    
    @ApiOperation(value = "Set tagline for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/tagline/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateTaglineForUser(
            @RequestBody final Map<String, Object> body,
            @PathVariable(value = UUID) final String uuid) {

        final Optional<String> tagline = Optional.ofNullable((String) body.get("tagline"));

        if (!tagline.isPresent()) {
            throw new IllegalArgumentException("Tagline is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateTagline(tagline.get(), uuid));
    }
    
    @ApiOperation(value = "Set extended user type for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/admin/v1/extend_user_type/trainer/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateExtendedUserTypeForTrainer(@RequestBody final Map<String, Object> body,
													            @PathVariable(value = UUID) final String uuid) {

        final Optional<String> request = Optional.ofNullable((String) body.get("extend_user_type"));

        if (!request.isPresent()) {
            throw new IllegalArgumentException("ExtendUserType is required for this action.");
        }
        
        try {
        	ExtendUserType extendUserType = ExtendUserType.valueOf(request.get().toUpperCase());
        	return Collections.singletonMap(SUCCESS, userProfileService.updateExtendUserType(extendUserType, uuid));
		} catch (Exception e) {
			throw new IllegalArgumentException("ExtendUserType is not valid");
		}
    }

    @ApiOperation(value = "Set skills for trainer.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/skills", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateSkills(
            @RequestBody final Map<String, Object> body,
            final Account account) {

        final Optional<List<String>> skills = Optional.ofNullable((List<String>) body.get("skills"));

        if (!skills.isPresent()) {
            throw new IllegalArgumentException("Skills is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateSkills(skills.get(), account));
    }
    
    @ApiOperation(value = "Set skills for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/skills/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateSkills(
            @RequestBody final Map<String, Object> body,
            @PathVariable(value = UUID) final String uuid) {

        final Optional<List<String>> skills = Optional.ofNullable((List<String>) body.get("skills"));

        if (!skills.isPresent()) {
            throw new IllegalArgumentException("Skills is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateSkills(skills.get(), uuid));
    }

    @ApiOperation(value = "Set spoken languages for trainer.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/languages", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateLanguages(
            @RequestBody final Map<String, Object> body,
            final Account account) {

        final Optional<List<String>> languages = Optional.ofNullable((List<String>) body.get("languages"));

        if (!languages.isPresent()) {
            throw new IllegalArgumentException("Languages is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateLanguages(languages.get(), account));
    }
    
    @ApiOperation(value = "Set spoken languages for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/languages/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateLanguages(
            @RequestBody final Map<String, Object> body,
            @PathVariable(value = UUID) final String uuid) {

        final Optional<List<String>> languages = Optional.ofNullable((List<String>) body.get("languages"));

        if (!languages.isPresent()) {
            throw new IllegalArgumentException("Languages is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateLanguages(languages.get(), uuid));
    }

    @ApiOperation(value = "Set experience for trainer.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/experience", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateExperience(
            @RequestBody final Map<String, Object> body,
            final Account account) {

        final Optional<String> experience = Optional.ofNullable((String) body.get("experience"));

        if (!experience.isPresent()) {
            throw new IllegalArgumentException("Experience field is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateExperience(experience.get(), account));
    }

    @ApiOperation(value = "Set experience for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/experience/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateExperience(
            @RequestBody final Map<String, Object> body,
            @PathVariable(value = UUID) final String uuid) {

        final Optional<String> experience = Optional.ofNullable((String) body.get("experience"));

        if (!experience.isPresent()) {
            throw new IllegalArgumentException("Experience field is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateExperience(experience.get(), uuid));
    }
    
    @ApiOperation(value = "Set credential for trainer.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/credentials", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateCredentials(
            @RequestBody final Map<String, Object> body,
            final Account account) {

        final String trainerId = (String) body.get("uuid");

        if (StringUtils.isEmpty(trainerId)) {
            throw new IllegalArgumentException("Trainer UUID is required for this action.");
        }

        final Optional<List<String>> credentials = Optional.ofNullable((List<String>) body.get("credentials"));

        if (!credentials.isPresent()) {
            throw new IllegalArgumentException("Credentials is required for this action.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.updateCredentials(credentials.get(), trainerId));
    }
 

    @ApiOperation(value = "Get full profile of user.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/profile/{uuid}/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getFullProfile(
            @PathVariable(value = UUID) final String userId,
            @PathVariable(value = "language") final String language,
            @RequestParam(value = "v", required = false) final ProfileVersion version,
            @RequestParam(value = "fullProfile", required = false) final Boolean fullProfile,
            final Account account) {

		final UserEntity currentUser = userProfileService.getFullProfile(userId, account, language, version,
				fullProfile);

		if (!currentUser.getActivated() && (account.isEu() || account.isPt())) {
			// return error code
			LOG.warn("User {} not activated", userId);
			return new ResponseEntity<>(
					ErrorCode.OPERATOR_NOT_FOUND.withError("PROFILE_IS_DEACTIVATED", "Profile is deactivated"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/mobile/v2/user_profile/{uuid}/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFullProfile_V2(
            @PathVariable(value = UUID) final String userId,
            @PathVariable(value = "language") final String language,
            @RequestParam(value = "v", required = false) final ProfileVersion version,
            @RequestParam(value = "fullProfile", required = false) final Boolean fullProfile,
            final Account account) {
    	
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	apiResponse.setStatus(ErrorCode.SUCCESS);
    	
    	try {
    		List<QuestionPTiMatch> questionsPTIMatch = new ArrayList<QuestionPTiMatch>();
    		UserPTiMatchDTO userPtiMatch = new UserPTiMatchDTO();
    		Map<String, QuestionEntity> questionsMap = new HashMap<String, QuestionEntity>();
    		final UserEntity currentUser = userProfileService.getFullProfile(userId, account, language, version, fullProfile);
    		int ptBookingCredit = 0;
    		
    		if(!currentUser.getActivated()) {
    			LOG.warn("User {} not activated", userId);
    			apiResponse.setStatus(ErrorCode.FAILED.withDetail("User not activated"));
    		} else {
    			List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = questionsPTIService.getQuestions(new QuestionQuery(UserType.PT.toString(), "en"));
    			if(!questions.isEmpty()) {
    				questions.forEach(question -> {
        				questionsMap.put(question.key, question);
        			});
    				questionsPTIMatch = questionsPTIService.getQuestionsPTIMatch(account.ugAccessToken(), currentUser.getUuid(), questionsMap);
    			} else {
    				LOG.info("Questions list is empty");
    			}
    			
    			// Get UserPtiMatach data
    			userPtiMatch = userPtiMatchService.getByEuUuidAndPtUuid(account.uuid(), userId);
    			
    			// Get PT Booking Credit By Level
    			if(!StringUtils.isEmpty(currentUser.getLevel())) {
    				Optional<LevelEntity> optional = levelRepo.findByCode(currentUser.getLevel());
    				if(optional.isPresent()) {
    					LevelEntity entity = optional.get();
    					ptBookingCredit = entity.getPtBookingCredit();
    				}
    			}
    		}
    		
    		Map<String, Object> data = new HashMap<String, Object>();
    		data.put("user_info", currentUser);
    		data.put("questions_ptimatch", questionsPTIMatch);
    		data.put("user_ptimatch", userPtiMatch);
    		data.put("pt_booking_credit", ptBookingCredit);
    		
    		apiResponse.setData(data);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOG.error("[getFullProfile_V2][error: {}]", e.getMessage());
		}
    	
    	return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }
    

    @ApiOperation(value = "update user's profile", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/profile/me/{language}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> updateUserProfile(final Account account,
                                                 @RequestBody final ProfileRequest request) {

    	LOG.info("Update user profile request input: {}", gson.toJson(request));
    	
        final UserEntity currentUser = userProfileService.getCurrentUser(account);
        final Profile profile = currentUser.getProfile();

        final GenderType gender = request.getGender() !=null ? request.getGender() : profile.getGender();
        final String country = request.getCountry() !=null ? request.getCountry() : profile.getCountry();
        final String city = request.getCity() !=null ? request.getCity() : profile.getCity();
        final String birthday = request.getBirthday() !=null ? request.getBirthday() : profile.getBirthday();
        final String phoneNumber = request.getPhoneNumber() !=null ? request.getPhoneNumber() : profile.getPhone();
        final String picture = request.getPicture() !=null ? request.getPicture() : currentUser.getPicture();
        final String name = request.getName() !=null ? request.getName() : currentUser.getName();
        final String usercode = request.getUsercode() !=null ? request.getUsercode() : currentUser.getUsercode();
        final String clubCode = request.getClubcode() !=null ? request.getClubcode() : currentUser.getClubcode();
        final String email = request.getEmail() !=null ? request.getEmail() : currentUser.getEmail();
        final Boolean enableSubscribe = request.getEnableSubscribe() !=null ? request.getEnableSubscribe() : currentUser.getEnableSubscribe();
        final Boolean enablePrivateChat = request.getEnablePrivateChat() != null ? request.getEnablePrivateChat() : currentUser.getEnablePrivateChat();
        final ExtendUserType extUserType = request.getExtUserType() != null ? request.getExtUserType() : currentUser.getExtUserType();
        
        UserStatus userStatus = currentUser.getStatus();
        String level = currentUser.getLevel();
        Long approvedDate = null;
        if(!StringUtils.isEmpty(request.getEmail()) && !request.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
        	final ErrorCode error = userProfileService.checkEmailValid(request.getEmail());
        	if (error.getCode() != 0) {
        		throw new ProfileUpdateException(new IllegalArgumentException(email + " is invalid!"), error);
        	}
        }   
        
        if(!StringUtils.isEmpty(request.getUsercode()) && !request.getUsercode().equalsIgnoreCase(currentUser.getUsercode())) {
        	final ImportUserResult result = userProfileService.checkUserCodeValid(request.getUsercode(), currentUser.getUserType().toString().toLowerCase());
        	if (result.getErrorCode() != 0) {
        		throw new ProfileUpdateException(new IllegalArgumentException(usercode + " is invalid!"), new ErrorCode(result.getErrorCode(),result.getUserCode(),""));
        	}
        	//If PT then auto assign level. Level getting from the detail Error return
            LOG.info(String.format("User code is valid: %s, Level: %s", result.getUserCode(), result.getLevel()));
            if(!StringUtils.isEmpty(result.getLevel())
            		&& StringUtils.isEmpty(level)
            		&& currentUser.getUserType().toString().equalsIgnoreCase(UserType.PT.toString())) {
            	userStatus = UserStatus.builder().documentStatus(DocumentStatusType.APPROVED).build();
            	level = result.getLevel();
            	approvedDate = System.currentTimeMillis();
            }
        } 
        
        final UserEntity userUpdate =
                UserEntity.builder()
                        .from(currentUser)
                        .withName(name)
                        .withPicture(picture)
                        .withUsercode(usercode)
                        .withClubcode(clubCode)
                        .withEmail(email)
                        .withStatus(userStatus)
                        .withLevel(level)
                        .withApprovedDate(approvedDate)
                        .withEnableSubscribe(enableSubscribe)
                        .withEnablePrivateChat(enablePrivateChat)
                        .withExtUserType(extUserType)
                        .withUpdatedProfile(true)
                        .withProfile(Profile.builder().from(profile)
                                .withGender(gender)
                                .withCountry(country)
                                .withCity(city)
                                .withBirthday(birthday)
                                .withPhone(phoneNumber)
                                .build()).build();
        
        return updateUserEntity(account.uuid(), account.type(), userUpdate, SHOULD_SEND_EVENT);
    }
    
    @ApiOperation(value = "update user's club", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/admin/profile/{userId}/club/{clubCode}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public Map<String, Object> updateUserClub(final Account account, 
    										@PathVariable(value = "userId") final String userId,
    										@PathVariable(value = "clubCode") final String clubCode) {
        final UserEntity user =
                userProfileService.getUser(userId);
        if(user == null) {
        	throw new IllegalArgumentException("User is not existing");
        }
        final UserEntity userUpdate =
                UserEntity.builder()
                        .from(user)
                        .withClubcode(clubCode)
                        .build();

        return updateUserEntity(userId, CommonConstant.ADMIN_USER_TYPE, userUpdate, false);
    }

    @ApiOperation(value = "Check association status.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/association", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> checkAssociation(
            @RequestBody final Map<String, Object> users,
            final Account account) {

        if (Objects.isNull(users)) {
            throw new IllegalArgumentException("List of other UUID need to check association with the current user is missing.");
        }

        return userProfileService.checkAssociation(users, account);
    }

    @ApiOperation(value = "Store media types.", tags = PROFILE_TAG_NAME)
    @RequestMapping(value = "/uploadmedia/{type}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> uploadMedia(
            @RequestBody @Valid final List<Media> medias,
            @PathVariable(value = "type") final String type,
            final Account account) {
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("Type of this media is required.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.storeMediaInfo(type, medias, account));
    }
    
    @ApiOperation(value = "Store media types.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/uploadmedia/{type}/{uuid}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> uploadMedia(
            @RequestBody @Valid final List<Media> medias,
            @PathVariable(value = "type") final String type,
            @PathVariable(value = UUID) final String uuid) {
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("Type of this media is required.");
        }

        return Collections.singletonMap(SUCCESS, userProfileService.storeMediaInfo(type, medias, uuid));
    }
    
    @ApiOperation(value = "Update Profile picture.", tags = PROFILE_TAG_NAME)
    @RequiredAdminRole
    @RequestMapping(value = "/uploadPicture/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> uploadProfilePicture(
            @PathVariable(value = UUID) final String uuid,
            @RequestParam(value = PROFILE_URL) final String profileUrl) {

        return Collections.singletonMap(SUCCESS, userProfileService.updateProfilePicture(profileUrl, uuid));
    }
 
	@ApiOperation(value = "Set tagline for trainer.", tags = PROFILE_TAG_NAME)
	@RequestMapping(value = "/bio", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> updateBio(@RequestBody final Map<String, Object> body, final Account account) {

		final Optional<String> bio = Optional.ofNullable((String) body.get("bio"));

		LOG.info("--- >>> --- {} ,", bio);
		if (!bio.isPresent()) {
			throw new IllegalArgumentException("Bio is required for this action.");
		}

		return Collections.singletonMap(SUCCESS, userProfileService.updateBio(bio.get(), account));
	}
	
	private Map<String, Object> updateUserEntity(final String userId, final String accountType, final UserEntity userEntity, final boolean shouldSendEvent){
        if (!userProfileService.updateProfile(userId, userEntity, accountType, shouldSendEvent)) {
        	return Collections.singletonMap(SUCCESS, Boolean.FALSE);
        }
        return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}
 
	@RequestMapping(value = "/mobile/v1/me/clubs", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateClubsOfOwner(final Account account, @RequestBody final List<ClubEntity> clubs) {
		LOG.info("[updateClubsOfOwner] Account login with user uuid [{}] and type [{}]", account.uuid(), account.type());
		LOG.info("[updateClubsOfOwner] List of clubs input: {}", gson.toJson(clubs));
		
		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		if(account.isClub()) {
			boolean result = userProfileService.addClubsOfOwner(account.uuid(), clubs);
			if(result == false) {
				apiResp.setStatus(ErrorCode.FAILED.withDetail("Something wrong on updating clubs of owner"));
			}
		} else {
			apiResp.setStatus(ErrorCode.FAILED.withDetail("Do not support for user type " + account.type()));
		}
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/users/{user_uuid}/clubs", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getClubsFromOwner(@PathVariable(name = "user_uuid")final String userUuid, final Account account) {
		LOG.info("[getClubsFromOwner] Logged request data with user uuid [{}]", userUuid);
		
		ApiRespListObject<ClubEntity> apiResp = new ApiRespListObject<ClubEntity>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		
		List<ClubEntity> clubs = userProfileService.getClubsFromOwner(userUuid);
		apiResp.setData(clubs);
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/me/club_owner_info", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateInfoOfClubOwner(@Valid @RequestBody ClubOwnerInfoRequest request, final Account account) {
		LOG.info("[updateInfoOfClubOwner] Logged request data: {}", gson.toJson(request));
		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);

		try {
			userProfileService.updateClubOwnerInfo(request.getClubAmenities(), request.getClubOpeningHours(), account.uuid());
		} catch (Exception e) {
			apiResp.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/me/check/pti-matched", method = RequestMethod.GET)
	public ResponseEntity<Object> checkPtiMatched(final Account account) {
		LOG.info("[checkPtiMatched] Logged request data: {}", account.uuid());
		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);

		try {
			UserEntity userEntity = userProfileService.getUser(account.uuid());
			if (userEntity.getOnBoardCompleted()) {
				apiResp.setData(userEntity.getOnBoardCompleted());
			} else {
				Boolean isExisted = userPtiMatchService.isUserPtiAvailable(account.uuid());
				apiResp.setData(isExisted);
			}
		} catch (Exception e) {
			apiResp.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/mobile/v1/search_tracking/user/{uuid}/top_latest", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTopRecentSearch(@PathVariable(name = "uuid") final String userUuid, final Account account) {
		ApiRespListObject<SearchTrackingEntity> apiResponse = new ApiRespListObject<SearchTrackingEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<SearchTrackingEntity> entities = searchTrackingService.getTopLatestSearch(userUuid, RECENT_SEARCH_LIMIT);
			apiResponse.setData(entities);
		} catch (Exception e) {
			LOG.error("[getTopRecentSearch] Error with detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/search_tracking", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> trackingUserSearch(@RequestBody final SearchTrackingRequest searchTrackingRequest, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		LOG.info("[trackingUserSearch] Logged request data: {}", gson.toJson(searchTrackingRequest));
		
		try {
			apiResponse.setData(searchTrackingService.updateTrackingSearch(searchTrackingRequest, account.uuid()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
