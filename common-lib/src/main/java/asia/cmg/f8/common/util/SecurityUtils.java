package asia.cmg.f8.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public final class SecurityUtils {

    private SecurityUtils() {
        // empty
    }

    public static Object getAccount() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            final Object details = authentication.getDetails();
            if (details instanceof OAuth2AuthenticationDetails) {
                final OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) details;
                return oauthDetails.getDecodedDetails(); // this should be DefaultAccount
            }
        }
        // TODO should we return null?
        return null;
    }
}
