package asia.cmg.f8.user.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.dto.ResetUserPassword;
import asia.cmg.f8.user.entity.ChangePassword;
import asia.cmg.f8.user.entity.ChangePasswordResponse;
import asia.cmg.f8.user.entity.ChangeUsername;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.notification.dto.UsergridChangeNameApi;

/**
 * Created on 1/10/17.
 */
@Component
public class ChangeUsernamePasswordFallback implements ChangeUsernamePasswordClient {
	private static final Logger LOG = LoggerFactory.getLogger(ChangeUsernamePasswordFallback.class);

	@Override
	public UserGridResponse<ChangePasswordResponse> changePassword(@PathVariable("uuid") final String uuid,
			@RequestBody final ChangePassword request) {
		LOG.error("Error occur while change password");
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> changeUsername(@PathVariable("uuid") final String uuid,
			@RequestBody final ChangeUsername request) {
		LOG.error("Error occur while change username");
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> getUserByUserName(@PathVariable(QUERY) final String query) {
		LOG.error("Error occur while finding user");
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> changeUserCode(String uuid, UserEntity request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGridResponse<ChangePasswordResponse> resetUserPassword(String uuid, ResetUserPassword request) {
		LOG.error("Error occur while reset user password");
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> changeName(String uuid, UsergridChangeNameApi request) {
		LOG.error("Error occur while change name of user");
		return null;
	}
}
