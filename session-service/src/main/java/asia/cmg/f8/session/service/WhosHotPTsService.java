package asia.cmg.f8.session.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.spec.user.Media;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.client.CheckInUserClient;
import asia.cmg.f8.session.client.FollowUserClient;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.dto.ActivityPostInfo;
import asia.cmg.f8.session.dto.CheckInUser;
import asia.cmg.f8.session.dto.Profile;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.dto.WhosHotUsers;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.WhosHotConfig;
import asia.cmg.f8.session.entity.WhosHotEntity;
import asia.cmg.f8.session.repository.TrainerUserRepository;
import asia.cmg.f8.session.repository.WhosHotRepository;

@Service
public class WhosHotPTsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WhosHotPTsService.class);

	private static final String WHOS_HOT_CONFIG_QUERY = "language='%s' And group ='whos_hot' order by ordinal asc";
	private static final String LIKE_COUNTER_PATTERN = "%s_like_counter";
	private static final String GET_POSTS_QUERY = "select uuid where verb = 'post' and owner_id=%s and created >= %s";
	private static final int ATTRIBUTE_LIMIT = 1000;
	private static final String CONFIGURATION_LIKES_WEIGHT = "NumberOfLikes";
	private static final String CONFIGURATION_POSTS_WEIGHT = "NumberOfPosts";
	private static final String CONFIGURATION_CLIENTS_WEIGHT = "NumberOfClients";
	private static final String CONFIGURATION_SESSIONS_BURNED_WEIGHT = "NumberOfSessionBurned";
	private static final String CONFIGURATION_NUMBER_OF_DISPLAY = "NumberOfTrainersToDisplay";
	private static final int QUERIED_LAST_DAYS = 10;
	private static final int LIMIT = 1000;

	private static final String QUERY_PT_USER_CHECK_IN_CLUB = "SELECT * WHERE expired_time > %1$s "
			+ "AND club_id = '%2$s' " + "AND not (user_id = '%3$s') " + "AND user_type = 'pt' "
			+ "ORDER BY expired_time desc";

	private final List<String> sessionBurnedStatusList = Arrays.asList("COMPLETED", "BURNED", "EU_CANCELLED");;

	@Autowired
	private UserClient userClient;

	@Autowired
	private CheckInUserClient checkInUserClient;

	@Autowired
	private TrainerUserRepository trainerUserRepository;

	@Autowired
	private WhosHotRepository whosHotRepository;

	@Autowired
	private FollowUserClient followUserClient;

	public List<WhosHotConfig> getWhosHotConfig() {

		final UserGridResponse<WhosHotConfig> response = userClient
				.getWhosHotConfig(String.format(WHOS_HOT_CONFIG_QUERY, "en"), ATTRIBUTE_LIMIT);

		if (response == null || response.isEmpty()) {
			return Collections.emptyList();
		}
		return response.getEntities();
	}

	public List<WhosHotUsers> getWhosHotPtList(final String loginUserUuid, final String nearestClubCode,
			final String clubUuid) {

		List<WhosHotUsers> whosHotUsers = getWhosHotPtListWithManual(loginUserUuid, clubUuid);

		List<String> checkedUserUuids = getCheckedUserUuIdsInClub(loginUserUuid, clubUuid);

		List<WhosHotUsers> algorithmWhosHotUsers = new ArrayList<WhosHotUsers>();

		algorithmWhosHotUsers = getWhosHotPtListWithAlgorithm(loginUserUuid, checkedUserUuids, nearestClubCode);

		if (!CollectionUtils.isEmpty(algorithmWhosHotUsers)) {
			if (CollectionUtils.isEmpty(whosHotUsers)) {
				whosHotUsers = algorithmWhosHotUsers;
			} else {
				whosHotUsers.addAll(algorithmWhosHotUsers);
				whosHotUsers = whosHotUsers.stream().distinct().collect(Collectors.toList());
			}
		}

		return whosHotUsers;
	}

	private List<WhosHotUsers> getWhosHotPtListWithAlgorithm(final String loginUserUuid,
			final List<String> checkedUserUuIds, String clubCode) {

		final List<WhosHotConfig> whosHotConfigList = getWhosHotConfig();

		int numberOfDisplay = 1;
		final WhosHotConfig numberOfDisplayItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_NUMBER_OF_DISPLAY);
		LOGGER.info("- > no of display -- > {}", numberOfDisplayItem);
		if (numberOfDisplayItem != null) {
			numberOfDisplay = Integer.parseInt(numberOfDisplayItem.getMinimumConditions());
		}

		final WhosHotConfig likesConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_LIKES_WEIGHT);
		final WhosHotConfig postConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_POSTS_WEIGHT);
		final WhosHotConfig clientConfigItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_CLIENTS_WEIGHT);
		final WhosHotConfig sessionBurnedConfigItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_SESSIONS_BURNED_WEIGHT);

		final Pageable topDisplay = new PageRequest(0, numberOfDisplay);
		final List<BasicUserEntity> basicUsers = whosHotRepository.getTopWhosHotUsers(
				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getWeight()),
				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getMinimumConditions()),
				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getWeight()),
				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getMinimumConditions()),
				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getWeight()),
				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getMinimumConditions()),
				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getWeight()),
				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getMinimumConditions()),
				checkedUserUuIds, clubCode, topDisplay);
		if (basicUsers.isEmpty()) {
			return new ArrayList<WhosHotUsers>();
		}

		String queryByUuids = String.format("uuid='%s'", basicUsers.get(0).getUuid());
		for (int i = 1; i < basicUsers.size(); i++) {
			queryByUuids += String.format(" OR uuid='%s'", basicUsers.get(i).getUuid());
		}

		final UserGridResponse<UserEntity> result = userClient.searchUsersByQuery(queryByUuids);
		if (result == null) {
			return Collections.emptyList();
		}
		return result.getEntities().stream().map(users -> setWhosHotUsers(users, loginUserUuid))
				.collect(Collectors.toList());

	}

	private List<WhosHotUsers> getWhosHotPtListWithAlgorithmByClubCode(final String loginUserUuid,
			final String nearestClubCode) {

		final List<WhosHotConfig> whosHotConfigList = getWhosHotConfig();

		int numberOfDisplay = 1;
		final WhosHotConfig numberOfDisplayItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_NUMBER_OF_DISPLAY);
		LOGGER.info("- > no of display -- > {}", numberOfDisplayItem);
		if (numberOfDisplayItem != null) {
			numberOfDisplay = Integer.parseInt(numberOfDisplayItem.getMinimumConditions());
		}

		final WhosHotConfig likesConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_LIKES_WEIGHT);
		final WhosHotConfig postConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_POSTS_WEIGHT);
		final WhosHotConfig clientConfigItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_CLIENTS_WEIGHT);
		final WhosHotConfig sessionBurnedConfigItem = getValueFromConfiguration(whosHotConfigList,
				CONFIGURATION_SESSIONS_BURNED_WEIGHT);

		final Pageable topDisplay = new PageRequest(0, numberOfDisplay);
		final List<BasicUserEntity> basicUsers = whosHotRepository.getTopWhosHotUsers(
				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getWeight()),
				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getMinimumConditions()),
				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getWeight()),
				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getMinimumConditions()),
				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getWeight()),
				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getMinimumConditions()),
				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getWeight()),
				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getMinimumConditions()),
				nearestClubCode, topDisplay);
		if (basicUsers.isEmpty()) {
			return new ArrayList<WhosHotUsers>();
		}

		String queryByUuids = String.format("uuid='%s'", basicUsers.get(0).getUuid());
		for (int i = 1; i < basicUsers.size(); i++) {
			queryByUuids += String.format(" OR uuid='%s'", basicUsers.get(i).getUuid());
		}

		final UserGridResponse<UserEntity> result = userClient.searchUsersByQuery(queryByUuids);
		if (result == null) {
			return Collections.emptyList();
		}
		return result.getEntities().stream().map(users -> setWhosHotUsers(users, loginUserUuid))
				.collect(Collectors.toList());

	}

	public List<String> getCheckedUserUuIdsInClub(final String loginUserUuid, final String clubId) {

		final String userCheckUserInClubQuery = String.format(QUERY_PT_USER_CHECK_IN_CLUB,
				ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now()), clubId, loginUserUuid);
		final UserGridResponse<CheckInUser> checkInUsers = checkInUserClient
				.getAllUserCheckInClub(userCheckUserInClubQuery);

		final List<String> result = new ArrayList<String>();
		if (Objects.isNull(checkInUsers) || CollectionUtils.isEmpty(checkInUsers.getEntities())) {
			LOGGER.warn("Usergrid went wrong or no data while executing query: " + userCheckUserInClubQuery);
			result.add("No Checked User");
			return result;
		}

		for (int i = 0; i < checkInUsers.getEntities().size(); i++) {
			result.add(checkInUsers.getEntities().get(i).getUserId());
		}

		return result;

	}

	public List<WhosHotUsers> getCheckedUserInClub(final String loginUserUuid, final String clubId) {

		final String userCheckUserInClubQuery = String.format(QUERY_PT_USER_CHECK_IN_CLUB,
				ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now()), clubId, loginUserUuid);
		final UserGridResponse<CheckInUser> checkInUsers = checkInUserClient
				.getAllUserCheckInClub(userCheckUserInClubQuery);

		if (Objects.isNull(checkInUsers) || CollectionUtils.isEmpty(checkInUsers.getEntities())) {
			LOGGER.warn("Usergrid went wrong or no data while executing query: " + userCheckUserInClubQuery);
			return Collections.emptyList();
		}

		String queryByUuids = String.format("uuid='%s'", checkInUsers.getEntities().get(0).getUserId());
		for (int i = 1; i < checkInUsers.getEntities().size(); i++) {
			queryByUuids += String.format(" OR uuid='%s'", checkInUsers.getEntities().get(i).getUserId());
		}
		final UserGridResponse<UserEntity> result = userClient.searchUsersByQuery(queryByUuids);
		if (result == null) {
			return Collections.emptyList();
		}
		return result.getEntities().stream().map(users -> setWhosHotUsers(users, loginUserUuid))
				.collect(Collectors.toList());
	}

	private List<WhosHotUsers> getWhosHotPtListWithManual(final String loginUserUuid, final String clubUuid) {

		try {
			final UserGridResponse<UserEntity> whosHotPtList = userClient.searchUsersFromClub(clubUuid, LIMIT, null);

			if (whosHotPtList != null && whosHotPtList.getEntities() != null
					&& !whosHotPtList.getEntities().isEmpty()) {

				return whosHotPtList.getEntities().stream().map(users -> setWhosHotUsers(users, loginUserUuid))
						.collect(Collectors.toList());
			}

		} catch (Exception ex) {
			LOGGER.error("Error to search searchUsersFromClub {}", ex);
			return Collections.<WhosHotUsers>emptyList();
		}
		return Collections.<WhosHotUsers>emptyList();
	}

	private WhosHotUsers setWhosHotUsers(final UserEntity userEntity, final String loginUserUuid) {
		boolean followStatus = false;

		if (!loginUserUuid.equalsIgnoreCase(userEntity.getUuid())) {
			followStatus = followUserClient.checkFollowConnection(loginUserUuid, userEntity.getUuid()).getContent();
		}

		String coverPhoto = userEntity.getPicture();
		final Profile profile = userEntity.getProfile();
		if (profile != null && CollectionUtils.isNotEmpty(profile.covers())) {
			for (final Iterator<Media> media = userEntity.getProfile().covers().iterator(); media.hasNext();) {
				final Media m = media.next();
				if (StringUtils.isNotEmpty(m.getThumbnailUrl())) {
					coverPhoto = m.getThumbnailUrl();
					break;
				}
				if (StringUtils.isNotEmpty(m.getUrl())) {
					coverPhoto = m.getUrl();
					break;
				}
			}
		}
		return WhosHotUsers.builder().uuid(userEntity.getUuid()).name(userEntity.getName()).avatar(coverPhoto)
				.level(userEntity.getLevel()).followStatus(followStatus).build();

	}

	public void runWhosHotPTAlgorithm() {

		final List<BasicUserEntity> activePTsList = trainerUserRepository.findAllActivePT();

		if (activePTsList.isEmpty()) {
			LOGGER.debug("runWhosHotPTAlgorithm - no active PT to run");
			return;
		}

		activePTsList.forEach(activePT -> {
			LOGGER.debug("runWhosHotPTAlgorithm - PT: {}", activePT.getUuid());
			final HashMap<String, Integer> ptNumbers = getWhosHotPoint(activePT.getUuid());
			updatePointToDB(activePT.getUuid(), ptNumbers);
		});
	}

	private HashMap<String, Integer> getWhosHotPoint(final String ptUuid) {
		final HashMap<String, Integer> ptNumbers = new HashMap<String, Integer>();
		try {
			final List<String> postList = getPostList(ptUuid);
			final int numberOfPosts = postList == null ? 0 : postList.size();
			final int numberOfLikes = postList == null ? 0 : getNumberOfLikes(postList);

			final int numberOfSessionBurned = getNumberOfSessionBurned(ptUuid);
			final int numberOfClients = getNumberOfActiveClientsOfPT(ptUuid);

			LOGGER.debug("ptUuid: " + ptUuid + " - numberOfPosts: " + numberOfPosts + " - numberOfLikes: "
					+ numberOfLikes + " - numberOfClients: " + numberOfClients + " - numberOfSessionBurned: "
					+ numberOfSessionBurned);

			ptNumbers.put(CONFIGURATION_LIKES_WEIGHT, numberOfLikes);
			ptNumbers.put(CONFIGURATION_CLIENTS_WEIGHT, numberOfClients);
			ptNumbers.put(CONFIGURATION_SESSIONS_BURNED_WEIGHT, numberOfSessionBurned);
			ptNumbers.put(CONFIGURATION_POSTS_WEIGHT, numberOfPosts);
		} catch (Exception ex) {
			LOGGER.error("getWhosHotPoint - ptUuid: " + ptUuid, ex);
		}
		return ptNumbers;
	}

	private void updatePointToDB(final String ptUuid, final HashMap<String, Integer> ptNumbers) {
		try {
			final List<WhosHotEntity> list = whosHotRepository.findByPTUuid(ptUuid);

			WhosHotEntity entity;
			if (list.isEmpty()) {
				entity = new WhosHotEntity();
				entity.setPtUuid(ptUuid);
			} else {
				entity = list.get(0);
			}

			entity.setNumberOfSessionBurned(ptNumbers.get(CONFIGURATION_SESSIONS_BURNED_WEIGHT));
			entity.setNumberOfClients(ptNumbers.get(CONFIGURATION_CLIENTS_WEIGHT));
			entity.setNumberOfPosts(ptNumbers.get(CONFIGURATION_POSTS_WEIGHT));
			entity.setNumberOfLikes(ptNumbers.get(CONFIGURATION_LIKES_WEIGHT));
			entity.setUpdatedDate(LocalDateTime.now());

			whosHotRepository.save(entity);

		} catch (Exception ex) {
			LOGGER.error("updatePointToDB - ptUuid: " + ptUuid, ex);
		}
	}

	private List<String> getPostList(final String ptUuid) {
		final long fromDate = LocalDateTime.now().minusDays(QUERIED_LAST_DAYS).atZone(ZoneId.systemDefault())
				.toInstant().toEpochMilli() / 1000L;
		final String query = String.format(GET_POSTS_QUERY, ptUuid, fromDate);

		final UserGridResponse<ActivityPostInfo> countInfoResponse = userClient.getActivitiesOfPT(query);

		if (countInfoResponse == null || countInfoResponse.isEmpty()) {
			return null;
		}
		return countInfoResponse.getEntities().stream().map(ActivityPostInfo::getPostUuid).collect(Collectors.toList());
	}

	private int getNumberOfLikes(final List<String> postList) {
		final LongAdder totalLikes = new LongAdder();
		postList.forEach(postId -> {
			final String likePostCounterName = String.format(LIKE_COUNTER_PATTERN, postId);
			final Long likesPostCounter = trainerUserRepository.getLikesCounter(likePostCounterName);
			if (likesPostCounter != null) {
				totalLikes.add(likesPostCounter);
			}
		});

		return totalLikes.intValue();
	}

	private int getNumberOfSessionBurned(final String ptUuid) {
		final long fromDate = LocalDateTime.now().minusDays(QUERIED_LAST_DAYS).atZone(ZoneId.systemDefault())
				.toInstant().toEpochMilli() / 1000L;
		final Long numberOfSessionBurned = trainerUserRepository.getNumberOfSessionBurnedOfPT(ptUuid,
				sessionBurnedStatusList, fromDate);
		if (numberOfSessionBurned != null) {
			return numberOfSessionBurned.intValue();
		}
		return 0;
	}

	private int getNumberOfActiveClientsOfPT(final String ptUuid) {
		final Long numberOfClients = trainerUserRepository.getNumberOfActiveClientsOfPT(ptUuid);
		if (numberOfClients != null) {
			return numberOfClients.intValue();
		}
		return 0;
	}

	private WhosHotConfig getValueFromConfiguration(final List<WhosHotConfig> configurationList, final String key) {
		for (final WhosHotConfig item : configurationList) {
			if (item.getKey().trim().equals(key) && Boolean.valueOf(item.getActivated()) == true) {
				return item;
			}
		}
		return null;
	}

	public List<WhosHotConfig> searchWhosHotConfig(final String language) {

		final UserGridResponse<WhosHotConfig> response = userClient
				.getWhosHotConfig(String.format(WHOS_HOT_CONFIG_QUERY, language), ATTRIBUTE_LIMIT);

		if (response == null || response.isEmpty()) {
			return Collections.emptyList();
		}
		return response.getEntities();
	}

}
