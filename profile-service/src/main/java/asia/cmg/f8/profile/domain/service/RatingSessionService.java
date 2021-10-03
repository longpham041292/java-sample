package asia.cmg.f8.profile.domain.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.RatingSessionEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;

import rx.Observable;

@Service
public class RatingSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(RatingSessionService.class);

	private static final String SEARCH_RATING_SESSION_QUERY = "select * where user_id='%s'";
	private static final String SEARCH_RATING_SESSION_BY_RATING_STARS_QUERY = "select * where user_id = '%s' and stars >= %s order by stars desc";
	private static final String QUERY_PICTURE_USER = "select uuid, picture where %s";

	private final UserClient userClient;

	@Inject
	public RatingSessionService(final UserClient userClient) {

		this.userClient = userClient;
	}

	public boolean isRatingAdded(final String pu_uuid, final String rating_uuid) {

		try {
			final List<RatingSessionEntity> ratingsessins = userClient.getRatingSessions(pu_uuid, rating_uuid).getEntities();

			if (!ratingsessins.isEmpty()) {
				
				return true;
			}
		} catch (Exception e) {
			
			return false;
		}
		
		return false;
	}
	
	public PagedUserResponse<RatingSessionEntity> getRatingSessionByUserId(final String userId,final int limit ,final String cursor) {

		return userClient.getRatingSessions(String.format(SEARCH_RATING_SESSION_QUERY, userId),limit,cursor);
	}
	
	public PagedUserResponse<RatingSessionEntity> getRatingSessionByUserIdAndRatingStars(final String userId, final Double ratingStars, final int limit, final String cursor) {

		return userClient.getRatingSessions(String.format(SEARCH_RATING_SESSION_BY_RATING_STARS_QUERY, userId, ratingStars), limit, cursor);
	}
	
	
	 public Observable<Map<String, String>> getAvatars(final Collection<String> listUuid) {
	        final String query = String.format(QUERY_PICTURE_USER, joiningListUuidWithOr(listUuid));
	        return userClient.getUserByQueryAvatar(query)
	                .filter(Objects::nonNull)
	                .map(UserGridResponse::getEntities)
	                .filter(Objects::nonNull)
	                .map(entries ->
	                        entries.stream()
	                                .collect(Collectors.toMap(
	                                        UserEntity::getUuid, entity -> Optional.ofNullable(entity.getPicture()).filter(StringUtils::isNotEmpty).orElse(StringUtils.EMPTY))))
	                .doOnError(error -> LOG.error("Get Avatars occurs error {} with query {}", error, query))
	                .firstOrDefault(Collections.emptyMap());
	    }
	
	
	private String joiningListUuidWithOr(final Collection<String> listUuid) {
        return listUuid.stream()
                .distinct()
                .map(user -> "uuid ='" + user + "'")
                .collect(Collectors.joining(" or "));
    }
	
	
	
	
}
