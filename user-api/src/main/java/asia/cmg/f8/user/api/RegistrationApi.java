package asia.cmg.f8.user.api;

import static asia.cmg.f8.common.spec.user.DocumentStatusType.ONBOARD;
import static asia.cmg.f8.common.web.errorcode.ErrorCode.REQUEST_INVALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.client.FacebookUserInfoClient;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.domain.client.AppleUserInfoAPI;
import asia.cmg.f8.user.domain.client.FacebookUserInfoApi;
import asia.cmg.f8.user.domain.client.UserSignup;
import asia.cmg.f8.user.domain.client.UserSignup.Apple;
import asia.cmg.f8.user.dto.AppleUserPayloadDTO;
import asia.cmg.f8.user.dto.AppleVerifyResponse;
import asia.cmg.f8.user.dto.FbUserInfo;
import asia.cmg.f8.user.entity.BasicUserEntity;
import asia.cmg.f8.user.entity.Facebook;
import asia.cmg.f8.user.entity.Profile;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.entity.UserStatus;
import asia.cmg.f8.user.repository.BasicUserRepository;
import asia.cmg.f8.user.service.RegistrationService;

/**
 * Created by on 10/25/16.
 */
@RestController
public class RegistrationApi {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationApi.class);

    private final FacebookUserInfoClient facebookUserInfoClient;
    private final RegistrationService registrationService;
    private final UserProperties userProperties;
    
    @Autowired
    private FacebookUserInfoApi facebookUserInfoApi;

	@Autowired
	private AppleUserInfoAPI appleUserInfoAPI;
	
	@Autowired
	private BasicUserRepository basicUserRepo;
	
	@Autowired
	private UserClient userClient;
	
	@Value("${security.apple.client_id}")
	private String APPLE_CLIENT_ID;
	@Value("${security.apple.client_secret_key}")
	private String APPLE_CLIENT_SECRET;
	

    
    public RegistrationApi(final FacebookUserInfoClient facebookUserInfoClient,
                           final RegistrationService registrationService,
                           final UserProperties userProperties) {
        this.facebookUserInfoClient = facebookUserInfoClient;
        this.registrationService = registrationService;
        this.userProperties = userProperties;
    }

    /**
     * Signup new user using email/pass.
     *
     * @return the created user.
     */
    @RequestMapping(value = "/signup", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(final @Valid @RequestBody UserSignup user, final BindingResult bindingResults) {

    	if (bindingResults.hasErrors()) {
            LOG.error("Error when validate signup data {}", bindingResults.getAllErrors());
            return new ResponseEntity<Object>(REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }
        return doRegister(user);
    }    
    /**
     * Handle Sign up v3 with password, facebook, apple
     * @author phong
     * @param user
     * @param bindingResults
     * @return
     */
    @RequestMapping(value = "/users/v1/signup", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerV1(final @RequestBody UserSignup user, final BindingResult bindingResults) {

    	if (bindingResults.hasErrors()) {
            LOG.error("Error when validate signup data {}", bindingResults.getAllErrors());
            return new ResponseEntity<Object>(REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }
        return doRegisterV1(user);
    }
    

    private ResponseEntity doRegister(final UserSignup user) {
        final UserEntity userEntity = buildUserEntity(user);
        return new ResponseEntity<>(registrationService.register(userEntity), HttpStatus.OK);
    }

    private ResponseEntity doRegisterV1(final UserSignup user) {
    	UserEntity userEntity = null;
    	long currentTime = System.currentTimeMillis();
    	user.setUsername(Long.toString(currentTime));
    	Optional<BasicUserEntity> basicUserInfo = null;
    	
    	switch (user.getGrantType()) {
		case PASSWORD:
			user.setUpdatedProfile(true);
			user.setEmailValidate(false);
			userEntity = buildUserEntity(user);
			return new ResponseEntity<>(registrationService.register(userEntity), HttpStatus.OK);
		case FACEBOOK:
			FbUserInfo fbDto = null;
			try {
				fbDto = facebookUserInfoApi.getUserInfo(user.getFacebook().getAccessToken());
			} catch (Exception e) {
				LOG.error("Invalid facebook token", user.getFacebook().getAccessToken());
				return new ResponseEntity<>(new ErrorCode(4011, "INVALID_FACEBOOK_TOKEN", "Invalid facebook token"), BAD_REQUEST);
			}
			
//			basicUserInfo = basicUserRepo.findByEmail(fbDto.getEmail());
//			if(basicUserInfo.isPresent()) {
//				LOG.info("Email of FB account already existed on database: {}", fbDto.getEmail());
//				return new ResponseEntity<>(new ErrorCode(9002, "EMAIL_EXISTED", "Email existed"), BAD_REQUEST);
//			}
			userEntity =  this.getResponseUserByProviderId(fbDto.getId());
			if(userEntity != null) {
				return new ResponseEntity<>(registrationService.handleExistAuthProviderID(userEntity), HttpStatus.OK);
			}
			
			user.setName(fbDto.getName());
			user.setAuthProviderId(fbDto.getId());
			user.setUpdatedProfile(false);
			user.setEmail("fb_"+ currentTime + "@facebook.com");
			user.setLanguage("en");
			user.setEmailValidate(true);
			
			userEntity = buildUserEntity(user);
			return new ResponseEntity<>(registrationService.register(userEntity), HttpStatus.OK);
		case APPLE:
			if(user.getApple().getRefreshToken() == null || user.getApple().getRefreshToken() == "") {
				LOG.info("Apple refresh token is empty" );
				return new ResponseEntity<>(new ErrorCode(4011, "INVALID_APPLE_REFRESH_TOKEN", "Apple refresh token is required."), BAD_REQUEST);
			}
			AppleUserPayloadDTO dto = this.appleValidateToken(user.getApple().getRefreshToken());
			if(dto == null) {
				LOG.error("Invalid Apple refresh token", user.getApple().getCode());
				return new ResponseEntity<>(new ErrorCode(4011, "INVALID_APPLE_REFRESH_TOKEN", "Invalid Apple refresh token"), BAD_REQUEST);				
			}
			
//			basicUserInfo = basicUserRepo.findByEmail(dto.getEmail());
//			if(basicUserInfo.isPresent()) {
//				LOG.info("Email of Apple account already existed on database: {}", dto.getEmail());
//				return new ResponseEntity<>(new ErrorCode(9002, "EMAIL_EXISTED", "Email existed"), BAD_REQUEST);
//			}
			userEntity =  this.getResponseUserByProviderId(user.getApple().getId());
			if(userEntity != null) {
				return new ResponseEntity<>(registrationService.handleExistAuthProviderID(userEntity), HttpStatus.OK);
			}
			
			user.setName(user.getApple().getName());
			user.setAuthProviderId(user.getApple().getId());
			user.setUpdatedProfile(false);
			user.setEmail("apple_" + currentTime + "@apple.com");
			user.setLanguage("en");
			user.setEmailValidate(true);

			userEntity = buildUserEntity(user);
			return new ResponseEntity<>(registrationService.register(userEntity), HttpStatus.OK);
		default:
			break;
		}
        
    	return new ResponseEntity<Object>(REQUEST_INVALID, HttpStatus.BAD_REQUEST);
    }



    /**
     * Signup an user with facebook account.
     *
     * @param userEntity the user entity
     * @param request    the current request
     * @return new registered client. Return a {@link ErrorCode} if the facebook token is invalid.
     */
    @RequestMapping(value = "/signup/facebook", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerFacebook(final @Valid @RequestBody UserSignup userEntity, final BindingResult bindingResults, final HttpServletRequest request) {

        if (bindingResults.hasErrors()) {
            LOG.error("Error when validate signup data {}", bindingResults.getAllErrors());
            return new ResponseEntity<>(REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }

        // verify the token with facebook
        final UserSignup.Facebook facebook = userEntity.getFacebook();

        String accessToken = null;
        if (facebook != null) {
            accessToken = facebook.getAccessToken();
        }

        if (accessToken != null) {

            final FacebookUserInfoClient.FbUserInfo userInfo = facebookUserInfoClient.getUserInfo(accessToken);
            if (userInfo != null) {

                facebook.setAccessToken(null);
                facebook.setId(userInfo.getId());
                return doRegister(userEntity);
            }
        }
        return new ResponseEntity<>(new ErrorCode(9000, "INVALID_FACEBOOK_TOKEN", "Invalid facebook token"), BAD_REQUEST);
    }
    
    private UserEntity buildUserEntity(@Valid final UserSignup user) {
        final Profile profile = Profile.builder()
                .withBirthday(user.getBirthday())
                .withGender(user.getGender())
                .withPhone(user.getPhone())
                .withCountry(user.getCountry())
                .withCity(user.getCity())
                .build();

        final Facebook facebook;
        final Apple apple;
        if (user.getFacebook() != null) {
            facebook = Facebook.builder().id(user.getFacebook().getId()).build();
        } else {
            facebook = Facebook.builder().build(); // create empty
        }
        

        return UserEntity.builder()
                .status(UserStatus.builder().documentStatus(ONBOARD).build())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .usercode(user.getUsercode())
                .clubcode(user.getClubcode())
                .password(user.getPassword())
                .picture(StringUtils.isBlank(user.getPicture()) ? userProperties.getDefaultProfilePicture() : user.getPicture())
                .language(user.getLanguage())
                .userType(user.getUserType())
                .country(user.getCountry())
                .profile(profile)
                .activated(true)
                .emailvalidated(user.getEmailValidate())
                .confirmed(true)
                .lastTimeLoadNotification(System.currentTimeMillis())
                .updatedProfile(user.getUpdatedProfile())
                .authProviderId(user.getAuthProviderId())
                .grantType(user.getGrantType())
                .build();
    }
    
	public AppleUserPayloadDTO appleGenerateToken(String appleCode) {
		try {
			LOG.info("Start validate Apple Code {} ", appleCode);
			AppleVerifyResponse responseData = appleUserInfoAPI.authAppleID("authorization_code", APPLE_CLIENT_ID,
					APPLE_CLIENT_SECRET, appleCode, "" // empty refresh token
			);

			String idTokenHash = responseData.getIdToken();

			String[] idToken = idTokenHash.split("\\.");
			String tokenPayload = idToken[1];
			String jsonResponse = new String(Base64.getDecoder().decode(tokenPayload.getBytes()));
			Gson gson = new Gson();
			return gson.fromJson(jsonResponse, AppleUserPayloadDTO.class);

		} catch (Exception e) {
			LOG.info("Invalid Apple Code {} or user is deactivated, {}", appleCode, e);
			return null;
		}
	}
	
	public AppleUserPayloadDTO appleValidateToken(String appleRefreshToken) {
		try {

			AppleVerifyResponse responseData = appleUserInfoAPI.authAppleID("refresh_token", APPLE_CLIENT_ID,
					APPLE_CLIENT_SECRET, "", // empty code
					appleRefreshToken);
			String idTokenHash = responseData.getIdToken();
			String[] idToken = idTokenHash.split("\\.");
			String tokenPayload = idToken[1];
			String jsonResponse = new String(Base64.getDecoder().decode(tokenPayload.getBytes()));
			Gson gson = new Gson();
			return gson.fromJson(jsonResponse, AppleUserPayloadDTO.class);
		} catch (Exception e) {
			LOG.info("Invalid Apple refresh token or user is deactivated, " + e);
			return null;
		}
	}

	
	private UserEntity getResponseUserByProviderId(final String providerID) {
		final UserGridResponse<UserEntity> response = userClient
				.getUserByQuery("where auth_provider_id='" + providerID + "' or fbId = '"+ providerID +"'");
		if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
			return null;
		}
		return response.getEntities().iterator().next();
	}

}
