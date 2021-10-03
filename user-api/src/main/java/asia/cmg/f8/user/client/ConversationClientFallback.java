package asia.cmg.f8.user.client;

import org.springframework.stereotype.Component;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;

/**
 * Created by 11/9/16.
 */
@Component
public class ConversationClientFallback implements ConversationClient {

	@Override
	public UserGridResponse<UserEntity> createConversationConnectionWith(String loginUuid, String userUuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> deleteConversationConnectionWith(String loginUuid, String userUuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<UserEntity> getConversationConnection(String userUuid, String cursor, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
