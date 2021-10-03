/**
 * 
 */
package asia.cmg.f8.profile.domain.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import asia.cmg.f8.common.profile.FollowingConnectionEvent;
import asia.cmg.f8.common.spec.social.FollowingAction;
import asia.cmg.f8.profile.domain.client.CounterClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.dto.Counter;
import asia.cmg.f8.profile.util.SocialConstant;
import rx.Observable;

/**
 * @author khoa.bui
 *
 */
@Service
public class CounterService {
	private static final Logger LOG = LoggerFactory.getLogger(CounterService.class);
	private final UserClient userClient;
	private final CounterClient counterClient;

	@Inject
	public CounterService(final UserClient userClient, final CounterClient counterClient) {
		this.userClient = userClient;
		this.counterClient = counterClient;
	}
	
	public void randomNumberOfViewsOfPost(final String counterName, final int value) {
		try {
			counterClient.randomCounter(counterName, value);
		} catch (Exception e) {
			LOG.error("[randomNumberOfViewsOfPost] error detail: {}", e.getMessage());
		}
	}

	public void updateFollowersNumber(final FollowingConnectionEvent message) {

		final String followerId = message.getFollowerId().toString();

		final Optional<UserEntity> userEntity = Optional
				.ofNullable(userClient.getUser(followerId).getEntities().get(0));
		userEntity.ifPresent(user -> {

			final String counterName = CounterClient.buildFollowerCounterName(user);
			if (FollowingAction.FOLLOW.name().equalsIgnoreCase(message.getAction().toString())) {
				counterClient.increaseCounter(counterName);
				LOG.info("Increase the following counter for {}", counterName);
			} else {
				counterClient.decreaseCounter(counterName);
				LOG.info("Decrease the following counter for {}", counterName);
			}
		});
	}
	
	/**
     * Get number of post's comments, number of post's likes
     *
     * @param listPostUuid         list of post's uuids
     * @param listPostRequestUuid  list of post's request_uuids
     * @return Map with counter name as key and Integer value as numbers of count
     */
    public Observable<Map<String, Integer>> getNumberOfLikesCommentOfPost(
            final Collection<String> listPostUuid,
            final Collection<String> listPostRequestUuid) {

//        final int capacityOfListCounter = listPostUuid.size() + listPostUuid.size() + listPostRequestUuid.size();
        final Set<String> listCounter = new HashSet<>();

        listPostUuid
                .stream()
                .filter(Objects::nonNull)
                .forEach(postUuid -> {
                    final String likePostCounterName = SocialConstant.getLikeOfPostCounterName(postUuid);
                    listCounter.add(likePostCounterName);
                });
        
        listPostUuid
        		.stream()
        		.filter(Objects::nonNull)
        		.forEach(postUuid -> {
        			final String viewPostCounterName = SocialConstant.getViewOfPostCounterName(postUuid);
        			listCounter.add(viewPostCounterName);
        		});

        listPostRequestUuid
                .stream()
                .filter(Objects::nonNull)
                .forEach(postRequestUuid -> {
                    final String commentPostCounterName = SocialConstant.getCommentOfPostCounterName(postRequestUuid);
                    listCounter.add(commentPostCounterName);
                });

        return counterClient
                .getCounters(listCounter)
                .map(counters -> counters
                        .stream()
                        .collect(Collectors.toMap(Counter::getName,
                                       counter -> Integer.max(0, counter.getValue()))))
                .firstOrDefault(Collections.emptyMap());
    }
}
