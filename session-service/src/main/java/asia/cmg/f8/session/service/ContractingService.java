package asia.cmg.f8.session.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.session.dto.ContractUser;
import asia.cmg.f8.session.dto.ContractingHistory;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.TrainerClient;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.ContractingRepository;

/**
 * Created on 12/7/16.
 */
@Service
public class ContractingService {

	private static final String HISTORY = "history";
	private static final String USER_INFO = "userInfo";
	private static final String TRAIER_INFO = "trainerInfo";

	private final ContractingRepository contractingRepository;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	public static final Logger LOGGER = LoggerFactory.getLogger(ContractingService.class);

	// private final UserClient userClient;

	@Inject
	public ContractingService(final ContractingRepository contractingRepository,
			final NamedParameterJdbcTemplate jdbcTemplate) {
		this.contractingRepository = contractingRepository;
		this.jdbcTemplate = jdbcTemplate;
		// this.userClient = userClient;
	}

	/**
	 * Search all contracting of given PT.
	 *
	 * @param account
	 *            the current account. It's PT
	 * @param keyword
	 *            search keyword. It will search by name and username of user.
	 * @return List of Contracting EU of current PT
	 */
	public List<ContractUser> searchContractingEu(final Account account, final boolean allContracts,
			final String keyword) {
		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		if (allContracts) {
			return contractingRepository
					.searchAllContractingEuUserByKeyword(account.uuid(), validPackageStatus, keyword).stream()
					.map(this::parseContractUser).collect(Collectors.toList());
		}
		return contractingRepository.searchContractingEuUserByKeyword(account.uuid(), validPackageStatus, keyword)
				.stream().map(this::parseContractUser).collect(Collectors.toList());
	}

	/**
	 * Search all contracting of given EU.
	 *
	 * @param euUuid
	 *            the current account. It's EU.
	 * @param keyword
	 *            search keyword. It will search by name and username of user.
	 * @return List of Contracting PT of current EU
	 */
	public List<ContractUser> searchContractingPt(final String euUuid, final boolean allContracts,
			final String keyword) {
		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		if (allContracts) {
			return contractingRepository.searchAllContractingPtByKeyword(euUuid, validPackageStatus, keyword).stream()
					.map(this::parseContractUser).collect(Collectors.toList());
		}
		return contractingRepository.searchContractingPtByKeyword(euUuid, validPackageStatus, keyword).stream()
				.map(this::parseContractUser).collect(Collectors.toList());
	}

	/**
	 *
	 * @param account
	 *            the current account. It's EU.
	 * @param keyword
	 *            search keyword, searched by name or username of user.
	 * @param pageable
	 *            pagination information
	 * @return list of PTs excluding contracting PTs
	 */

	public PageResponse<ContractUser> searchPTsExcludingContracting(final Account account, final String keyword,
			final Pageable pageable, final boolean alltrainers) {
		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final Page<Object[]> resultSet = alltrainers ? contractingRepository.searchAllActivePTs(keyword, account.uuid(), pageable)
				: contractingRepository.searchPTsExcludingContracting(account.uuid(), validPackageStatus, keyword,
						pageable);
		if (Objects.isNull(resultSet) || resultSet.getContent().isEmpty()) {
			return new PageResponse<>();
		}

		final PageResponse<ContractUser> pagedResult = new PageResponse<>();
		pagedResult.setCount(resultSet.getTotalElements());
		pagedResult.setEntities(
				resultSet.getContent().stream().map(this::parsePTExcludingContracting).collect(Collectors.toList()));
		return pagedResult;
	}
	
	public PageResponse<ContractUser> searchPTsSameLevelExcludingContracting(final Account account, final String keyword, final String userLevel,
			final Pageable pageable, final boolean alltrainers) {
		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final Page<Object[]> resultSet = alltrainers ? contractingRepository.searchAllActivePTsSameLevel(keyword, account.uuid(), userLevel, pageable)
				: contractingRepository.searchPTsExcludingContractingAndSameLevel(account.uuid(), validPackageStatus, keyword, userLevel,pageable);
		if (Objects.isNull(resultSet) || resultSet.getContent().isEmpty()) {
			return new PageResponse<>();
		}

		final PageResponse<ContractUser> pagedResult = new PageResponse<>();
		pagedResult.setCount(resultSet.getTotalElements());
		pagedResult.setEntities(
				resultSet.getContent().stream().map(this::parsePTExcludingContracting).collect(Collectors.toList()));
		return pagedResult;
	}

