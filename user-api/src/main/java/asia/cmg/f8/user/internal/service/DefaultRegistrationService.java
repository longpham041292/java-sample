package asia.cmg.f8.user.internal.service;



import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.esotericsoftware.minlog.Log;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.profile.SignUpUserEvent;
import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.user.UserActivationEvent;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.client.SessionClient;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.client.UserProfileClient;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.entity.UserStatus;
import asia.cmg.f8.user.event.EventHandler;
import asia.cmg.f8.user.exception.UserRegistrationException;
import asia.cmg.f8.user.repository.AccountTokenRepository;
import asia.cmg.f8.user.service.JwtService;
import asia.cmg.f8.user.service.RegistrationService;
import asia.cmg.f8.user.service.UserManagementService;

/**
 * Created on 2/17/17.
 */
@Service
public class DefaultRegistrationService implements RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRegistrationService.class);

    private static final ErrorCode ERROR_ON_CREATE_USER = new ErrorCode(4002, "ERROR_ON_CREATE_USER", "Error when create new user");
    private static final String USERNAME = "username";
    private static final String USERCODE = "usercode";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String EMAIL = "email";
    private static final String FACEBOOK_ID = "fbId";
    public static final String REQUEST_DATA_IS_INVALID = "REQUEST_DATA_IS_INVALID";
    public static final int ACTIVATED = 1;
    public static final int NOTACTIVATED = 0;
    private static final int USER_CODE_VALID = 0;
    private final UserClient userClient;
    private final SessionClient sessionClient;

    @Autowired
    private UserProfileClient userProfileClient;
    
    @Autowired
    private UserProperties userProperties;

    @Autowired
    private JwtService jwtService;
    
	@Autowired
	private AccountTokenRepository accountTokenRepo;
	  
    @Value( "${jwt.access_expiresIn}" )
    private int accessExpiresIn;
	  
    @Value( "${jwt.refresh_expiresIn}" )
	private int refreshExpiresIn;
    
    private final EventHandler eventHandler;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final UserManagementService managementService;

    @Inject
    public DefaultRegistrationService(final UserClient userClient,
    		final SessionClient sessionClient,
    		final UserManagementService managementService, final EventHandler eventHandler) {
        this.userClient = userClient;
        this.sessionClient = sessionClient;
        this.eventHandler = eventHandler;
        this.managementService = managementService;
    }

    @Override
    public Map<String, Object> register(@Valid UserEntity user) {
    	try {
    		Map<String, Object> map = new HashMap<String, Object>();
        	UserGridResponse<UserEntity> response = null;
        	synchronized(this) {
        		if(!StringUtils.isEmpty(user.getUsercode()) && managementService.isUserCodeExisted(user.getUsercode())) {
        			throw new UserRegistrationException(new IllegalArgumentException(USERCODE + " is existed!"), ErrorCode.DUPLICATE_USER_CODE);
        		}
        		
        		if(!StringUtils.isEmpty(user.getUsercode())) {
                	//Check userCode is valid,
        			final ImportUserResult validUserCode = sessionClient.isValidUserCodeInMigrationDB(user.getUsercode(), user.getUserType().toString().toLowerCase());
                    if(USER_CODE_VALID != validUserCode.getErrorCode()) {
                    	throw new UserRegistrationException(new IllegalArgumentException(USERCODE + " is invalid!"), new ErrorCode(validUserCode.getErrorCode(), validUserCode.getUserCode(), ""));
                    }
                    //Do not need to onboard if user already existed in import list
                    if(!StringUtils.isEmpty(validUserCode.getUserCode())) {
	                    user = UserEntity.builder().from(user)
	                			.onBoardCompleted(true).build();
                    }
                    //If PT then auto assign level. Level getting from the detail Error return
                    LOGGER.info(String.format("User code is valid: %s, Level: %s", validUserCode.getUserCode(), validUserCode.getLevel()));
                    if(!StringUtils.isEmpty(validUserCode.getLevel()) && user.getUserType().toString().equalsIgnoreCase(UserType.PT.toString())) {
                    	final UserStatus userStatus = UserStatus.builder().documentStatus(DocumentStatusType.APPROVED).build();
                    	user = UserEntity.builder().from(user)
                    			.status(userStatus)
                    			.level(validUserCode.getLevel())
                    			.approvedDate(System.currentTimeMillis()).build();
                    }
                }
        		response = userClient.createUser(user);
                if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
                    throw new UserRegistrationException(ERROR_ON_CREATE_USER);
                }
        	}
        	
            final UserEntity userEntity = response.getEntities().iterator().next();
            
        	//add user to default group
            LOGGER.info("Add user {} default group {}", userEntity.getUuid(), userProperties.getDefaultGroupId());
        	userClient.addUserToGroup(userEntity.getUuid(), userProperties.getDefaultGroupId());
        	switch (userEntity.getUserType()) {
			case EU:
				userClient.addUserToGroup(userEntity.getUuid(), userProperties.getEuGroupId());
				break;
			case PT:
				userClient.addUserToGroup(userEntity.getUuid(), userProperties.getPtGroupId());
				break;
			case CLUB:
				userClient.addUserToGroup(userEntity.getUuid(), userProperties.getClubGroupId());
				break;
			default:
				break;
			}
        	
            //Insert user first
            this.insertRecordToSessionUser(user, userEntity);

            String userType = null;
            if (userEntity.getUserType() != null) {
                userType = userEntity.getUserType().toString();
            }
            this.followingUser(userEntity, userType);

            LOGGER.info("Fired user sign up event for user {}", userEntity.getEmail());
            
            // Saving account token info
    		long expiredAt = System.currentTimeMillis() + accessExpiresIn;
    		long refreshExpiredAt = System.currentTimeMillis() + refreshExpiresIn;
            String accessToken = jwtService.generateToken(userEntity.getUuid(), ACCESS_TOKEN);
            String refreshToken = jwtService.generateToken(userEntity.getUuid(), REFRESH_TOKEN);
            AccountTokenEntity token = new AccountTokenEntity(userEntity.getUuid(), accessToken, refreshToken, expiredAt, refreshExpiredAt);
            if(user.getPassword() != null) {
            	token.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            token = accountTokenRepo.save(token);
            LOGGER.info("Saved token id {} of user {}", token.getId(), userEntity.getUuid());
            
            // Return the map response
            map.put("access_token", accessToken);
            map.put("refresh_token", refreshToken);
            map.put("user", userEntity);
            return map;
            
        } catch (final UserGridException exception) {
            LOGGER.error("Error on signup {}", exception.toString());
            LOGGER.error("Error on signup detail message {}", exception.getMessage());
            String detail = exception.getMessage();
            ErrorCode errorCode = ErrorCode.REQUEST_INVALID.withError(exception.getError(), detail);
            if (exception.getMessage().contains(EMAIL)) {
                detail = EMAIL;
                errorCode = new ErrorCode(4013, REQUEST_DATA_IS_INVALID, detail);
            } else if (exception.getMessage().contains(USERNAME)) {
                detail = USERNAME;
                errorCode = new ErrorCode(4014, REQUEST_DATA_IS_INVALID, detail);
            } else if (exception.getMessage().contains(FACEBOOK_ID)){
                errorCode = new ErrorCode(4015, REQUEST_DATA_IS_INVALID, detail);
            }

            throw new UserRegistrationException(exception, errorCode);
        }
    }

    private void followingUser(final UserEntity userEntity, final String userType) {
        final SignUpUserEvent signUpUserEvent = SignUpUserEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSubmittedAt(System.currentTimeMillis())
                .setUserId(userEntity.getUuid())
                .setUserType(userType)
                .setName(userEntity.getName())
                .setEmail(userEntity.getEmail())
                .setPicture(userEntity.getPicture())
                .setLanguage(userEntity.getLanguage())
                .setActivated(userEntity.getActivated())
                .build();

        eventHandler.publish(signUpUserEvent);
    }

    private void insertRecordToSessionUser(final UserEntity user,final UserEntity userEntity) {
        if(user.getActivated()) {
            final UserEntity newUser = UserEntity.builder().activated(user.getActivated()).build();
            final UserGridResponse<UserEntity> entityUserGridResponse =  userProfileClient.activateUser(userEntity.getUuid(), newUser);
            if (entityUserGridResponse == null || entityUserGridResponse.getEntities() == null || entityUserGridResponse.getEntities().isEmpty()) {
                throw new UserRegistrationException(ERROR_ON_CREATE_USER);
            }
            eventHandler.publish(UserActivationEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString())
                    .setSubmittedAt(System.currentTimeMillis())
                    .setUuid(userEntity.getUuid())
                    .setName(userEntity.getName())
                    .setEmail(userEntity.getEmail())
                    .setAvarta(userEntity.getPicture())
                    .setUserName(userEntity.getUsername())
                    .setUserType(userEntity.getUserType().name().toLowerCase(Locale.US))
                    .setJoinedDate(userEntity.getCreated())
                    .setActivated(ACTIVATED)
                    .setEmailValidated(NOTACTIVATED)
                    .setClubCode((null == userEntity.getClubcode()) ? "" : userEntity.getClubcode())
                    .setUserCode((null == userEntity.getUsercode()) ? "" : userEntity.getUsercode())
                    .setDocumentStatus(null == userEntity.getStatus() || null == userEntity.getStatus().documentStatus() ? DocumentStatusType.ONBOARD.toString() : userEntity.getStatus().documentStatus().toString())
                    .setLevel(userEntity.getLevel())
                    .setPhone(userEntity.getProfile() == null ? null : userEntity.getProfile().getPhone())
                    .setGender(userEntity.getProfile() == null ? GenderType.MALE.ordinal() : userEntity.getProfile().getGender().ordinal())
                    .build());
            LOGGER.info(String.format("Fire event for changing User Info: %s", userEntity.getUuid()));
        }
        else {
        	eventHandler.publish(UserActivationEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString())
                    .setSubmittedAt(System.currentTimeMillis())
                    .setUuid(userEntity.getUuid())
                    .setName(userEntity.getName())
                    .setEmail(userEntity.getEmail())
                    .setAvarta(userEntity.getPicture())
                    .setUserName(userEntity.getUsername())
                    .setUserType(userEntity.getUserType().name().toLowerCase(Locale.US))
                    .setJoinedDate(userEntity.getCreated())
                    .setUserCode((null == userEntity.getUsercode()) ? "" : userEntity.getUsercode())
                    .setActivated(NOTACTIVATED)
                    .setEmailValidated(NOTACTIVATED)
                    .setClubCode((null == userEntity.getClubcode()) ? "" : userEntity.getClubcode())
                    .setDocumentStatus(null == userEntity.getStatus() || null == userEntity.getStatus().documentStatus() ? DocumentStatusType.ONBOARD.toString() : userEntity.getStatus().documentStatus().toString())
                    .setLevel(userEntity.getLevel())
                    .setPhone(userEntity.getProfile() == null ? null : userEntity.getProfile().getPhone())
                    .setGender(userEntity.getProfile() == null ? GenderType.MALE.ordinal() : userEntity.getProfile().getGender().ordinal())
                    .build());
        }
    }

	@Override
	public Map<String, Object> handleExistAuthProviderID(UserEntity userEntity) {
		Map<String, Object> map = new HashMap<String, Object>();
		 // Saving account token info
		long expiredAt = System.currentTimeMillis() + accessExpiresIn;
		long refreshExpiredAt = System.currentTimeMillis() + refreshExpiresIn;
        String accessToken = jwtService.generateToken(userEntity.getUuid(), ACCESS_TOKEN);
        String refreshToken = jwtService.generateToken(userEntity.getUuid(), REFRESH_TOKEN);
        AccountTokenEntity token = accountTokenRepo.findByUUID(userEntity.getUuid());
        if(token != null) {
        	token.setAccessToken(accessToken);
        	token.setExpiredAt(expiredAt);
        	token.setRefreshToken(refreshToken);
        	token.setRefreshExpiredAt(refreshExpiredAt);
        } else {
        	token = new AccountTokenEntity(userEntity.getUuid(), accessToken, refreshToken, expiredAt, refreshExpiredAt);        	
        }
        if(userEntity.getPassword() != null) {
        	token.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }
        
        accountTokenRepo.save(token);
        LOGGER.info("Saved token id {} of user {}", token.getId(), userEntity.getUuid());
        
        // Return the map response
        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("user", userEntity);
        return map;
	}
}
