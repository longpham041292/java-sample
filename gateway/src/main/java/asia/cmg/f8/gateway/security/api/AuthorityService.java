package asia.cmg.f8.gateway.security.api;

import asia.cmg.f8.gateway.security.auth.TokenAuthentication;

/**
 * Load authorities of given {@link TokenAuthentication}
 * <p>
 * Created on 12/27/16.
 */
public interface AuthorityService {

    /**
     * Load authorities of given {@link TokenAuthentication}.
     *
     * @param authentication the current token
     * @return new token with authorities populated.
     */
    TokenAuthentication loadAuthorities(TokenAuthentication authentication);
}