	/**
	 * Search all client of given PT.
	 *
	 * @param account
	 *            the current account. It's PT.
	 * @param keyword
	 *            search keyword. It will search by name and username of user.
	 * @return List of Client of PT
	 */
	public PageResponse<TrainerClient> searchPtClient(final Account account, final String keyword,
			final Pageable pageable) {

		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final Page<Object[]> resultSet = contractingRepository.searchPtClientByKeyword(account.uuid(), keyword,
				validPackageStatus, pageable);
		if (Objects.isNull(resultSet) || resultSet.getContent().isEmpty()) {
			return new PageResponse<>();
		}

		final PageResponse<TrainerClient> pagedResult = new PageResponse<>();
		pagedResult.setCount(resultSet.getTotalElements());
		pagedResult.setEntities(resultSet.getContent().stream().map(orderEntity -> {
			final Timestamp expiredDateTimeStamp = (Timestamp) orderEntity[5];
			final Timestamp boughtDateTimeStamp = (Timestamp) orderEntity[6];

			final Long expiredDate = (expiredDateTimeStamp != null) ? expiredDateTimeStamp.toInstant().getEpochSecond()
					: null;
			final Long boughtDate = (boughtDateTimeStamp != null) ? boughtDateTimeStamp.toInstant().getEpochSecond()
					: null;

			return TrainerClient.builder()
					.userUuid((String) orderEntity[0])
					.picture((String) orderEntity[1])
					.name((String) orderEntity[2])
					.sessionBurned((Integer) orderEntity[3])
					.sessionNumber((Integer) orderEntity[4])
					.expiredDate(expiredDate)
					.boughtDate(boughtDate)
					.sessionConfirmed(((BigInteger) orderEntity[7])
					.intValue())
					.packageUuid((String) orderEntity[8])
					.build();
		}).collect(Collectors.toList()));
		return pagedResult;
	}

	private ContractUser parseContractUser(final Object[] entity) {
		Integer sessionBurned = null;
		Integer sessionNumber = null;
		Integer sessionConfirmed = null;
		Long expiredDate = null;
		Long boughtDate = null;

		sessionBurned = (Integer) entity[5];
		sessionNumber = (Integer) entity[6];
		sessionConfirmed = ((BigInteger) entity[9]).intValue();
		Integer trainingStyle = (Integer) entity[12];

		final Timestamp expiredDateTimeStamp = (Timestamp) entity[7];
		final Timestamp boughtDateTimeStamp = (Timestamp) entity[8];

		expiredDate = (expiredDateTimeStamp != null) ? expiredDateTimeStamp.toInstant().getEpochSecond() : null;
		boughtDate = (boughtDateTimeStamp != null) ? boughtDateTimeStamp.toInstant().getEpochSecond() : null;

		return ContractUser.builder()
				.userUuid((String) entity[0])
				.orderNumber((String) entity[1])
				.name((String) entity[2])
				.picture((String) entity[3])
				.username((String) entity[4])
				.sessionBurned(sessionBurned)
				.sessionNumber(sessionNumber)
				.expiredDate(expiredDate)
				.boughtDate(boughtDate)
				.sessionConfirmed(sessionConfirmed)
				.packageUuid((String) entity[10])
				.userLevel((String) entity[11])
				.trainingStyle(trainingStyle == 0 ? ProductTrainingStyle.OFFLINE.name() : ProductTrainingStyle.ONLINE.name())
				.build();
	}

	private ContractUser parsePTExcludingContracting(final Object[] entity) {
		int followed = ((BigInteger) entity[4]).intValue();
		return ContractUser.builder()
				.userUuid((String) entity[0])
				.name((String) entity[1])
				.picture((String) entity[2])
				.username((String) entity[3])
				.isFollowed(followed > 0)
				.userLevel((String)entity[5])
				.build();

	}

