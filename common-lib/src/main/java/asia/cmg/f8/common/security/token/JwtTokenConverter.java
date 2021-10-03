package asia.cmg.f8.common.security.token;

import asia.cmg.f8.common.security.internal.DefaultAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 11/1/16.
 */
public class JwtTokenConverter extends JwtAccessTokenConverter {

    public static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenConverter.class);

    @Override
    public OAuth2Authentication extractAuthentication(final Map<String, ?> map) {

        final OAuth2Authentication authentication = super.extractAuthentication(map);

        final String uuid = (String) map.get("uuid");
        final String userType = (String) map.get("userType");
        final String ugToken = (String) map.get("usergridAccessToken"); // UserGrid access token.
        final String language = (String) map.get("language");
        final Collection<GrantedAuthority> authorities = authentication.getAuthorities();

        final DefaultAccount user = new DefaultAccount(uuid, ugToken, userType, language, authorities);
        authentication.setDetails(user);

        LOGGER.info("Created account from JWT token. uuid=\"{}\" userType=\"{}\" language=\"{}\"", uuid, userType, language);
        return authentication;
    }
}
