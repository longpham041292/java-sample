package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallbackImpl implements UserClient {

    @Override
    public UserGridResponse<UserEntity> getUserByUuid(final String uuid) {
        //TODO: return an Flag to handle exception instead throw exception
        throw new UserGridException("ERROR_ON_GET_USER_BY_UUID",
                String.format("Error when query user %s", uuid));
    }

    @Override
    public UserGridResponse<UserEntity> getEmailAdmins(final String query) {
        
        //TODO: return an Flag to handle exception instead throw exception
        throw new UserGridException("ERROR_ON_GET_USER_BY_EMAIL", String.format("Error when perform query: %s",
                query));
    }

	@Override
	public UserGridResponse<UserEntity> updateLastMsg(final String uuid, final UserEntity newUser) {
		// TODO Auto-generated method stub
		return null;
	}

}
