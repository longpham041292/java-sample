package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.client.UserApiClient;
import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.exception.UserNotActivateException;
import asia.cmg.f8.gateway.security.token.JwtTokenFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Created on 11/3/16.
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationProvider.class);

    private final AuthorityService authorityService;
    
    public static final String ACCESSTOKENTYPE = "access token";
    public static final String REFRESHTOKENTYPE = "refresh token";
    
    @Autowired
    public JwtTokenFactoryImpl jwtFactoryImpl;
    
    @Autowired
    public UserApiClient userApiClient;

    protected AbstractAuthenticationProvider(final AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public final Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        final ClientAuthentication auth = (ClientAuthentication) authentication;
        final TokenAuthentication tokenAuthentication = doAuthenticate(auth);

        if (tokenAuthentication != null) {

            // it mean user's authenticated. Now we populate other user information to display on UI.
            final UserDetail userDetail = tokenAuthentication.getUserDetail();
            if (!userDetail.isActivated()) { // check again to ensure the user is valid for authentication

                LOGGER.info("User \"{}\" is not activated. Do not allow to login", userDetail.getUserName());
                throw new UserNotActivateException("User is not activated.");
            }
            LOGGER.info("User \"{}\" is authenticated.", userDetail.getUserName());
            return authorityService.loadAuthorities(tokenAuthentication);
        }
        return null; // otherwise just ignore it.
    }

    /**
     * @param authentication the authentication
     * @return {@link TokenAuthentication}. Return null if it's fail to authenticate
     */
    protected abstract TokenAuthentication doAuthenticate(ClientAuthentication authentication) throws AuthenticationException;

    @Override
    public final boolean supports(final Class<?> authentication) {
        return ClientAuthentication.class.equals(authentication);
    }
}
