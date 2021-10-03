package asia.cmg.f8.gateway.security.facebook;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on 11/3/16.
 */
@Component
public class FacebookUserInfoApiFallback implements FacebookUserInfoApi {

	@Override
	public FbUserInfo getUserInfo(String token) {
		return null;
	}
}
