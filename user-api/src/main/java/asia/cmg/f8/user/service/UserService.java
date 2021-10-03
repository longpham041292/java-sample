package asia.cmg.f8.user.service;


import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.user.UserActivationEvent;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.client.ChangeUsernamePasswordClient;
import asia.cmg.f8.user.client.SessionClient;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.client.UserConfirmClient;
import asia.cmg.f8.user.client.UserProfileClient;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.domain.client.AdminChangeNameRequest;
import asia.cmg.f8.user.domain.client.AdminChangeUserCodeRequest;
import asia.cmg.f8.user.domain.client.AdminChangeUserNameRequest;
import asia.cmg.f8.user.dto.ChangeExtendUserTypeRequest;
import asia.cmg.f8.user.dto.ResetUserPassword;
import asia.cmg.f8.user.dto.VerifyPhoneNumberDTO;
import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.entity.AdminActivateRequest;
import asia.cmg.f8.user.entity.BasicUserEntity;
import asia.cmg.f8.user.entity.ChangePassword;
import asia.cmg.f8.user.entity.ChangePasswordResponse;
import asia.cmg.f8.user.entity.ChangeUsername;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.entity.UserStatus;
import asia.cmg.f8.user.event.EventHandler;
import asia.cmg.f8.user.internal.service.DefaultRegistrationService;
import asia.cmg.f8.user.notification.dto.UsergridChangeNameApi;
import asia.cmg.f8.user.repository.BasicUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.esotericsoftware.minlog.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created on 11/22/16.
 */
