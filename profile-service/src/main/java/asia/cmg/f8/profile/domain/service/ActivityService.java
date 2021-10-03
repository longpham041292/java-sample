package asia.cmg.f8.profile.domain.service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.ActivityClient;
import asia.cmg.f8.profile.domain.entity.ActivityEntity;
import rx.Observable;

@Service
public class ActivityService {
	
	private static final String QUERY_USER_LIKED_POST_BY_POST_ID = "select uuid where %s";
	
	@Autowired
	private ActivityClient postClient;

	public Observable<Set<String>> getUserLikedPostStatusByPostId(
            final String userId,
            final Collection<String> listPostId) {

        final String query = listPostId.stream().map(postId -> "uuid ='" + postId + "'").collect(Collectors.joining(" or "));

        final Observable<UserGridResponse<ActivityEntity>> userGridResponseObservable =
							                postClient.getUserLikedPostStatusByQuery(userId, String.format(QUERY_USER_LIKED_POST_BY_POST_ID, query));

        return userGridResponseObservable
                .map(activityEntityUserGridResponse -> activityEntityUserGridResponse
                .getEntities()
                .stream()
                .map(ActivityEntity::getUuid)
                .collect(Collectors.toSet()))
                .firstOrDefault(Collections.emptySet());
    }
	
	public List<ActivityEntity> getActivitiesByOwner(final String ownerUuid, final int limit) {
		String QUERY_ACTIVITIES_BY_OWNER = String.format("where owner_id = '%s' and status = 'published'", ownerUuid);
		try {
			UserGridResponse<ActivityEntity> ugResponse = postClient.getActivityByQuery(QUERY_ACTIVITIES_BY_OWNER, limit);
			if(ugResponse != null) {
				return ugResponse.getEntities();
			}
		} catch (Exception e) {
		}
		
		return Collections.emptyList();
	}
	
	public List<ActivityEntity> getVideoPostsByOwner(final String ownerUuid, final int limit) {
		String QUERY_ACTIVITIES_BY_OWNER = String.format("where owner_id = '%s' and content_type = 'video' and status = 'published'", ownerUuid);
		try {
			UserGridResponse<ActivityEntity> ugResponse = postClient.getActivityByQuery(QUERY_ACTIVITIES_BY_OWNER, limit);
			if(ugResponse != null) {
				return ugResponse.getEntities();
			}
		} catch (Exception e) {
		}
		
		return Collections.emptyList();
	}
	
	public List<ActivityEntity> getSponsoredVideoPosts(final int limit) {
		Long currentTimeInSecond = Instant.now().getEpochSecond();
		String QUERY_ACTIVITIES_BY_OWNER = String.format("where sponsored.start_time <= %s and sponsored.end_time >= %s and content_type = 'video' and status = 'published'", currentTimeInSecond, currentTimeInSecond);
		try {
			UserGridResponse<ActivityEntity> ugResponse = postClient.getActivityByQuery(QUERY_ACTIVITIES_BY_OWNER, limit);
			if(ugResponse != null) {
				return ugResponse.getEntities();
			}
		} catch (Exception e) {
		}
		
		return Collections.emptyList();
	}
}
