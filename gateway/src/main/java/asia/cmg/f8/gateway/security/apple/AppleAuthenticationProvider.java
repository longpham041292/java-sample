package asia.cmg.f8.gateway.security.apple;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import com.google.gson.Gson;

import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.auth.AbstractAuthenticationProvider;
import asia.cmg.f8.gateway.security.auth.ClientAuthentication;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.auth.TokenAuthenticationUtil;
import asia.cmg.f8.gateway.security.dto.AppleUserPayloadDTO;
import asia.cmg.f8.gateway.security.dto.AppleVerifyResponse;
import asia.cmg.f8.gateway.security.dto.UserInfo;
import asia.cmg.f8.gateway.security.exception.EmailExistedException;
import asia.cmg.f8.gateway.security.exception.InValidAuthenticationException;
import asia.cmg.f8.gateway.security.exception.UserNotActivateException;
import asia.cmg.f8.gateway.security.exception.UserNotConfirmedException;
import asia.cmg.f8.gateway.security.exception.UserNotExistException;
import asia.cmg.f8.gateway.security.usergrid.UserGridApi;
import asia.cmg.f8.gateway.security.usergrid.UserGridResponse;

public class AppleAuthenticationProvider extends AbstractAuthenticationProvider {
	private static final String APPLE_GRANT_TYPE = "apple";

	@Value("${security.apple.client_id}")
	private String APPLE_CLIENT_ID;
	@Value("${security.apple.client_secret_key}")
	private String APPLE_CLIENT_SECRET;

	@Autowired
	private AppleUserInfoAPI appleUserInfoAPI;

	@Autowired
	private UserGridApi userGridApi;

	@Autowired
	private TokenAuthenticationUtil tokenAuthenticationUtil;

	protected AppleAuthenticationProvider(AuthorityService authorityService) {
		super(authorityService);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TokenAuthentication doAuthenticate(ClientAuthentication auth) throws AuthenticationException {
		if (!auth.getGrantType().equalsIgnoreCase(APPLE_GRANT_TYPE)) {
			return null;
		}

		final String appleCode = auth.get("apple_code");
		final String appleID = auth.get("apple_id");

		
		LOGGER.info("Start find user with apple id {} in usergrid", appleID);
        final Map<String, Object> response = getResponseUserByAppleId(appleID, appleCode);
        
        LOGGER.info("Exist user ID {}, Start Validate apple code {} and apple id {}", appleCode, appleID);
        AppleUserPayloadDTO dto = this.appleGenerateToken(appleCode);
        
        if (dto == null) {
        	LOGGER.error("Invalid Apple code token or user is deactivated, " + appleCode);
        	throw new InValidAuthenticationException("Invalid Apple code token or user is deactivated, ");
        }
        
        
		UserInfo userInfo = UserInfo.create(response);
		UserDetail userDetail =  new AppleUserDetail(response);
		TokenAuthentication authentication = null;
        if( doValidation(userInfo)) {
    		/**
    		 * Validate token in DB and generate new accessToken, Refresh Token
    		 */
        	return tokenAuthenticationUtil.generateTokenAuthentication(userInfo, userDetail);
        }
		return authentication;		
	}

	private boolean doValidation(UserInfo userInfo) {
    	if (userInfo == null) {
            throw new AuthenticationServiceException("User is not exist in system");
        }
    	
        final String userUuid = userInfo.getUuid();
        
        if (!userInfo.isConfirmed()) {
        	LOGGER.info("User uuid {} not confirmed", userInfo.getUuid());
            throw new UserNotConfirmedException("User " + userUuid + " is not confirmed");
        }

        if (!userInfo.isActivated()) {
        	LOGGER.info("User uuid {} not activated", userInfo.getUuid());
            throw new UserNotActivateException("User with facebook id " + userUuid + " is not activated");
        }

        return true;
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
			throw new InValidAuthenticationException("Invalid Apple refresh token or user is deactivated, " + e);
		}
	}
	public AppleUserPayloadDTO appleGenerateToken(String appleCode) {
		try {
			LOGGER.info("Start validate Apple Code {} ", appleCode);
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
			LOGGER.error("Invalid Apple code or user is deactivated, {}, Code: ", e.getMessage(), appleCode);
			throw new InValidAuthenticationException("Invalid Apple code or user is deactivated, " + e + " Code: "+ appleCode);
		}
	}

	private Map<String, Object> getResponseUserByAppleId(final String appleId, String appleCode) {
		final UserGridResponse<Map<String, Object>> response = userGridApi
				.getUserByQuery("where auth_provider_id='" + appleId + "'");
		if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
			LOGGER.error("Apple Id {} does not exist in system", appleId);
			LOGGER.info("Start generate Apple refresh token {} ", appleCode);
			AppleVerifyResponse responseData = appleUserInfoAPI.authAppleID("authorization_code", APPLE_CLIENT_ID,
					APPLE_CLIENT_SECRET, appleCode, "" // empty refresh token
			);
			if (responseData == null) {
	        	LOGGER.error("Invalid Apple code token or user is deactivated, " + appleCode);
	        	throw new InValidAuthenticationException("Invalid Apple code token or user is deactivated, ");
	        }
			throw new UserNotExistException(responseData.getRefreshToken());
		}
		return response.getEntities().stream().filter(entry -> {
			final String username = (String) entry.get("username");
			return username != null;
		}).findFirst().orElse(null);
	}
	
	private void isExistUserByEmail(final String email) {
		final UserGridResponse<Map<String, Object>> response = userGridApi.getUserByQuery("where email='" + email + "'");
		if (response != null && response.getEntities() != null || !response.getEntities().isEmpty()) {
			LOGGER.info("User with email {} is existed in usergrid.", email);
			throw new EmailExistedException("User with "+ email + " is existed.");
		}

	}
}
