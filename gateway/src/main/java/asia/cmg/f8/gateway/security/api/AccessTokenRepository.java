package asia.cmg.f8.gateway.security.api;

import org.springframework.security.core.Authentication;

/**
 * Created on 10/21/16.
 */
public interface AccessTokenRepository {

    void save(String token, Authentication authentication);

    Authentication load(String token);

    boolean hasAuthentication(String token);

    void remove(String token);

    /**
     * Find an access token by using user's uuid.
     *
     * @param uuid the uuid
     * @return the access token. Return null if token is not found.
     */
    String findByUuid(String uuid);
}
