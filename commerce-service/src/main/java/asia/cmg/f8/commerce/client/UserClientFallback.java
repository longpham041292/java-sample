package asia.cmg.f8.commerce.client;

import org.springframework.stereotype.Component;

import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.common.util.UserGridResponse;

@Component
public class UserClientFallback implements UserClient {

	@Override
	public UserGridResponse<UserEntity> getUser(String uuid) {
		return new UserGridResponse<UserEntity>();
	}

	@Override
	public UserGridResponse<UserEntity> getUserInGroup(String group, String query) {
		return new UserGridResponse<UserEntity>();
	}

}
