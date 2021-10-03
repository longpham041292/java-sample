package asia.cmg.f8.gateway.security.usergrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on 11/3/16.
 */
@Component
public class UserRoleApiFallback implements UserRoleApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleApiFallback.class);

    @Override
    public UserGridResponse<RoleEntity> getRoles(@RequestParam("uuid") final String uuid) {
        LOGGER.warn("Failed to get roles from user {}", uuid);
        return null; // just return null
    }

	@Override
	public UserGridResponse<RoleEntity> getRoles(String uuid, String accessToken) {
		LOGGER.warn("Failed to get roles from user {}", uuid);
        return null; // just return null
	}
}
