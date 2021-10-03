package asia.cmg.f8.session.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.common.util.NumberUtils;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.BasicUserOrder;
import asia.cmg.f8.session.dto.ClientSessionInfo;
import asia.cmg.f8.session.dto.PTSessionInfo;
import asia.cmg.f8.session.dto.PtExport;
import asia.cmg.f8.session.dto.PtListingInfo;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.MVSessionStatsDailyRepository;
import asia.cmg.f8.session.repository.TrainerUserRepository;
import asia.cmg.f8.session.utils.CommandTextValidator;
import asia.cmg.f8.session.utils.ReportUtils;

@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class UserService {

	private static final String DD_LLLL_YYYY = "dd/LLLL/yyyy";

	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final BasicUserRepository userRepository;
	private final MVSessionStatsDailyRepository sessionStatsDailyRepository;
	private final TrainerUserRepository trainerRepository;

	private final SessionProperties sessionProperties;

	public UserService(final BasicUserRepository userRepository, final SessionProperties sessionProperties,
			final TrainerUserRepository trainerRepository,
			final MVSessionStatsDailyRepository sessionStatsDailyRepository) {
		this.userRepository = userRepository;
		this.sessionProperties = sessionProperties;
		this.trainerRepository = trainerRepository;
		this.sessionStatsDailyRepository = sessionStatsDailyRepository;
	}

	@Autowired
	private UserClient userClient;

	@Cacheable(cacheNames = "userLocaleCache", key = "#uuid")
	public Locale getLocale(final String uuid) {

		Locale locale = Locale.ENGLISH;
		final UserGridResponse<BasicUserInfo> userInfo = userClient.findUserLanguage(uuid);

		if (userInfo != null && userInfo.getEntities() != null && !userInfo.getEntities().isEmpty()) {
			final BasicUserInfo user = userInfo.getEntities().iterator().next();
			final String language = user.getLanguage();
			if (language != null) {
				locale = new Locale(language);

				LOG.info("Loaded \"{}\" locale of user {}", locale, uuid);
			}
		}
		return locale;
	}

	@Transactional(readOnly = true)
	public List<BasicUserOrder> getAllMembers(final String keyword, final Pageable pageable) {
		final List<String> validSessionPackage = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		// LOG.info("getAllMembers - pageable {}", pageable.toString());
		final List<Object[]> page = userRepository.listUserWithOrder(CommandTextValidator.getSafeKeyword(keyword),
				validSessionPackage, pageable);

		return page.stream().map(this::parseBasicUserOrderWithPackageInfo).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<BasicUserOrder> getAllMembersByTimeRange(final String keyword, final Pageable pageable, final LocalDateTime fromDate, final LocalDateTime toDate) {
		final List<String> validSessionPackage = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
//		LOG.info("getAllMembers - pageable {}", pageable.toString());
		final List<Object[]> page = userRepository.listUserByTimeRange(CommandTextValidator.getSafeKeyword(keyword),
				validSessionPackage, fromDate, toDate, pageable);

		
		return page.stream().map(this::parseBasicUserOrderWithPackageInfo).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<BasicUserOrder> getAllMembers(final LocalDateTime fromDate, final LocalDateTime toDate) {
		final List<String> validSessionPackage = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final List<Object[]> page = userRepository.listUserWithOrder(validSessionPackage, fromDate, toDate);

		return page.stream().map(this::parseBasicUserOrder).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PtListingInfo> getAllTrainers(final int year, final Pageable pageable) {
		final List<Object[]> page = trainerRepository.listTrainers(year, pageable);
		LOG.info("getAllTrainers - " + pageable.toString());
		return page.stream().map(this::parsePtListingInfo).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<PtListingInfo> getAllTrainers(final LocalDateTime fromDate, final LocalDateTime toDate, final Pageable pageable) {
		final List<Object[]> page = trainerRepository.listTrainers(fromDate, toDate, pageable);
		LOG.info("getAllTrainers - " + pageable.toString());
		return page.stream().map(this::parsePtListingInfo).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PtExport> exportTrainers(final int year) {
		final List<Object[]> page = trainerRepository.allTrainers(year);

		return page.stream().map(this::parsePtExport).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PtListingInfo> searchTrainers(final int year, final String keyword, final Pageable pageable) {
		final List<Object[]> page = trainerRepository.searchTrainers(year, CommandTextValidator.getSafeKeyword(keyword),
				pageable);

		return page.stream().map(this::parsePtListingInfo).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<PtListingInfo> searchTrainers(final LocalDateTime fromDate, final LocalDateTime toDate, final String keyword, final Pageable pageable) {
		final List<Object[]> page = trainerRepository.searchTrainers(fromDate, toDate, CommandTextValidator.getSafeKeyword(keyword),
				pageable);

		return page.stream().map(this::parsePtListingInfo).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ClientSessionInfo> getClientOfPt(final String userId, final String keyword, final String language,
			final Pageable pageable) {
		final List<Object[]> page = userRepository.listClientOfPtWithOrder(userId, keyword, pageable);
		return page.stream()
				.map(content -> ReportUtils.parseClientSessionInfo(content, language, sessionProperties.getCurrency()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ClientSessionInfo> getClientOfPtWithActiveOrder(final String userId, final String keyword,
			final String language, final Pageable pageable) {
		final List<String> validPackageStatus = SessionPackageStatus.getAdminValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final List<Object[]> page = userRepository.listClientOfPtWithActiveOrder(userId, keyword, validPackageStatus,
				pageable);
		return page.stream()
				.map(content -> ReportUtils.parseClientSessionInfo(content, language, sessionProperties.getCurrency()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PTSessionInfo> getPTsOfUser(final String userUuid, final String keyword, final String language,
			final Pageable pageable) {
		final List<Object[]> page = userRepository.listPTsOfUser(userUuid, keyword, pageable);

		return page.stream()
				.map(content -> ReportUtils.parsePTSessionInfo(content, language, sessionProperties.getCurrency()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PTSessionInfo> getPTsOfUserWithActiveOrder(final String userUuid, final String keyword,
			final String language, final Pageable pageable) {
		final List<String> validSessionPackage = SessionPackageStatus.getAdminValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final List<Object[]> page = userRepository.listPTsOfUserWithActiveOrder(userUuid, keyword, validSessionPackage,
				pageable);
		return page.stream()
				.map(content -> ReportUtils.parsePTSessionInfo(content, language, sessionProperties.getCurrency()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<StatisticResponse> getUserSessionStatistic(final String userUuid, final TimeInfo timeRange) {
		LOG.info("Stats for a user from {}, middle {}, to {}", timeRange.getStartTime(), timeRange.getMiddleTime(),
				timeRange.getEndTime());

		final List<String> statuses = Arrays.asList(SessionStatus.COMPLETED.name(), SessionStatus.BURNED.name(),
				SessionStatus.EU_CANCELLED.name());

		final List<Object[]> rawStats = userRepository.calculateSessionOfUserByTimeRange(userUuid,
				timeRange.getStartTime(), timeRange.getMiddleTime(), timeRange.getEndTime(), statuses);

		final StatisticResponse completedResponse = new StatisticResponse(SessionStatus.COMPLETED.name(), 0, null);
		final StatisticResponse burnedResponse = new StatisticResponse(SessionStatus.BURNED.name(), 0, null);

		int prevBurn = 0;
		int currentBurn = 0;

		for (final Object[] row : rawStats) {
			final SessionStatus statusEnum = SessionStatus.valueOf(String.valueOf(row[0]));

			final BigInteger previousBi = (BigInteger) row[1];
			final BigInteger currentBi = (BigInteger) row[2];
			final int previous = previousBi.intValue();
			final int current = currentBi.intValue();

			if (SessionStatus.COMPLETED.compareTo(statusEnum) == 0) {
				completedResponse.setTotal(current);
				completedResponse.setTrend(calculateTrend(previous, current));
				// TODO need to review
			} else if (SessionStatus.BURNED.compareTo(statusEnum) == 0
					|| SessionStatus.EU_CANCELLED.compareTo(statusEnum) == 0) {
				prevBurn += previous;
				currentBurn += current;
			}
		}

		burnedResponse.setTotal(currentBurn);
		burnedResponse.setTrend(calculateTrend(prevBurn, currentBurn));

		final List<StatisticResponse> statsResponse = new ArrayList<>();
		statsResponse.add(completedResponse);
		statsResponse.add(burnedResponse);
		return statsResponse;
	}

	@Transactional(readOnly = true)
	public StatisticResponse getUserSessionConfirmStatistic(final String userUuid, final TimeInfo timeRange) {
		final List<Object[]> rawStats = sessionStatsDailyRepository.statsConfirmedSessionInTimeRange(userUuid,
				timeRange.getStartTime(), timeRange.getMiddleTime(), timeRange.getEndTime());

		LOG.info("Raw data {}", rawStats.get(0));

		final StatisticResponse confirmedResponse = new StatisticResponse(SessionStatus.CONFIRMED.name(), 0, null);

		final Object[] row = rawStats.get(0);
		final BigInteger previousBi = (BigInteger) row[0];
		final BigInteger currentBi = (BigInteger) row[1];
		final int previous = previousBi.intValue();
		final int current = currentBi.intValue();

		confirmedResponse.setTotal(current);
		confirmedResponse.setTrend(calculateTrend(previous, current));

		final List<StatisticResponse> statsResponse = new ArrayList<>();
		statsResponse.add(confirmedResponse);
		return confirmedResponse;
	}

	@Transactional(readOnly = true)
	public List<Map<String, List<String>>> getMemberPurchaseHitory(final String userUuid, final TimeRange timeRange,
			final TimeInfo timeInfo, final String language) {
		final List<Object[]> purchaseResponse = userRepository.getCompletedOrdersOfUserInRange(userUuid,
				timeInfo.getMiddleTime(), timeInfo.getEndTime());

		final Map<String, BigDecimal> purchaseHistory = ReportUtils.initPurchaseHistory(timeRange);
		for (final Object[] col : purchaseResponse) {
			buildPurchaseRecord(col, purchaseHistory, timeRange);
		}

		return purchaseHistory.entrySet().stream()
				.map(entry -> Collections.singletonMap(entry.getKey(),
						Arrays.asList(entry.getValue().toString(), CurrencyUtils
								.formatCurrency(sessionProperties.getCurrency(), language, entry.getValue()))))
				.collect(Collectors.toList());
	}

	public List<String> getAllUserUuids() {
		return userRepository.getAllUserUuids();
	}

	public Optional<BasicUserEntity> findOneByUuid(final String uuid) {
		return userRepository.findOneByUuid(uuid);
	}

	private void buildPurchaseRecord(final Object[] col, final Map<String, BigDecimal> purchaseHistory,
			final TimeRange timeRange) {
		final Timestamp timeStamp = (Timestamp) col[0];
		final Double price = (Double) col[1];
		final String key = ReportUtils.buildKeyPurchaseHistory(timeRange, timeStamp);

		final BigDecimal currentAmount = purchaseHistory.get(key);
		if (currentAmount != null) {
			purchaseHistory.put(key, currentAmount.add(new BigDecimal(String.valueOf(price))));
		}
	}

	private BasicUserOrder parseBasicUserOrder(final Object[] content) {
		final BasicUserOrder user = new BasicUserOrder();
		user.setUuid((String) content[0]);
		user.setName((String) content[1]);
		user.setCity((String) content[2]);
		user.setCountry((String) content[3]);
		final boolean activated = content[4] != null && (Boolean) content[4];
		user.setActivated(activated);

		user.setSessionBurned((Integer) content[5]);
		user.setSessionNumber((Integer) content[6]);
		user.setSessionConfirmed(((BigInteger) content[13]).intValue());
		final Long expireDate = content[7] == null ? null : ((Timestamp) content[7]).getTime();

		user.setExpireDate(expireDate);
		if (expireDate != null) {

			final LocalDateTime dateTime = LocalDateTime.parse(method(content[7].toString()),
					DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_LLLL_YYYY);
			final LocalDate localDate = dateTime.toLocalDate();

			// user.setDisplayExpiredDate(DateUtils.formatDateTime(user.getExpireDate(),
			// sessionProperties.getDateTimeFormat()));
			user.setDisplayExpiredDate(localDate.format(formatter));

		}

		user.setEmail((String) content[8]);

		final Long joinDate = content[9] == null ? null : ((Timestamp) content[9]).getTime();
		user.setJoinDate(joinDate);
		if (joinDate != null) {

			final LocalDateTime dateTime = LocalDateTime.parse(method(content[9].toString()),
					DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_LLLL_YYYY);
			final LocalDate localDate = dateTime.toLocalDate();
			final LocalTime localTime = dateTime.toLocalTime();

			// user.setDisplayJoinDate(DateUtils.formatDateTime(user.getJoinDate(),
			// sessionProperties.getDateTimeFormat()));
			user.setDisplayJoinDate(localDate.format(formatter));
			user.setJoinTime(localTime.toString());
		}
		user.setUserName((String) content[10]);
		user.setMemberCode(content[11] != null ? (String) content[11] : "");
		user.setPhone((String)content[12]);

		return user;
	}

	private BasicUserOrder parseBasicUserOrderWithPackageInfo(final Object[] content) {
		final BasicUserOrder user = new BasicUserOrder();
		user.setUuid((String) content[0]);
		user.setAvatar((String) content[1]);
		user.setName((String) content[2]);
		user.setCity((String) content[3]);
		user.setCountry((String) content[4]);
		final boolean activated = content[5] != null && (Boolean) content[5];
		user.setActivated(activated);

		user.setSessionBurned((Integer) content[6]);
		user.setSessionNumber((Integer) content[7]);
		final Long timeStamp = content[8] == null ? null : ((Timestamp) content[8]).getTime();
		user.setExpireDate(timeStamp);
		user.setDisplayExpiredDate(
				DateUtils.formatDateTime(user.getExpireDate(), sessionProperties.getDateTimeFormat()));

		// Check user have order and package.
		if (!Objects.isNull(content[9])) {
			user.setPackageSessionstatus(SessionPackageStatus.valueOf((String) content[9]));
			user.setPackageSessionUuid((String) content[10]);
			user.setCurrentPTPackageSession((String) content[11]);
		}

		user.setEmail((String) content[12]);

		final Long joinDate = content[13] == null ? null : ((Timestamp) content[13]).getTime();
		user.setJoinDate(joinDate);
		if (joinDate != null) {
			user.setDisplayJoinDate(
					DateUtils.formatDateTime(user.getJoinDate(), sessionProperties.getDateTimeFormat()));
		}

		final String userCode = content[14] == null ? null : (String) content[14];
		user.setUserCode(userCode);
		user.setUserName((String) content[15]);
		return user;
	}

	private Integer calculateTrend(final int previous, final int current) {
		Integer trend = null;
		if (previous > 0 && current > 0) {
			trend = (int) Math.round((double) (current - previous) / previous * 100);
		}
		return trend;
	}

	private PtListingInfo parsePtListingInfo(final Object[] objects) {
		final Timestamp joinDateTimeStamp = (Timestamp) objects[10];
		final Long joinDate = Objects.isNull(joinDateTimeStamp) ? null : joinDateTimeStamp.toInstant().getEpochSecond();

		final Double ptRevenue = Objects.isNull(objects[8]) ? 0 : NumberUtils.roundUpDouble((Double) objects[8]);
		final Double f8Revenue = Objects.isNull(objects[9]) ? 0 : NumberUtils.roundUpDouble((Double) objects[9]);

		final String currency = sessionProperties.getCurrency();
		final String language = sessionProperties.getLang();
		final String displayPtRevenue = CurrencyUtils.formatCurrency(currency, language, ptRevenue);
		final String displayF8Revenue = CurrencyUtils.formatCurrency(currency, language, f8Revenue);
		final String country = (String) objects[4];

		return PtListingInfo.builder().uuid((String) objects[0]).name((String) objects[1]).avatar((String) objects[2])
				.city((String) objects[3]).country(ReportUtils.getLocalizeCountry(country))
				.username((String) objects[5]).documentStatus((String) objects[6]).level((String) objects[7])
				.ptRevenue(ptRevenue).f8Revenue(f8Revenue).displayPtRevenue(displayPtRevenue)
				.displayF8Revenue(displayF8Revenue).joinDate(joinDate).isActivated((Boolean) objects[11])
				.userCode((String) objects[12]).phone((String)objects[13]).build();
	}

	private PtExport parsePtExport(final Object[] objects) {

		final Timestamp joinDateTimeStamp = (Timestamp) objects[10];
		String joinDateAsString = StringUtils.EMPTY;
		String joinTimeAsString = StringUtils.EMPTY;

		if (!Objects.isNull(joinDateTimeStamp)) {
			final LocalDateTime joinDate = ZoneDateTimeUtils
					.convertFromUTCToLocalDateTime(joinDateTimeStamp.toInstant().getEpochSecond());
			joinTimeAsString = joinDate.toLocalTime().toString();
			joinDateAsString = joinDate.toLocalDate().format(ReportUtils.DATE_DDMMYYYY_FORMATTER);
		}

		final Double ptRevenue = Objects.isNull(objects[8]) ? 0 : NumberUtils.roundUpDouble((Double) objects[8]);
		final Double f8Revenue = Objects.isNull(objects[9]) ? 0 : NumberUtils.roundUpDouble((Double) objects[9]);
		final String displayPtRevenue = ReportUtils.formatCurrency(ptRevenue);
		final String displayF8Revenue = ReportUtils.formatCurrency(f8Revenue);
		final String country = (String) objects[4];

		return PtExport.builder().name((String) objects[1]).avatar((String) objects[2]).city((String) objects[3])
				.country(ReportUtils.getLocalizeCountry(country)).username((String) objects[5])
				.documentStatus((String) objects[6]).level((String) objects[7]).ptRevenue(ptRevenue)
				.f8Revenue(f8Revenue).displayPtRevenue(displayPtRevenue).displayF8Revenue(displayF8Revenue)
				.joinDate(joinDateAsString).joinTime(joinTimeAsString).isActivated((Boolean) objects[11])
				.email((String) objects[12]).staffCode((String) objects[13]).build();
	}

	@Transactional(readOnly = true)
	public BasicUserEntity getBasicUserInfoByUuid(final String userUuid) {
		final Optional<BasicUserEntity> userEntity = userRepository.findOneByUuid(userUuid);
		if (userEntity.isPresent()) {
			return userEntity.get();
		}

		return null;
	}

	private String method(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '0') {
			str = str.substring(0, str.length() - 2);
		}
		return str;
	}

	public List<String> getEuUserHaveUsercode() {
		return userRepository.getEuUserHaveUsercode();
	}

	public List<String> getAllUserUuidsByUserType(final String userType) {
		return userRepository.getAllUserUuidsByUserType(userType);
	}

	public int countActiveUser(final String userType, final LocalDateTime start, final LocalDateTime end) {
		return userRepository.countActiveUser(userType, start, end);
	}

	public int countTotalUser(String userType) {
		return userRepository.countTotalUser(userType);
	}

	public int countTotalPtHasBookingByRange(LocalDateTime start, LocalDateTime end) {
		List<Integer> statusCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal());
		return userRepository.countTotalPtHasBookingByRange(start, end, statusCode);
	}

	public Integer countTotalUserActived(String userType, LocalDateTime start, LocalDateTime end) {
		
		return userRepository.countTotalEndUserRegistered(userType, start, end);
	}

	public Integer countTotalUserByRangeTime(String userType, LocalDateTime start, LocalDateTime end) {
		return userRepository.countTotalUserByRangeTime(userType, start, end);
	}
	public int countApprovedTrainerByRange(LocalDateTime start, LocalDateTime end) {
		return userRepository.countApprovedTrainerByRange(start, end);
	}
}
