package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.ActivityPostInfo;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.UserConnectionDto;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.ResetAutoFollowEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.ResetAutoFollowRepository;
import asia.cmg.f8.session.service.SessionService;

@RestController
public class AutoFollowAPI {

	private final SessionService sessionService;

	private final UserClient userClient;

	private final ResetAutoFollowRepository resetAutoFollowRepository;

	private final SessionProperties sessionProperties;

	private final BasicUserRepository basicUserRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoFollowAPI.class);

	public AutoFollowAPI(SessionService sessionService, UserClient userClient,
			ResetAutoFollowRepository resetAutoFollowRepository, BasicUserRepository basicUserRepository,
			SessionProperties sessionProperties) {
		this.sessionService = sessionService;
		this.userClient = userClient;
		this.resetAutoFollowRepository = resetAutoFollowRepository;
		this.basicUserRepository = basicUserRepository;
		this.sessionProperties = sessionProperties;
	}

	@RequestMapping(value = "/updateautofollow-reset-job", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	public void autoFollowResetFollowJob(@RequestParam("durationInHour") final int durationInHour) {
		LocalDateTime endJobTime = LocalDateTime.now().plusHours(durationInHour);
		
		LOGGER.info("Start autoFollowResetFollowJob endJobTime = {}", endJobTime);
		resetAutoFollowForMultiUser(sessionService.getAllUserFromSession(), endJobTime);
	}

	public void resetAutoFollowForMultiUser(List<UserConnectionDto> sessionEntities, final LocalDateTime endJobTime) {
		LOGGER.info("Start Number of sessions for reset: " + sessionEntities.size() + " " + LocalDateTime.now());

		UserGridResponse<UserEntity> diamondList = userClient
				.getUserByQuery("SELECT uuid WHERE activated=true AND level='Diamond'");
		List<String> diamondIds = diamondList.getEntities().stream().map(x -> x.getUuid()).distinct()
				.collect(Collectors.toList());
		List<String> defaultIds = new ArrayList<String>(Arrays.asList(sessionProperties.getF8UserUuids()));
		defaultIds.addAll(diamondIds);
		ExecutorService executor = Executors.newFixedThreadPool(sessionProperties.getNumOfThreadResetConnection());

		List<String> pts = sessionEntities.stream().map(x -> x.getPtUuid()).distinct().collect(Collectors.toList());
		List<String> eus = sessionEntities.stream().map(x -> x.getUserUuid()).distinct().collect(Collectors.toList());
		List<String> followings = this.basicUserRepository.getAllUserUuids();
		// for eu
		LOGGER.info("Number of sessions for reset for EUs: " + eus.size());
		for (String uuid : eus) {
			executor.execute(new AutoFollowReset(uuid, userClient, resetAutoFollowRepository, sessionEntities, true,
					followings, defaultIds, endJobTime));
		}

		// for pt
		LOGGER.info("Number of sessions for reset for PTs: " + pts.size());
		for (String uuid : pts) {
			executor.execute(new AutoFollowReset(uuid, userClient, resetAutoFollowRepository, sessionEntities, false,
					followings, defaultIds, endJobTime));
		}
		LOGGER.info("End Number of sessions for reset " + LocalDateTime.now());
	}
}

class AutoFollowReset implements Runnable {
	private final String uuid;
	private final UserClient userClient;
	private final ResetAutoFollowRepository resetAutoFollowRepository;
	private final List<UserConnectionDto> sessionEntities;
	private final boolean isEu;
	private final List<String> followings;
	private final List<String> defaultIds;
	private final LocalDateTime endJobTime;

	private static final String QUERY_FEED_DONOT_BELONG_TO_OWNER = "select uuid where verb = 'post' and not(owner_id='%s')";
	private static final int QUERY_FEED_PAGESIZE = 100;

	private static final Logger LOGGER = LoggerFactory.getLogger(AutoFollowReset.class);

	public AutoFollowReset(String uuid, UserClient userClient, ResetAutoFollowRepository resetAutoFollowRepository,
						   List<UserConnectionDto> sessionEntities, boolean isEu, List<String> followings, List<String> defaultIds, LocalDateTime endJobTime) {
		this.uuid = uuid;
		this.userClient = userClient;
		this.resetAutoFollowRepository = resetAutoFollowRepository;
		this.sessionEntities = sessionEntities;
		this.isEu = isEu;
		this.followings = followings;
		this.defaultIds = defaultIds;
		this.endJobTime = endJobTime;
	}

	public void run() {
		try {
			if (!continueRunning(endJobTime)) {
				LOGGER.info("Stopping job due to outside of allowable timeframe");
				return;
			}
			LOGGER.info("Start reset following for: " + uuid + " " + LocalDateTime.now());
			List<String> defaultList;
			if (this.isEu) {
				defaultList = this.sessionEntities.stream().filter(x -> x.getUserUuid().equals(uuid))
						.map(x -> x.getPtUuid()).distinct().collect(Collectors.toList());
			} else {
				defaultList = this.sessionEntities.stream().filter(x -> x.getPtUuid().equals(uuid))
						.map(x -> x.getUserUuid()).distinct().collect(Collectors.toList());
			}
			defaultList.addAll(defaultIds);
			followings.forEach(followingUuId -> {
				try {
					// exclude from default list
					if (!defaultList.contains(followingUuId)) {
						userClient.deleteFollowingConnectionBySecret(uuid, followingUuId);
					}
				} catch (Exception err) {
					LOGGER.warn("Reset error: " + uuid + ":" + followingUuId);
				}
			});

			removeFeedsOnUser(uuid);

			// log
			ResetAutoFollowEntity resetAutoFolowEntity = new ResetAutoFollowEntity();
			resetAutoFolowEntity.setUuid(UUID.randomUUID().toString());
			resetAutoFolowEntity.setUserUuid(uuid);
			resetAutoFolowEntity.setNumberOfFollowing(followings.size());
			resetAutoFollowRepository.saveAndFlush(resetAutoFolowEntity);
			LOGGER.info("End reset following for: " + uuid + " " + LocalDateTime.now());
		} catch (Exception err) {
			LOGGER.error("Reset error for user_uuid: " + uuid, err);
		}
	}

	private void removeFeedsOnUser(String userId) {
		String cursor = null;
		do {
			PageResponse<ActivityPostInfo> feeds = userClient.getFeeds(userId, cursor, String.format(QUERY_FEED_DONOT_BELONG_TO_OWNER, userId),
					QUERY_FEED_PAGESIZE);
			for (ActivityPostInfo activityPostInfo : feeds.getEntities()) {
				userClient.deleteFeed(userId, activityPostInfo.getPostUuid());
			}
			cursor = feeds.getCursor();
		} while (cursor != null);
	}
	
	private boolean continueRunning(final LocalDateTime endJobTime) {
		if (LocalDateTime.now().isAfter(endJobTime)) {
			return false;
		}
		return true;
	}
}