@Component
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);


    private static final String QUERY_USER_EXIST_BY_UUID = "select * where uuid = '%s'";
    public static final int ACTIVATED = 1;
    public static final int NOTACTIVATED = 0;

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private UserProfileClient userProfileClient;
    
    @Autowired
    private UserClient userClient;

    @Autowired
    private UserConfirmClient userConfirmClient;

    @Autowired
    private ChangeUsernamePasswordClient changeUsernamePasswordClient;

    @Autowired
    private UserProperties userProps;
    
    @Autowired
    DefaultRegistrationService defaultRegistrationService;
    
    @Autowired
    SessionClient sessionClient;
    
    @Autowired
    private BasicUserRepository userRepo; 

    public boolean activateUser(final AdminActivateRequest request) {
        final String userId = request.getUserId();
        final Optional<UserEntity> userResp = userProfileClient.queryUserByUuid(
                String.format(QUERY_USER_EXIST_BY_UUID, userId)).getEntities().stream().findFirst();

        if (!userResp.isPresent()) {
            LOG.info("Could not found user uuid: " + userId);
            return false;
        }

        final UserEntity newUser = UserEntity.builder().activated(request.isActive()).build();
        if (!userProfileClient.activateUser(userId, newUser).getEntities().stream().findFirst().isPresent()) {
            LOG.info("Something went wrong while update activate status of user uuid: " + userId);
            return false;
        }

        final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setUserId(userId)
                .setUserType(userResp.get().getUserType().toString())
                .setActivate(String.valueOf(request.isActive())) // we use string instead of boolean because we need support the case of NULL value
                .setSubmittedAt(System.currentTimeMillis())
                .build();
        eventHandler.publish(changeUserInfoEvent);

        LOG.info(String.format("Fire event for changing User Info: %s", userId));

        return true;
    }

    public boolean userConfirmRegistration(final String uuid, final String token) throws IOException {

    	try {
    		LOG.info("Begin activating user {}", uuid);
	        final String response = userConfirmClient.confirmActivateUser(uuid, token);
	        /**
	         * Because user-grid does not support to return JSON content so we temporary parse returned HTML content to
	         * check if user's activated or not.
	         */
	        LOG.info("Activated for user with respons {}", response);
	        return handleAfterActivatedResponse(response, uuid);
    	} catch(Exception ex) {
    		LOG.info("Failed to activate user {} with excpetion message {} and root cause {}: ", uuid, ex.getMessage(), ex.getCause());
    		try {
    			//Some times using Feign call activate user is failed. Try manually curl connect to activate user
    			final String response = manuallyConnect(userProps.getActivatedUserUrl() + "/users/" + uuid + "/confirm?token="+ token);
    			return handleAfterActivatedResponse(response, uuid);
    		} catch(Exception e) {
    			 LOG.info("Failed to confirm user {}", uuid);
    			 return false;
    		}
    		
    	}
    }

    public boolean userConfirmEmail(final String uuid) {
        final Optional<UserEntity> userResp = userProfileClient.queryUserByUuid(
                String.format(QUERY_USER_EXIST_BY_UUID, uuid)).getEntities().stream().findFirst();

        if (!userResp.isPresent()) {
            LOG.info("Could not found user uuid: " + uuid);
            return false;
        }

        final UserEntity newUser = UserEntity.builder()
                .emailvalidated(true)
                .build();
        final List<UserEntity> result = userProfileClient.confirmEmail(uuid, newUser).getEntities();

        if (result.isEmpty()) {
            LOG.info("Could not change emailvalidated for user uuid: " + uuid);
            return false;
        }

        LOG.info("User {} is confirmed email successful. Event UserActivationEvent is fired", uuid);

        eventHandler.publish(UserActivationEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSubmittedAt(System.currentTimeMillis())
                .setUuid(userResp.get().getUuid())
                .setName(userResp.get().getName())
                .setEmail(userResp.get().getEmail())
                .setAvarta(userResp.get().getPicture())
                .setUserName(userResp.get().getUsername())
                .setUserType(userResp.get().getUserType().name().toLowerCase(Locale.US))
                .setJoinedDate(userResp.get().getCreated())
                .setActivated(ACTIVATED)
                .setEmailValidated(ACTIVATED)
                .setClubCode((null == userResp.get().getClubcode()) ? "" : userResp.get().getClubcode())
                .setUserCode((null == userResp.get().getUsercode()) ? "" : userResp.get().getUsercode())
                .setGender(userResp.get().getProfile() == null ? GenderType.FEMAIL.ordinal() : userResp.get().getProfile().getGender().ordinal())
                .build());

        return true;
    }
    
    private boolean handleAfterActivatedResponse (final String response, final String uuid) {
    	if (response.contains(userProps.getActiveSuccessText())) {
    		
            eventHandler.publish(UserActivationEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString())
                    .setSubmittedAt(System.currentTimeMillis())
                    .setUuid(uuid)
                    .setName("")
                    .setEmail("")
                    .setAvarta("")
                    .setUserName("")
                    .setUserType("")
                    .setJoinedDate(-1)
                    .setActivated(ACTIVATED)
                    .build());
            

            LOG.info("User {} is confirmed successful. Event UserActivationEvent is fired", uuid);
            return true;
    	}
    	return false;
    }
    private String manuallyConnect(final String urlToRead) throws IOException {
    	LOG.info("Mannualy invoke the url: {} to activate user", urlToRead);
    	final StringBuilder result = new StringBuilder();
    	final URL url = new URL(urlToRead);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
           result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }

    public boolean changePassword(final String uuid, final ChangePassword request, final String token) {
    
        final UserGridResponse<ChangePasswordResponse> res = changeUsernamePasswordClient.changePassword(uuid, request);
        if (Objects.isNull(res)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public boolean resetUserPassword(final String uuid, final ResetUserPassword request) {
        
        final UserGridResponse<ChangePasswordResponse> res = changeUsernamePasswordClient.resetUserPassword(uuid, request);
        if (Objects.isNull(res)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public boolean changeUsername(final String uuid,
                                  final ChangeUsername request,
                                  final Account account) {
        final UserGridResponse<UserEntity> res =
                changeUsernamePasswordClient.changeUsername(uuid, request);
        if (Objects.isNull(res) || res.getEntities().isEmpty()) {
            return Boolean.FALSE;
        }
        final ChangeUserInfoEvent event = ChangeUserInfoEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setUserId(account.uuid())
                .setUserType(account.type())
                .setSubmittedAt(System.currentTimeMillis())
                .setNewUsername(request.getNewUsername())
                .build();
        eventHandler.publish(event);
        LOG.info("Fired event change username ");
        return Boolean.TRUE;
    }

	public boolean adminChangeUsername(final AdminChangeUserNameRequest request, final String accessToken) {
		
		final ChangeUsername changeUserNameRequest = ChangeUsername.builder()
				.newUsername(request.getNewUsername()).build();
		final UserGridResponse<UserEntity> res = changeUsernamePasswordClient.changeUsername(request.getUuid(), changeUserNameRequest);
		if (Objects.isNull(res) || res.getEntities().isEmpty()) {
			return Boolean.FALSE;
		}
		final ChangeUserInfoEvent event = ChangeUserInfoEvent.newBuilder()
				.setEventId(UUID.randomUUID().toString())
				.setUserId(request.getUuid())
				.setUserType(res.getEntities().get(0).getUserType().toString().toLowerCase())
				.setSubmittedAt(System.currentTimeMillis())
				.setNewUsername(request.getNewUsername()).build();
		eventHandler.publish(event);
		LOG.info("Fired event change username ");
		return Boolean.TRUE;
	}
    
	public boolean adminChangeName(final AdminChangeNameRequest request, final String accessToken) {
		
		UsergridChangeNameApi data = new UsergridChangeNameApi();
		data.setName(request.getName());
		
		final UserGridResponse<UserEntity> res = changeUsernamePasswordClient.changeName(request.getUuid(), data);
		if (Objects.isNull(res) || res.getEntities().isEmpty()) {
			return Boolean.FALSE;
		}
		
		try{
			final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
	                .setEventId(java.util.UUID.randomUUID().toString())
	                .setUserId(request.getUuid())
	                .setSubmittedAt(System.currentTimeMillis())
	                .setUserType(res.getEntities().get(0).getUserType().toString().toLowerCase())
//	                .setFullName(request.getName())
	                .build();

	        LOG.info(String.format("Fire event for changing User Info: %s", request.getUuid()));
	        eventHandler.publish(changeUserInfoEvent);
		}
		catch(Exception e){
			Log.info(e.toString());	
		}
		
		LOG.info("Fired event change user full name");
		
		return Boolean.TRUE;
	}
	
	public boolean adminChangeUserCode(final AdminChangeUserCodeRequest request, final String accessToken, final String userType, final String oldUserLevel, final String newUserLevel) {
		UserEntity user = UserEntity.builder()
			    .usercode(request.getNewUserCode())
			    .build();
		if(!StringUtils.isEmpty(newUserLevel) && !newUserLevel.equalsIgnoreCase(oldUserLevel)) {
			final UserStatus userStatus = UserStatus.builder().documentStatus(DocumentStatusType.APPROVED).build();
			user = UserEntity.builder().from(user)
        			.status(userStatus)
        			.level(newUserLevel)
        			.approvedDate(System.currentTimeMillis()).build();
		}
		final UserGridResponse<UserEntity> res = changeUsernamePasswordClient.changeUserCode(request.getUuid(), user);
		if (Objects.isNull(res) || res.getEntities().isEmpty()) {
			return Boolean.FALSE;
		}

		try{
			final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
	                .setEventId(java.util.UUID.randomUUID().toString())
	                .setUserId(request.getUuid())
	                .setUserType(userType)
	                .setSubmittedAt(System.currentTimeMillis())
	                .setUsercode(request.getNewUserCode())
	                .build();

	        LOG.info(String.format("Fire event for changing User Info: %s", request.getUuid()));
	        eventHandler.publish(changeUserInfoEvent);
		}
		catch(Exception e){
			Log.info(e.toString());	
		}
		
		LOG.info("Fired event change user code ");
		return Boolean.TRUE;
	}
	
	public boolean adminChangeExtendUserType(final ChangeExtendUserTypeRequest request) {
		
		final UserGridResponse<UserEntity> user = userClient.getUserByQuery(String.format(QUERY_USER_EXIST_BY_UUID, request.getUserUuid()));
    	if(user.isEmpty()) {
    		return false;
    	}
		UserEntity newUser = UserEntity.builder()
								    .extUserType(request.getExtUserType())
								    .build();
		UserGridResponse<UserEntity> userInfo = userClient.updateUserEntity(request.getUserUuid(), newUser);
		
		if (Objects.isNull(userInfo) || userInfo.getEntities().isEmpty()) {
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}

	public boolean checkVerifyPhone(String uuid) {
		 Optional<BasicUserEntity> userOptional =  userRepo.findByUuid(uuid);
		 if(userOptional.isPresent()) {
			 return  userOptional.get().getVerifyPhone();
		 }
		 return false;
	}

	public Boolean updateVerifyPhone(String uuid) {
		Optional<BasicUserEntity> userOptional =  userRepo.findByUuid(uuid);
		 if(userOptional.isPresent()) {
			 BasicUserEntity user = userOptional.get();
			 user.setVerifyPhone(true);
			 userRepo.save(user);
			 VerifyPhoneNumberDTO phoneDto = new VerifyPhoneNumberDTO(true);
			 userClient.updateVerifyPhoneNumber(uuid, phoneDto);
			 return true;
		 }
		 return false;
	}

	public Boolean updatePhone(String uuid, String phone) {
		Optional<BasicUserEntity> userOptional =  userRepo.findByUuid(uuid);
		 if(userOptional.isPresent()) {
			 BasicUserEntity user = userOptional.get();
			 user.setPhone(phone);
			 userRepo.save(user);
			 return true;
		 }
		 return false;
	}
	
	public String getPhoneNumber(String uuid) {
		Optional<BasicUserEntity> userOptional =  userRepo.findByUuid(uuid);
		 if(userOptional.isPresent()) {
			 BasicUserEntity user = userOptional.get();
			 return user.getPhone();
		 }
		 return null;
	}
}
