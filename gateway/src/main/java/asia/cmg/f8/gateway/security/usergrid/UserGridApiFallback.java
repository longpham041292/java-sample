package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import asia.cmg.f8.gateway.security.api.UserDetail;

import java.util.Map;

/**
 * Created on 11/2/16.
 */
@Component
public class UserGridApiFallback implements UserGridApi {

    @Override
    public Map<String, Object> requestAccessToken(@RequestBody final Map<String, Object> body) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> getUserByQuery(@PathVariable("query") final String query) {
        return null;
    }

	@Override
	public UserGridResponse<Map<String, Object>> getUserByUuid(String uuid) {
		return null;
	}
}
