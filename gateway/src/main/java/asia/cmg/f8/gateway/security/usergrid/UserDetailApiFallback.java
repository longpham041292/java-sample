package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * Created on 11/3/16.
 */
@Component
public class UserDetailApiFallback implements UserDetailApi {

    @Override
    public UserGridResponse<Map<String, Object>> currentUser(@RequestHeader("Authorization") final String accessToken) {
        return null;
    }
}
