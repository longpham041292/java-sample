package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.auth.AbstractAuthenticationProvider;
import asia.cmg.f8.gateway.security.auth.ClientAuthentication;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;
import asia.cmg.f8.gateway.security.dto.UserInfo;
import asia.cmg.f8.gateway.security.exception.InValidUserNameOrPasswordException;
import asia.cmg.f8.gateway.security.exception.UserNotActivateException;
import asia.cmg.f8.gateway.security.exception.UserNotConfirmedException;
import asia.cmg.f8.gateway.security.token.JwtProperties;
import asia.cmg.f8.gateway.security.utils.GatewayConstant;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 10/19/16.
 */
public class UserGridAuthenticationProvider extends AbstractAuthenticationProvider {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserGridAuthenticationProvider.class);

    private static final String USER_QUERY_EMAIL = "select uuid,username,activated,email,confirmed,updated_profile where email = '%s'";
    private static final String USERNAME = "username";
    private final UserGridApi userGridApi;
    private final UserGridProperties userGridProperties;
    private final JwtProperties jwtProperties;
    
    @Autowired
	private PasswordEncoder passwordEncoder;

    public UserGridAuthenticationProvider(final UserGridAuthorityService authorityService, final UserGridApi userGridApi, final UserGridProperties userGridProperties, JwtProperties jwtProperties) {
        super(authorityService);
        this.userGridApi = userGridApi;
        this.userGridProperties = userGridProperties;
        this.jwtProperties = jwtProperties;
    }

    private UserInfo getUserByEmail(final String email) {
        final UserGridResponse<Map<String, Object>> response = userGridApi.getUserByQuery(String.format(USER_QUERY_EMAIL, email));
        if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
            return null;
        }
        return UserInfo.create(response.getEntities().iterator().next());
    }


    @Override
    protected TokenAuthentication doAuthenticate(final ClientAuthentication auth) throws AuthenticationException {

        try {
            TokenAuthentication authentication = null;
            final String allowedGrantType = "password";
            if (allowedGrantType.equals(auth.getGrantType())) {

                /**
                 * because user-grid does not support login by email so we do a little trick
                 * here by checking if there is any user with given email and then using their username to login with user-gird.
                 */
                final String email = auth.get("email");
                final String password = auth.get("password");
                
                LOGGER.info("Start authenticate user with grant type {} and email {}", allowedGrantType, email);

                final UserInfo userInfo = getUserByEmail(email);
                if (userInfo == null) {
                	LOGGER.info("Could not get user info from UG by email {}", email);
                    throw new InValidUserNameOrPasswordException(GatewayConstant.LOGIN_ERROR_MSG);
                }

                /**
                 * Verify if user's confirmed by email yet. It happen first.
                 */
                if (!userInfo.isConfirmed()) {
                	LOGGER.info("User uuid {} not confirmed", userInfo.getUuid());
                    throw new UserNotConfirmedException("User " + email + " is not confirmed");
                }

                /**
                 * Verify if user is activated. If must happen after confirm verification as business's requirement
                 */
                if (!userInfo.isActivated()) {
                	LOGGER.info("User uuid {} not activated", userInfo.getUuid());
                    throw new UserNotActivateException("User " + email + " is not activated");
                }

                final String username = userInfo.getUsername();

                /**
                 * prepare content to login user with user-grid
                 */
                final Map<String, Object> content = new HashMap<>(4);
                content.put(USERNAME, username);
                content.put("password", auth.get("password"));
                content.put("grant_type", allowedGrantType);

                final Long ttl = userGridProperties.getAccessTokenTtl();
                if (ttl != null && ttl > 0) {
                    content.put("ttl", ttl);
                    LOGGER.info("Request access token with ttl = {} milliseconds", ttl);
                }

                // Validate access token from User-grid
                final Map<String, Object> token = userGridApi.requestAccessToken(content);
//                String accessToken = (String) token.get("access_token");
                
                // Validate access token from database
                AccountTokenDTO tokenDTO = null;
                long expiredAt = System.currentTimeMillis() + jwtProperties.getExpiresIn() * 1000;
                long refreshExpiredAt = System.currentTimeMillis() + jwtProperties.getRefreshExpiresIn() * 1000;
                final Map user = (Map) token.get("user");
                final UserDetail userDetail = new UserGridUserDetail(user);
                
                try {
                	tokenDTO = userApiClient.getUserToken(userInfo.getUuid());
				} catch (Exception e) {
					// TODO: logging on user token does not existed
				}
            	String accessToken = jwtFactoryImpl.encode(userInfo.getUuid(), expiredAt);
            	String refreshToken = jwtFactoryImpl.encode(userInfo.getUuid(), refreshExpiredAt);
            	
                if(tokenDTO == null) {
                	String encodedPassword = passwordEncoder.encode(password);
                	tokenDTO = new AccountTokenDTO(userInfo.getUuid(), expiredAt, refreshExpiredAt, accessToken,refreshToken , encodedPassword);
                	AccountTokenDTO updatedTokenDTO = userApiClient.saveUserToken(tokenDTO);
                	authentication = new TokenAuthentication(updatedTokenDTO.getAccessToken(), updatedTokenDTO.getRefreshToken(), userGridProperties.getAccessTokenTtl(), userDetail);
                } else {
                	tokenDTO.setAccessToken(accessToken); 
                	tokenDTO.setRefreshToken(refreshToken);
					tokenDTO.setExpiredAt(expiredAt);
					tokenDTO.setRefreshExpiredAt(refreshExpiredAt);
					AccountTokenDTO updatedTokenDTO = userApiClient.saveUserToken(tokenDTO);
                	authentication = new TokenAuthentication(updatedTokenDTO.getAccessToken(), updatedTokenDTO.getRefreshToken(), userGridProperties.getAccessTokenTtl(), userDetail);
				}
            }
            
            return authentication;
        } catch (final FeignException exception) {
            /**
             * Still don't know why Feign handle exception incorrectly in other environment so this is a temporary way to handle FeignException.
             */
            throw new InValidUserNameOrPasswordException(GatewayConstant.LOGIN_ERROR_MSG, exception);
        } catch (Exception e) {
        	throw new InValidUserNameOrPasswordException(GatewayConstant.LOGIN_ERROR_MSG, e);
		}
    }
}