	//
	public Map<String, Object> getContractingHistoryByTrainer(final String userUuid, final String trainerUuid) {
		final List<String> historyShownStatus = SessionStatus.getHistoryShownStatus().stream().map(Enum::name)
				.collect(Collectors.toList());
		final List<Object[]> history = contractingRepository.getContractingHistory(userUuid, trainerUuid,
				historyShownStatus);

		if (history.isEmpty()) {
			return Collections.emptyMap();
		}

		return buildContractingHistoryResponse(history);
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private Map<String, Object> buildContractingHistoryResponse(final List<Object[]> history) {
		final Map<String, Object> result = new HashMap<>();

		ContractingHistory.BasicUserInfo userInfo = null;
		ContractingHistory.BasicTrainerInfo trainerInfo = null;
		final Map<String, ContractingHistory> historyMap = new HashMap<>();

		for (final Object[] row : history) {
			final String packageUuid = (String) row[1];

			ContractingHistory.Session session = null;
			if (!Objects.isNull(row[6])) {
				session = new ContractingHistory.Session();
				session.setUuid((String) row[6]);
				session.setStatus(SessionStatus
						.mappingClientCancelledSessionStatus(SessionStatus.valueOf((String) row[7])).name());
				session.setStartTime(((BigInteger) row[8]).longValue());
				session.setPackageUuid((String) row[9]);
				session.setTrainerName((String) row[13]);
			}

			if (historyMap.containsKey(packageUuid)) {
				if (!Objects.isNull(session)) {
					final ContractingHistory contractingHistory = historyMap.get(packageUuid);
					final List<ContractingHistory.Session> listOfSession = contractingHistory.getSessions();
					listOfSession.add(session);
					contractingHistory.setSessions(listOfSession);
				}
			} else {
				final List<ContractingHistory.Session> listOfSession = new ArrayList<>();

				if (!Objects.isNull(session)) {
					listOfSession.add(session);
				}

				final ContractingHistory contractingHistory = new ContractingHistory();
				contractingHistory.setSessions(listOfSession);

				final ContractingHistory.BasicOrderInfo orderInfo = new ContractingHistory.BasicOrderInfo();
				orderInfo.setUuid((String) row[0]);
				orderInfo.setOrderCode((String) row[2]);
				orderInfo.setNumberOfBurnedSession((Integer) row[3]);
				orderInfo.setNumberOfSession((Integer) row[4]);
				orderInfo.setNumberOfConfirmedSession((Integer) row[14]);
				if (!Objects.isNull(row[5])) {
					orderInfo.setExpiredDate(((BigInteger) row[5]).longValue());
				}
				orderInfo.setLimitOfDay((Integer) row[14]);
				contractingHistory.setUserOrder(orderInfo);

				historyMap.put(packageUuid, contractingHistory);
			}

			if (Objects.isNull(userInfo)) {
				userInfo = new ContractingHistory.BasicUserInfo();
				userInfo.setUuid((String) row[10]);
				userInfo.setAvatar((String) row[11]);
				userInfo.setFullName((String) row[12]);
			}

			if (Objects.isNull(trainerInfo)) {
				trainerInfo = new ContractingHistory.BasicTrainerInfo();
				trainerInfo.setUuid((String) row[16]);
				trainerInfo.setAvatar((String) row[15]);
				trainerInfo.setFullName((String) row[13]);
			}

		}

		result.put(HISTORY, historyMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
		result.put(USER_INFO, userInfo);
		result.put(TRAIER_INFO, trainerInfo);
		return result;
	}

	public List<String> getContractingOfUser(final String currentUserId, final String userRole,
			final List<String> users) {
		final Map<String, Object> queryParam = new HashMap<>(users.size());
		if (CommonConstant.EU_USER_TYPE.equals(userRole)) {
			queryParam.put("user_uuid", currentUserId);
			queryParam.put("contractings", users);
			return jdbcTemplate.queryForList(
					"select pt_uuid from session_session_packages "
							+ "where user_uuid = :user_uuid and pt_uuid in (:contractings) " + "group by pt_uuid",
					queryParam, String.class);
		} else if (CommonConstant.PT_USER_TYPE.equals(userRole)) {
			queryParam.put("pt_uuid", currentUserId);
			queryParam.put("contractings", users);
			return jdbcTemplate.queryForList(
					"select user_uuid from session_session_packages "
							+ "where pt_uuid = :pt_uuid and user_uuid in (:contractings) " + "group by user_uuid",
					queryParam, String.class);

		}
		return Collections.emptyList();
	}

}
