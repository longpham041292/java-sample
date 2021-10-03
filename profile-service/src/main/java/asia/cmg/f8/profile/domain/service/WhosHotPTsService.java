package asia.cmg.f8.profile.domain.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.database.entity.BasicUserEntity;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.repository.WhosHotRepository;
import asia.cmg.f8.profile.dto.WhosHotConfig;


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

	@Autowired
	private UserClient userClient;

	@Autowired
	private WhosHotRepository whosHotRepository;

	public List<WhosHotConfig> getWhosHotConfig() {

		final UserGridResponse<WhosHotConfig> response = userClient
				.getWhosHotConfig(String.format(WHOS_HOT_CONFIG_QUERY, "en"), ATTRIBUTE_LIMIT);

		if (response == null || response.isEmpty()) {
			return Collections.emptyList();
		}
		return response.getEntities();
	}

//	public List<WhosHotUsers> getWhosHotPtList(final String loginUserUuid, final String nearestClubCode,
//			final String clubUuid) {
//
//		List<WhosHotUsers> whosHotUsers = getWhosHotPtListWithManual(loginUserUuid, clubUuid);
//
//		List<String> checkedUserUuids = getCheckedUserUuIdsInClub(loginUserUuid, clubUuid);
//
//		List<WhosHotUsers> algorithmWhosHotUsers = new ArrayList<WhosHotUsers>();
//
//		algorithmWhosHotUsers = getWhosHotPtListWithAlgorithm(loginUserUuid, checkedUserUuids, nearestClubCode);
//
//		if (!CollectionUtils.isEmpty(algorithmWhosHotUsers)) {
//			if (CollectionUtils.isEmpty(whosHotUsers)) {
//				whosHotUsers = algorithmWhosHotUsers;
//			} else {
//				whosHotUsers.addAll(algorithmWhosHotUsers);
//				whosHotUsers = whosHotUsers.stream().distinct().collect(Collectors.toList());
//			}
//		}
//
//		return whosHotUsers;
//	}

	public List<String> getWhosHotPtListWithAlgorithm(final List<String> checkedTrainerUuIds, int limit) {

		final List<WhosHotConfig> whosHotConfigList = getWhosHotConfig();

		final WhosHotConfig likesConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_LIKES_WEIGHT);
		final WhosHotConfig postConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_POSTS_WEIGHT);
		final WhosHotConfig clientConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_CLIENTS_WEIGHT);
		final WhosHotConfig sessionBurnedConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_SESSIONS_BURNED_WEIGHT);

		final Pageable topDisplay = new PageRequest(0, limit);
		final List<Object> basicUsers = whosHotRepository.getTopWhosHotUsers(
									Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getWeight()),
									Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getMinimumConditions()),
									Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getWeight()),
									Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getMinimumConditions()),
									Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getWeight()),
									Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getMinimumConditions()),
									Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getWeight()),
									Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getMinimumConditions()),
									checkedTrainerUuIds, topDisplay);
		if (basicUsers.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < basicUsers.size(); i++) {
			Object[] record = (Object[])basicUsers.get(i);
			BasicUserEntity basicUserEntity = (BasicUserEntity)record[0];
			result.add(basicUserEntity.getUuid());
		}
		
		return result;
	}

//	private List<WhosHotUsers> getWhosHotPtListWithAlgorithmByClubCode(final String loginUserUuid,
//			final String nearestClubCode) {
//
//		final List<WhosHotConfig> whosHotConfigList = getWhosHotConfig();
//
//		int numberOfDisplay = 1;
//		final WhosHotConfig numberOfDisplayItem = getValueFromConfiguration(whosHotConfigList,
//				CONFIGURATION_NUMBER_OF_DISPLAY);
//		LOGGER.info("- > no of display -- > {}", numberOfDisplayItem);
//		if (numberOfDisplayItem != null) {
//			numberOfDisplay = Integer.parseInt(numberOfDisplayItem.getMinimumConditions());
//		}
//
//		final WhosHotConfig likesConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_LIKES_WEIGHT);
//		final WhosHotConfig postConfigItem = getValueFromConfiguration(whosHotConfigList, CONFIGURATION_POSTS_WEIGHT);
//		final WhosHotConfig clientConfigItem = getValueFromConfiguration(whosHotConfigList,
//				CONFIGURATION_CLIENTS_WEIGHT);
//		final WhosHotConfig sessionBurnedConfigItem = getValueFromConfiguration(whosHotConfigList,
//				CONFIGURATION_SESSIONS_BURNED_WEIGHT);
//
//		final Pageable topDisplay = new PageRequest(0, numberOfDisplay);
//		final List<BasicUserEntity> basicUsers = whosHotRepository.getTopWhosHotUsers(
//				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getWeight()),
//				Integer.valueOf(likesConfigItem == null ? "0" : likesConfigItem.getMinimumConditions()),
//				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getWeight()),
//				Integer.valueOf(postConfigItem == null ? "0" : postConfigItem.getMinimumConditions()),
//				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getWeight()),
//				Integer.valueOf(clientConfigItem == null ? "0" : clientConfigItem.getMinimumConditions()),
//				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getWeight()),
//				Integer.valueOf(sessionBurnedConfigItem == null ? "0" : sessionBurnedConfigItem.getMinimumConditions()),
//				nearestClubCode, topDisplay);
//		if (basicUsers.isEmpty()) {
//			return new ArrayList<WhosHotUsers>();
//		}
//
//		String queryByUuids = String.format("uuid='%s'", basicUsers.get(0).getUuid());
//		for (int i = 1; i < basicUsers.size(); i++) {
//			queryByUuids += String.format(" OR uuid='%s'", basicUsers.get(i).getUuid());
//		}
//
//		final UserGridResponse<UserEntity> result = userClient.searchUsersByQuery(queryByUuids);
//		if (result == null) {
//			return Collections.emptyList();
//		}
//		return result.getEntities().stream().map(users -> setWhosHotUsers(users, loginUserUuid))
//				.collect(Collectors.toList());
//
//	}

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
