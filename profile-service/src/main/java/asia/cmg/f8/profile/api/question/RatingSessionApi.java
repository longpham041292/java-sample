package asia.cmg.f8.profile.api.question;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.PagedRatingSessionResponse;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.RatingSessionEntity;
import asia.cmg.f8.profile.domain.entity.RatingSessionUserImpl;
import asia.cmg.f8.profile.domain.service.RatingSessionService;





@RestController
public class RatingSessionApi {

	private static final String SESSION_RATING_UUID = "session_rating_uuid";
	
	private static final String SUCCESS = "success";

	private static final Logger LOG = LoggerFactory.getLogger(RatingSessionApi.class);
	
	private static final int MAX_LIMIT = 20;
	
	private static final double MIN_STARS = 4.0d;
		
	private final UserClient userClient;
	
	private final RatingSessionService ratingSessionService;
	
	@Inject
	public RatingSessionApi(final UserClient userClient, final RatingSessionService ratingSessionService){
		this.userClient = userClient;
		this.ratingSessionService = ratingSessionService;
	}

	@RequestMapping(value = "/rating/trainer/{user_id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> updateReasonSessionRating(@PathVariable("user_id") final String user_id,
			@RequestBody final Map<String, List<String>> ratingRequest, final Account account) {

		if (ratingRequest.containsKey(SESSION_RATING_UUID)) {

			final List<String> ratingSessionList = ratingRequest.get(SESSION_RATING_UUID);

			for (final String ratingSessionId : ratingSessionList) {
				LOG.info("-- rating session id = , {}", ratingSessionId);

				if (!ratingSessionService.isRatingAdded(user_id, ratingSessionId)) {
					LOG.info("--- no exites---  ");
					//userClient.updateRatingOfTheTrainer(account.ugAccessToken(), ratingSessionId);
					userClient.updateRatingOfTheTrainer(user_id,ratingSessionId);
				}

			}

		}

		LOG.info("---add selected rating for ueser --- , {} ", user_id);

		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);

	}
	
	@RequestMapping(value = "/mobile/v1/rating_sessions/trainer/{user_id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PagedRatingSessionResponse<RatingSessionUserImpl> searchRatingSessionsByUserUuid(
											@PathVariable("user_id") final String user_id,
								            @RequestParam(value = "limit", required = false, defaultValue = "20")  int limit) {
		final PagedRatingSessionResponse<RatingSessionUserImpl> result = new PagedRatingSessionResponse<>();
		final List<RatingSessionUserImpl> resultUnSelected = new ArrayList<RatingSessionUserImpl>();
		final String cursor = null;
		
		LOG.info("[searchSessionRatingByUserId] userId and limit: {} and {} ", user_id, limit);
		
		PagedUserResponse<RatingSessionEntity> allratingRespond = ratingSessionService.getRatingSessionByUserIdAndRatingStars(user_id, MIN_STARS, limit, cursor);
		if(allratingRespond == null) {
			result.setContent(Collections.emptyList());
			return result;
		}
		
		List<RatingSessionEntity> ratings = allratingRespond.getEntities();
		if (ratings == null || ratings.isEmpty()) {
			result.setContent(Collections.emptyList());
			return result;
		}

		final Set<String> reviewerIds = ratings.stream().map(RatingSessionEntity::getReviewerId) .collect(Collectors.toSet());
		final Map<String, String> userAvatars = ratingSessionService.getAvatars(reviewerIds).toBlocking().first();

		resultUnSelected.addAll(ratings.stream()
				.map(rating -> RatingSessionUserImpl.builder()
						.picture(getUserAvatar(userAvatars, rating.getReviewerId())).id(rating.getUuid())
						.fullName(rating.getReviewerName()).reaction(rating.getReaction())
						.stars(rating.getStars()).sessionDate(rating.getSessionDate())
						.reasons(rating.getReasons()).comment(rating.getComment()).build())
				.collect(Collectors.toList()));

		result.setCursor(allratingRespond.getCursor());
		result.setContent(resultUnSelected);
		result.setTotal(resultUnSelected.size());
		
		return result;
	}

	@RequestMapping(value = "/rating/trainer/{user_id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PagedRatingSessionResponse<RatingSessionUserImpl> searchSessionRatingByUserId(
			@PathVariable("user_id") final String user_id,
			@RequestParam(name = "isSelected", required = false) final Boolean isSelected,
			@RequestParam(required = false)  String cursor,
            @RequestParam(value = "limit", defaultValue = "20")  int limit) {

		final PagedRatingSessionResponse<RatingSessionUserImpl> result = new PagedRatingSessionResponse<>();
		final List<RatingSessionUserImpl> resultUnSelected = new ArrayList<RatingSessionUserImpl>();
		
		LOG.info("[searchSessionRatingByUserId] isSelected and userId: {} and {} ", isSelected, user_id);
		LOG.info("[searchSessionRatingByUserId] cursor and limit: {} and {} ", cursor, limit);

		if (isSelected == null) {
			PagedUserResponse<RatingSessionEntity> allratingRespond = ratingSessionService.getRatingSessionByUserIdAndRatingStars(user_id, MIN_STARS, limit, cursor = null);
			if(allratingRespond == null) {
				result.setContent(Collections.emptyList());
				return result;
			}
			
			List<RatingSessionEntity> ratings = allratingRespond.getEntities();
			if (ratings == null || ratings.isEmpty()) {
				result.setContent(Collections.emptyList());
				return result;
			}

			final Set<String> reviewerIds = ratings.stream().map(RatingSessionEntity::getReviewerId) .collect(Collectors.toSet());
			final Map<String, String> userAvatars = ratingSessionService.getAvatars(reviewerIds).toBlocking().first();

			resultUnSelected.addAll(ratings.stream()
					.map(rating -> RatingSessionUserImpl.builder()
							.picture(getUserAvatar(userAvatars, rating.getReviewerId())).id(rating.getUuid())
							.fullName(rating.getReviewerName()).reaction(rating.getReaction())
							.stars(rating.getStars()).sessionDate(rating.getSessionDate())
							.reasons(rating.getReasons()).comment(rating.getComment()).build())
					.collect(Collectors.toList()));

			result.setCursor(allratingRespond.getCursor());
			result.setContent(resultUnSelected);
			result.setTotal(resultUnSelected.size());
			
			return result;
		}
		
		// List of rating
		if (isSelected == true) {
			final PagedUserResponse<RatingSessionEntity> response = userClient.getAllRatingSessions(user_id,limit,cursor);

			final List<RatingSessionEntity> ratings = response.getEntities();
			if (ratings == null || ratings.isEmpty()) {
				result.setContent(Collections.emptyList());
				return result;
			}
			
			final Set<String> reviewerIds = ratings.stream().map(RatingSessionEntity::getReviewerId).collect(Collectors.toSet());
			final Map<String, String> userAvatars = ratingSessionService.getAvatars(reviewerIds).toBlocking().first();
			
			result.setContent(response.getEntities()
	                .stream()
	                .map(rating -> RatingSessionUserImpl
	                        .builder()
	                        .picture(getUserAvatar(userAvatars, rating.getReviewerId()))
	                        .id(rating.getUuid())
	                        .fullName(rating.getReviewerName())
	                        .reaction(rating.getReaction())
	                        .stars(rating.getStars())
	                        .sessionDate(rating.getSessionDate())
	                        .reasons(rating.getReasons())
	                        .comment(rating.getComment())
	                        .build())
	                .collect(Collectors.toList()));
			result.setTotal(result.getContent().size());
			result.setCursor(response.getCursor());
			return result;	

		} else {	// Click on button (+)

			do{
				LOG.info("------limit and cursor ------, {} and {} ",limit, cursor);
				
			final PagedUserResponse<RatingSessionEntity> allratingRespond = ratingSessionService.getRatingSessionByUserId(user_id, limit, cursor);

			final List<RatingSessionEntity> ratings = allratingRespond.getEntities();
			if ((resultUnSelected.isEmpty()) && (ratings == null || ratings.isEmpty())) {
				result.setContent(Collections.emptyList());
				return result;
			}

			if(ratings != null && !ratings.isEmpty()){
				
			final Set<String> reviewerIds = ratings.stream().map(RatingSessionEntity::getReviewerId).collect(Collectors.toSet());
			final Map<String, String> userAvatars = ratingSessionService.getAvatars(reviewerIds).toBlocking().first();
			
			
			resultUnSelected.addAll(ratings
            .stream().filter(rating -> !ratingSessionService.isRatingAdded(user_id, rating.getUuid()))
            .map(rating -> RatingSessionUserImpl
                    .builder()
                    .picture(getUserAvatar(userAvatars, rating.getReviewerId()))
                    .id(rating.getUuid())
                    .fullName(rating.getReviewerName())
                    .reaction(rating.getReaction())
                    .stars(rating.getStars())
                    .sessionDate(rating.getSessionDate())
                    .reasons(rating.getReasons())
                    .comment(rating.getComment())
                    .build())
            .collect(Collectors.toList()));
			
			}
			
			
			limit =MAX_LIMIT - resultUnSelected.size();
			cursor = allratingRespond.getCursor();
			result.setCursor(allratingRespond.getCursor());
	       
			if(allratingRespond.getCursor() == null || limit <= 0 || (ratings == null || ratings.isEmpty())){
				break;	
			}
			
			}while(true);
			result.setContent(resultUnSelected);
			result.setTotal(resultUnSelected.size());
            return result;
			
		}

	}
	
	
	@RequestMapping(value = "/rating/trainer/{user_id}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> deleteReasonSessionRating(@PathVariable("user_id") final String user_id,
			@RequestParam("rating_session_id") final String rating_session_id) {

		LOG.info("----- , {} ", user_id);
		LOG.info("-----, {} ", rating_session_id);

		try {
		//final List<RatingSessionEntity> ratingsessins = userClient.deleteRatingSessions(user_id, rating_session_id)
					//.getEntities();
		userClient.deleteRatingSessions(user_id, rating_session_id).getEntities();

		} catch (Exception e) {
			return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);

		}

		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);

	}

	private String getUserAvatar(final Map<String, String> userAvatars, final String userUuid) {
		final String avatar = userAvatars.get(userUuid);
		if (!StringUtils.isEmpty(avatar)) {
			return avatar;
		}
		return StringUtils.EMPTY;
	}

}
