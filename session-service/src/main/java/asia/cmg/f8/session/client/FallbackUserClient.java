package asia.cmg.f8.session.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.api.FileUploadApi;
import asia.cmg.f8.session.dto.ActivityPostInfo;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.PagedUserResponse;
import asia.cmg.f8.session.entity.WhosHotConfig;

/**
 * Created on 11/28/16.
 */
@Component
public class FallbackUserClient implements UserClient {
	
	 private static final Logger LOG = LoggerFactory.getLogger(FileUploadApi.class);

    @Override
    public UserGridResponse<UserEntity> getUser(final String userId) {
    	LOG.warn("FallbackUserClient getUser userId: {}", userId);
        return null;
    }

    @Override
    public UserGridResponse<BasicUserInfo> findUserLanguage(@PathVariable("uuid") final String userUuid) {
    	LOG.warn("FallbackUserClient findUserLanguage userId: {}", userUuid);
    	return null;
    }

	
    @Override
    public UserGridResponse<ActivityPostInfo> getActivitiesOfPT(final String query){
    	LOG.warn("FallbackUserClient getActivitiesOfPT query: {}", query);
        return null;
    }

	@Override
	public UserGridResponse<WhosHotConfig> getWhosHotConfig(final String query, final int limit) {
		LOG.warn("FallbackUserClient getWhosHotConfig query: {}", query);
		return null;
	}

	@Override
	public UserGridResponse<WhosHotConfig> updateWhosHotConfig(final String query, final Object question) {
		LOG.warn("FallbackUserClient updateWhosHotConfig query: {}", query);
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> searchUsersFromClub(final String club_uuid, final int limit, final String cursor) {
		LOG.warn("FallbackUserClient searchUsersFromClub club_uuid: {}", club_uuid);
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> deleteFollowingConnectionBySecret(String userUuid, String followerUuid) {
		LOG.warn("FallbackUserClient deleteFollowingConnectionBySecret userUuid:" + userUuid + " followerUuid: " + followerUuid);
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> getUserByQuery(String query) {
		LOG.warn("FallbackUserClient getUserByQuery query: {}", query);
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> createUser(Object user) {
		LOG.warn("FallbackUserClient createUser query: {}", user);
		return null;
	}

	@Override
	public PageResponse<ActivityPostInfo> getFeeds(String userUuid, String cursor, String query, Integer limit) {
        LOG.warn("Fallback UserClient#getFeeds query: {}", query);
		return null;
	}

	@Override
	public void deleteFeed(String userUuid, String feedId) {
        LOG.warn("Fallback UserClient#deleteFeed userId-feedId {}-{}", userUuid, feedId);
	}

	@Override
	public UserGridResponse<UserEntity> searchUsersByQuery(String query) {
		LOG.warn("searchUsersByQuery {}", query);
		return null;
	}

	@Override
	public PagedUserResponse<UserEntity> getUserByQueryWithPaging(String query, int limit, String cursor) {
		LOG.warn("Fallback getUserByQueryWithPaging#query-limit-cursor {}-{}-{}", query, limit, cursor);
		return null;
	}

	@Override
	public PagedUserResponse<UserEntity> getUserByQueryWithPaging(String query, int limit) {
		LOG.warn("Fallback getUserByQueryWithPaging#query-limit {}-{}", query, limit);
		return null;
	}

	@Override
	public UserGridResponse<UserEntity> updateUser(String userId, UserEntity request) {
		LOG.warn("Fallback updateUser#userId-{} {}", userId, request);
		return null;
	}

	@Override
	public Map<String, Object> addUserToGroup(String userUuid, String groupUuid) {
		LOG.warn("Fallback addUserToGroup#userId-{} {}", userUuid, groupUuid);
		return null;
	}
}
