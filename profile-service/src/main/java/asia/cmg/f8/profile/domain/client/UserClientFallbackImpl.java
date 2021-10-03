package asia.cmg.f8.profile.domain.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.ClubEntity;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.RatingSessionEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.dto.WhosHotConfig;
import rx.Observable;

/**
 * Created by nhieu on 8/31/17.
 */
@Component
public class UserClientFallbackImpl implements UserClient{

    private static final Logger LOG = LoggerFactory.getLogger(UserClientFallbackImpl.class);

    @Override
    public UserGridResponse<UserEntity> getUser(final String uuid) {
        LOG.error("Could not get getUser with {}", uuid);
        return new UserGridResponse<>();
    }

//    @Override
//    public UserGridResponse<UserEntity> getCurrentUser(final String accessToken) {
//        LOG.error("Could not get getCurrentUser with {}", accessToken);
//        return new UserGridResponse<>();
//    }

//    @Override
//    public UserGridResponse<UserEntity> updateProfile(final UserEntity userEntity,final String userId,final String token) {
//        LOG.error("Could not update updateProfile with userId {} and token {}", userId, token);
//        return new UserGridResponse<>();
//    }

    @Override
    public UserGridResponse<UserEntity> updateProfile(final UserEntity userEntity,final String userId) {
        LOG.error("Could not update updateProfile with {}", userId);
        return new UserGridResponse<>();
    }

    @Override
    public UserGridResponse<UserEntity> updateProfileBySystem(final Map<String, Object> userProperties,final  String userId) {
        LOG.error("Could not update updateProfileBySystem with {}", userId);
        return null;
    }

    @Override
    public UserGridResponse<UserEntity> getUserByQuery(final String query) {
        LOG.error("Could not get getUserByQuery with query {}", query);
        return new UserGridResponse<>();
    }

    @Override
    public PagedUserResponse<UserEntity> searchUsers(final String query,final  int limit) {
        LOG.error("Could not get searchUsers with query {}", query);
        return new PagedUserResponse<>();
    }

    @Override
    public PagedUserResponse<UserEntity> searchUsersWithCursor(final String query,final  int limit,final String cursor) {
        LOG.error("Could not get searchUsersWithCursor with query {}", query);
        return new PagedUserResponse<>();
    }

//    @Override
//    public PagedUserResponse<UserEntity> getContractingConnection(final String token,final  String query,final  int limit) {
//        LOG.error("Could not get getContractingConnection with query {} and token {}", query, token);
//        return new PagedUserResponse<>();
//    }

    @Override
    public PagedUserResponse<UserEntity> searchContractingUser(final String uuid, final  String query) {
        LOG.error("Could not search searchContractingUser with query {}", query);
        return new PagedUserResponse<>();
    }

    @Override
    public PagedUserResponse<UserEntity> createContractingConnection(final String ptUuid,final  String euUuid) {
        LOG.error("Could not create createContractingConnection with ptUuid {} and euUuid {}", ptUuid, euUuid);
        return new PagedUserResponse<>();
    }


//	@Override
//	public UserGridResponse<UserEntity> getUserByQuery(final String query, final int limit) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public PagedUserResponse<UserEntity> updateRatingOfTheTrainer(final String token, final String session_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<RatingSessionEntity> getRatingSessions(final String ptUuid, final String session_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public PagedUserResponse<RatingSessionEntity> deleteRatingSessions(final String ptUuid, final String session_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Observable<UserGridResponse<UserEntity>> getUserByQueryAvatar(final String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<RatingSessionEntity> getAllRatingSessions(final String ptUuid, final int limit, final String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<RatingSessionEntity> getRatingSessions(final String query, final int limit, final String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<Object> addUserToClub(final String club_uuid, final String user_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<Object> deleteUserToClub(final String club_uuid, final String user_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<Object> addClubToUser(final String club_uuid, final String user_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<Object> deleteClubToUser(final String club_uuid, final String user_uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<UserEntity> searchUsersFromClub(final String club_uuid, final int limit, final String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedUserResponse<ClubEntity> addClubsOfOwner(String user_uuid, String clubUuid) {
		LOG.error("Could not add list of clubs to owner [{}]", user_uuid);
		return null;
	}

	@Override
	public PagedUserResponse<ClubEntity> getClubsFromOwner(String user_uuid) {
		LOG.error("Could not get clubs of owner [{}]", user_uuid);
		return null;
	}

	@Override
	public PagedUserResponse<ClubEntity> deleteClubOfOwner(final String user_uuid, final String clubUuid) {
		LOG.error("Could not delete club [{}] of owner [{}]", clubUuid, user_uuid);
		return null;
	}

	@Override
	public UserGridResponse<WhosHotConfig> getWhosHotConfig(String query, int limit) {
		LOG.error("Could not get getWhosHotConfig with query {}", query);
        return new UserGridResponse<>();
	}

	@Override
	public PagedUserResponse<UserEntity> getPTsInfo(String query) {
		// TODO Auto-generated method stub
		 LOG.error("Could not get getPTInfo with query {}", query);
        return new PagedUserResponse<>();
	}
}
