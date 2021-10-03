package asia.cmg.f8.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.entiy.usergrid.PagedUserResponse;
import asia.cmg.f8.report.entiy.usergrid.UserEntity;

@Component
public class UserClientFallbackImpl implements UserClient {

	private static final Logger LOG = LoggerFactory.getLogger(UserClientFallbackImpl.class);

	@Override
	public UserGridResponse<UserEntity> getUser(final String uuid) {
		LOG.error("Could not get getUser with {}", uuid);
		return new UserGridResponse<>();
	}

	@Override
	public UserGridResponse<UserEntity> searchUsers(String query) {
		LOG.error("Could not get searchUsers with query [{}]", query);
		return new UserGridResponse<UserEntity>();
	}

	@Override
	public PagedUserResponse<UserEntity> searchUsers(String query, int limit) {
		LOG.error("Could not get searchUsers with query [{}] and limit [{}]", query, limit);
		return new PagedUserResponse<UserEntity>();
	}

	@Override
	public PagedUserGridResponse<UserEntity> searchUsers(String query, int limit, String cursor) {
		LOG.error("Could not get searchUsers with query [{}] and limit [{}] and cursor [{}]", query, limit, cursor);
		return new PagedUserGridResponse<UserEntity>();
	}
}
