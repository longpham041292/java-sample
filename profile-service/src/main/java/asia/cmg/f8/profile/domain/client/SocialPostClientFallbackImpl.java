package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.ActivityEntity;
import rx.Observable;

import org.springframework.stereotype.Component;

/**
 * Created on 12/21/16.
 */
@Component
public class SocialPostClientFallbackImpl implements ActivityClient {

	@Override
	public UserGridResponse<asia.cmg.f8.profile.domain.entity.ActivityEntity> getActivityByQuery(String query) {
		return null;
	}

	@Override
	public Observable<UserGridResponse<ActivityEntity>> getUserLikedPostStatusByQuery(String userId, String query) {
		return null;
	}

	@Override
	public UserGridResponse<ActivityEntity> getActivityByQuery(String query, int limit) {
		return null;
	}
}
