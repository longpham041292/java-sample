package asia.cmg.f8.gateway.security.api;

import java.util.Optional;

/**
 * Created on 12/27/16.
 */
public interface UserDetailService {

    /**
     * Load {@link UserDetail} by access token.
     *
     * @param token the access token
     * @return user detail.
     */
    Optional<UserDetail> findByAccessToken(final String token);
}
