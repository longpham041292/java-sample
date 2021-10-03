package asia.cmg.f8.gateway.security.usergrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created on 11/3/16.
 */
@Component
public class UserGridLogoutApiFallback implements UserGridLogoutApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserGridLogoutApiFallback.class);

    @Override
    public Map<String, Object> logout(@RequestParam("uuid") final String uuid) {
        LOGGER.warn("Fail to logout user {}.", uuid);
        return null; // it means the logout is failed
    }
}
