package asia.cmg.f8.gateway.security.facebook;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.auth.AbstractAuthenticationProvider;
import asia.cmg.f8.gateway.security.auth.ClientAuthentication;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.auth.TokenAuthenticationUtil;
import asia.cmg.f8.gateway.security.dto.UserInfo;
import asia.cmg.f8.gateway.security.exception.EmailExistedException;
import asia.cmg.f8.gateway.security.exception.UserNotActivateException;
import asia.cmg.f8.gateway.security.exception.UserNotConfirmedException;
import asia.cmg.f8.gateway.security.exception.UserNotExistException;
import asia.cmg.f8.gateway.security.usergrid.UserGridApi;
import asia.cmg.f8.gateway.security.usergrid.UserGridProperties;
import asia.cmg.f8.gateway.security.usergrid.UserGridResponse;

/**
 * Created on 11/2/16.
 */
public class FacebookAuthenticationProvider extends AbstractAuthenticationProvider {

	public static final Logger LOGGER = LoggerFactory.getLogger(FacebookAuthenticationProvider.class);
    private static final String FACEBOOK_GRANT_TYPE = "facebook";

    private final UserGridApi userGridApi;
    private final FacebookUserInfoApi facebookUserInfoApi;
    private final FacebookUserGridAuthenticationApi facebookUserGridAuthenticationApi;
    private final UserGridProperties userGridProperties;
	@Autowired
	private TokenAuthenticationUtil tokenAuthenticationUtil;

    public FacebookAuthenticationProvider(final AuthorityService authorityService, final UserGridApi userGridApi, final FacebookUserInfoApi facebookUserInfoApi, final FacebookUserGridAuthenticationApi facebookUserGridAuthenticationApi, final UserGridProperties userGridProperties) {
        super(authorityService);
        this.userGridApi = userGridApi;
        this.facebookUserInfoApi = facebookUserInfoApi;
        this.facebookUserGridAuthenticationApi = facebookUserGridAuthenticationApi;
        this.userGridProperties = userGridProperties;
    }

    @Override
    protected TokenAuthentication doAuthenticate(final ClientAuthentication auth) throws AuthenticationException {

        if (!FACEBOOK_GRANT_TYPE.equals(auth.getGrantType())) {
            return null;
        }

        String fbToken = auth.get("facebook_token"); // facebook access token
        if(fbToken == null || fbToken.isEmpty()) {
        	fbToken = auth.get("fbToken");
        }
        LOGGER.info("Start authenticate user with grant type {} and facebook token {}", FACEBOOK_GRANT_TYPE, fbToken);
        
        final FbUserInfo fbInfo = getFbUserInfo(fbToken);
        final Map<String, Object> response = getResponseUserByFacebookId(fbInfo.getId(), fbInfo.getEmail());
		
		UserInfo userInfo = UserInfo.create(response);
		UserDetail userDetail =  new FacebookUserDetail(response);
		TokenAuthentication authentication = null;
        if( doValidation(userInfo)) {
    		/**
    		 * Validate token in DB and generate new accessToken, Refresh Token
    		 */
        	return tokenAuthenticationUtil.generateTokenAuthentication(userInfo, userDetail);
        }
		return authentication;
    }

    protected boolean doValidation(final UserInfo userInfo) {
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

    private Map<String, Object> getResponseUserByFacebookId(final String facebookId, final String email) {
        final UserGridResponse<Map<String, Object>> response = userGridApi.getUserByQuery("where auth_provider_id='" + facebookId + "' or fbId='" + facebookId + "'");
        if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
        	
//        	final UserGridResponse<Map<String, Object>> responseCheckEmail = userGridApi.getUserByQuery("where email='" + email + "'");
//        	if (responseCheckEmail == null || responseCheckEmail.getEntities() == null || responseCheckEmail.getEntities().isEmpty()) {
            	LOGGER.info("User with Facebook id {} does not exist.", facebookId);
            	throw new UserNotExistException("User with Facebook id "+ facebookId + " does not exist.");
//        	}
//    		LOGGER.info("User with email {} is existed in usergrid.", email);
//        	throw new EmailExistedException("User with "+ email + " is existed.");
        }
        return response.getEntities().stream().filter(entry -> {
            final String username = (String) entry.get("username");
            return username != null;
        }).findFirst().orElse(null);
    }

    protected FbUserInfo getFbUserInfo(final String fbToken) {
    	FbUserInfo userInfo = null;
    	try {
    		if(fbToken == null || fbToken == "") {
    			LOGGER.info("Facebook token is empty");
    			throw new AuthenticationServiceException("Facebook token is required");
    		}
    		userInfo = facebookUserInfoApi.getUserInfo(fbToken);
		} catch (Exception e) {
			LOGGER.info("Failed to authenticate token with facebook token. {}. Error: {}",fbToken,  e.getMessage());
			throw new AuthenticationServiceException("Failed to authenticate token with facebook.");
		}
        return userInfo;
    }
}
