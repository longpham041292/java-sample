package asia.cmg.f8.gateway.security.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 11/3/16.
 */
public class AccessTokenLogoutHandler implements LogoutHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenLogoutHandler.class);

    /*private final AccessTokenRepository accessTokenRepository;

    public AccessTokenLogoutHandler(final AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }*/

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        /*final String token = request.getParameter(AccessTokenLogoutFilter.TOKEN_PROP);
        if (token != null) {
            accessTokenRepository.remove(token);
        }*/
        LOGGER.info("Logged out token successful.");
    }
}
