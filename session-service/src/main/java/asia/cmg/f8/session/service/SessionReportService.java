package asia.cmg.f8.session.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.ReportActivitySessionRevenue;
import asia.cmg.f8.session.dto.SessionFinancialResponse;
import asia.cmg.f8.session.dto.TrainerSessionStats;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.ActivitySessionRevenue;
import asia.cmg.f8.session.wrapper.dto.OrderFinancialRecord;
import asia.cmg.f8.session.wrapper.dto.SessionFinancial;
import asia.cmg.f8.session.wrapper.dto.UserSession;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;
import asia.cmg.f8.session.wrapper.service.UserWrapperService;

/**
 * Created on 12/15/16.
 */
@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class SessionReportService {

	private static final Logger LOG = LoggerFactory.getLogger(SessionReportService.class);

	private static final String EMPTY = "";
	
	private static final double MIN_GOLD = 160000;
	private static final double MIN_PLATINUM = 200000;
	private static final double MIN_DIAMOND = 300000;
	
	private static final String GOLD = "GOLD";
	private static final String PLATINUM = "PLATINUM";
	private static final String DIAMOND = "DIAMOND";

	private final OrderSessionWrapperService orderSessionWrapperService;

	private final SessionRepository sessionRepository;

	@Inject
	private UserWrapperService userWrapperService;

	@Inject
	private SessionProperties sessionProps;

	@Inject
	public SessionReportService(final OrderSessionWrapperService orderSessionWrapperService,
			final SessionRepository sessionRepository) {
		this.orderSessionWrapperService = orderSessionWrapperService;
		this.sessionRepository = sessionRepository;
	}

	public List<ReportActivitySessionRevenue> getActivitySessionReport(final LocalDate startDate,
			final LocalDate fromDate, final String language) {
		final List<ActivitySessionRevenue> result = orderSessionWrapperService.getSessionStatusRevenue(startDate,
				fromDate);
		return result.stream().map(entry -> formatActivitySessionRow(entry, language)).collect(Collectors.toList());
	}

	public Map<String, List<SessionFinancialResponse>> getSessionFinancialReport(final String startDateStr,
			final String endDateStr, final String language, final boolean allContracts) {
		final long startTime = ReportUtils.parseDateToSecond(startDateStr);
		final long endTime = ReportUtils.parseDateToSecondEndDay(endDateStr);
		final LocalDate startLocalDate = ReportUtils.parseToLocalDate(startDateStr);
		final LocalDate endLocalDate = ReportUtils.parseToLocalDate(endDateStr);
		
		final String currentZone = sessionProps.getLocalTimeZoneId();

		final Map<String, List<SessionFinancialResponse>> allSessionTypes = new HashMap<>();
		allSessionTypes.put(SessionStatus.OPEN.name(), new ArrayList<>());
		allSessionTypes.put(SessionStatus.CONFIRMED.name(), new ArrayList<>());
		allSessionTypes.put(SessionStatus.EU_CANCELLED.name(), new ArrayList<>());
		allSessionTypes.put(SessionStatus.BURNED.name(), new ArrayList<>());
		allSessionTypes.put(SessionStatus.COMPLETED.name(), new ArrayList<>());
		allSessionTypes.put(SessionStatus.EXPIRED.name(), new ArrayList<>());
		allSessionTypes.put(ReportUtils.TRANSFERRED, new ArrayList<>());
		allSessionTypes.put(ReportUtils.FREE_SESSION, new ArrayList<>());
		allSessionTypes.put(ReportUtils.ORDER, new ArrayList<>());

		handleActivityFinancialRecords(allSessionTypes, startTime, endTime, language, currentZone);

		handleFreeSessionFinancial(allSessionTypes, startTime, endTime, language, currentZone);

		handleTransferSessionFinancial(allSessionTypes, startLocalDate, endLocalDate, language, currentZone);
		handleOpenSessionFinancial(allSessionTypes, startLocalDate, endLocalDate, language, currentZone);

		final LocalDate startDateContract = allContracts ? LocalDate.of(1970, 1, 1) : startLocalDate;
		final LocalDate endDateContract = allContracts ? LocalDate.of(9999, 1, 1) : endLocalDate;
		handleOrderFinancialReport(allSessionTypes, startDateContract, endDateContract, language);

		return allSessionTypes;
	}

	private void handleFreeSessionFinancial(final Map<String, List<SessionFinancialResponse>> allSessionTypes,
			final long startSecond, final long endSecond, final String language, final String timeZone) {
		final List<SessionFinancial> result = orderSessionWrapperService.getFreeSessionFinancialReport(startSecond,
				endSecond, timeZone);

		final List<SessionFinancialResponse> reportTypeList = allSessionTypes.get(ReportUtils.FREE_SESSION);
		result.stream().forEach(entry -> {
			reportTypeList.add(formatTransferSessionFinancialRow(entry, language));
		});
	}

	private void handleActivityFinancialRecords(final Map<String, List<SessionFinancialResponse>> allSessionTypes,
			final long startSecond, final long endSecond, final String language, final String timeZone) {

		final List<SessionFinancial> result = orderSessionWrapperService.getSessionFinancialReport(startSecond,
				endSecond, timeZone);

		final Map<String, List<String>> mappingSessionStatus = new HashMap<>();
		mappingSessionStatus.put(SessionStatus.CONFIRMED.name(), Collections.singletonList(SessionStatus.CONFIRMED.name()));
		mappingSessionStatus.put(SessionStatus.EU_CANCELLED.name(), Collections.singletonList(SessionStatus.EU_CANCELLED.name()));
		mappingSessionStatus.put(SessionStatus.BURNED.name(), Collections.singletonList(SessionStatus.BURNED.name()));
		mappingSessionStatus.put(SessionStatus.COMPLETED.name(), Collections.singletonList(SessionStatus.COMPLETED.name()));
		mappingSessionStatus.put(SessionStatus.EXPIRED.name(), Collections.singletonList(SessionStatus.EXPIRED.name()));

		Instant endInstant = Instant.ofEpochSecond(endSecond);
		result.stream().forEach(entry -> {
			if ((entry.getStatus().equals(SessionStatus.OPEN.name())
					|| entry.getStatus().equals(SessionStatus.PENDING.name())
					|| entry.getStatus().equals(SessionStatus.CONFIRMED.name())) && entry.getExpiredDate() != null
					&& entry.getExpiredDate().toInstant().isBefore(endInstant)) {
				
				entry.setStatus(SessionStatus.EXPIRED.name());
			}
			
			final String status = getMappingStatus(mappingSessionStatus, entry.getStatus());
			
			if (StringUtils.isNotEmpty(status)) {
				final List<SessionFinancialResponse> reportTypeList = allSessionTypes.get(status);
				if (reportTypeList != null) {
					reportTypeList.add(formatSessionFinancialRow(entry, language));
				}
			}
		});
	}

	private void handleTransferSessionFinancial(final Map<String, List<SessionFinancialResponse>> allSessionTypes,
			final LocalDate startLocalDate, final LocalDate endLocalDate, final String language, final String timeZone) {
		final List<SessionFinancial> result = orderSessionWrapperService
				.getTransferStatusFinancialReport(startLocalDate, endLocalDate, timeZone);
		final List<SessionFinancialResponse> reportTypeList = allSessionTypes.get(ReportUtils.TRANSFERRED);
		result.stream().forEach(entry -> {
			reportTypeList.add(formatTransferSessionFinancialRow(entry, language));
		});
	}

	private void handleOpenSessionFinancial(final Map<String, List<SessionFinancialResponse>> allSessionTypes,
			final LocalDate startLocalDate, final LocalDate endLocalDate, final String language, final String timeZone) {
		final List<SessionFinancial> result = orderSessionWrapperService.getOpenStatusFinancialReport(startLocalDate,
				endLocalDate, timeZone);
		final List<SessionFinancialResponse> reportTypeList = allSessionTypes.get(SessionStatus.OPEN.name());
		result.stream().forEach(entry -> {
			reportTypeList.add(formatSessionFinancialRow(entry, language));
		});
	}

	private void handleOrderFinancialReport(final Map<String, List<SessionFinancialResponse>> allSessionTypes,
			final LocalDate fromDate, final LocalDate toDate, final String language) {
		final List<OrderFinancialRecord> result = orderSessionWrapperService.getOrderFinancialReport(fromDate, toDate);
		final List<SessionFinancialResponse> reportTypeList = allSessionTypes.get(ReportUtils.ORDER);
		result.stream().forEach(entry -> reportTypeList.add(formatOrderFinancialRow(entry, language)));
	}

	public List<TrainerSessionStats> getPtStatsInPeriod(final String ptUuid, final long startTime, final long endTime) {

		final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name)
				.collect(Collectors.toList());

		final List<Object[]> rows = sessionRepository.getStatsSessionForTrainerInTimePeriod(ptUuid, startTime, endTime,
				revenueStatus);

		final List<TrainerSessionStats> stats = new ArrayList<>();

		for (final Object[] row : rows) {
			final Timestamp time = (Timestamp) row[0];
			final Double amount = (Double) row[1];

			LOG.info("PT stats data {}, {}", time, amount);

			final TrainerSessionStats stat = new TrainerSessionStats(ReportUtils.miliToSecond(time.getTime()), amount,
					sessionProps.getCurrency());
			stats.add(stat);
		}

		return stats;
	}

	public List<UserSession> getClientBurnedInOneDayOfTrainer(final String ptUuid, final long startTime,
			final long endTime) {
		final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name)
				.collect(Collectors.toList());
		return userWrapperService.getClientBurnedInOneDayOfTrainer(ptUuid, startTime, endTime, revenueStatus);
	}

	private ReportActivitySessionRevenue formatActivitySessionRow(final ActivitySessionRevenue entry,
			final String language) {
		final String sessionDate = ReportUtils.formatLocalDate(entry.getDate());
		final String commission = CurrencyUtils.formatCurrency(sessionProps.getCurrency(), language,
				ReportUtils.formatDoubleNullToZero(entry.getCommission()));

		final String serviceFee = CurrencyUtils.formatCurrency(sessionProps.getCurrency(), language,
				ReportUtils.formatDoubleNullToZero(entry.getServiceFee()));

		return new ReportActivitySessionRevenue(sessionDate, entry.getOpen(), entry.getConfirmed(),
				entry.getCancelled(), entry.getNoShown(), entry.getUsed(), entry.getExpired(), entry.getTransferred(),
				commission, serviceFee);
	}

	private SessionFinancialResponse formatSessionFinancialRow(final SessionFinancial entry, final String language) {
		final String commissionAmount = calculateAmountAndFormat(entry.getPrice(), entry.getNumOfSession(),
				entry.getCommission(), language);
		
		final double clubCommission = 0;
		double ptCommission = 0;
		double ptGrossIncome = 0;
		double ptNetIncome = 0;

		final String serviceFee = calculateAmountAndFormat(entry.getPrice(), entry.getNumOfSession(),
				(100 - entry.getCommission()), language);
		
		final String commissionUnitPrice = getCommissionUnitPrice(entry.getPrice(), entry.getNumOfSession());
		
		final String price = ReportUtils.formatCurrency(entry.getPrice());
		final String datePurchased = ReportUtils.formatDateFromMilis(entry.getDatePurchased());
		final String dateUpdated = ReportUtils.formatDate(entry.getDateUpdated());
		final String sessionTime = entry.getSessionTime();
		final String expiredDate = ReportUtils.formatDateFromMilis(entry.getExpiredDate());

		SessionFinancialResponse response = new SessionFinancialResponse(entry.getSessionUuid(), entry.getProductName(), entry.getOrderUuid(),
				entry.getProductUuid(), price, EMPTY, EMPTY, commissionAmount, serviceFee, commissionUnitPrice, datePurchased, dateUpdated,
				sessionTime, entry.getPtUuid(), entry.getUserUuid(), expiredDate, EMPTY, entry.getUserFullName(),
				entry.getUserUserName(), entry.getTrainerName(), entry.getTrainerUserName(), entry.getOrderCode(),
				EMPTY, entry.getUserClub(), entry.getPurchaseOrderClub(), entry.getTrainerStaffCode(),
				entry.getMemberBarCode(), 0, 0, 0, entry.getLastStatus());
		
		double commissionUnitPriceValue = this.calculateCommissionUnitPrice(entry.getPrice(), entry.getNumOfSession());
		ptCommission = (commissionUnitPriceValue * entry.getPtCommission())/100;
		
		if(!entry.isOnepayTransaction()) {
			ptGrossIncome = ptCommission;
		} else {
			double minCommission = this.getMinPtCommissionByProductName(entry.getProductName());
			ptGrossIncome = ptCommission < minCommission ? minCommission : ptCommission;
		}
		
		ptNetIncome = ptGrossIncome - (ptGrossIncome*10/100); 
		
		response.setPtCommissionPercent(entry.getPtCommission() + "%");
		response.setClubCommission(ReportUtils.formatCurrency(0d));
		response.setPtCommission(ReportUtils.formatCurrency(ptCommission));
		response.setPtGrossIncome(ReportUtils.formatCurrency(ptGrossIncome));
		response.setPtNetIncome(ReportUtils.formatCurrency(ptNetIncome));
		response.setCheckinClubName(entry.getCheckinClubName());
		response.setCouponCode(entry.getCouponCode());
		response.setOnepayTransaction(entry.isOnepayTransaction());
		response.setContractNumber(entry.getContractNumber());
		
		return response;
	}

	private SessionFinancialResponse formatTransferSessionFinancialRow(final SessionFinancial entry,
			final String language) {

		final String commissionAmount = calculateAmountAndFormat(entry.getPrice(), entry.getNumOfSession(),
				entry.getCommission(), language);

		final String serviceFee = calculateAmountAndFormat(entry.getPrice(), entry.getNumOfSession(),
				(100 - entry.getCommission()), language);

		final String price = ReportUtils.formatCurrency(entry.getPrice());
		final String commissionUnitPrice = getCommissionUnitPrice(entry.getPrice(), entry.getNumOfSession());

		final String datePurchased = ReportUtils.formatDateFromMilis(entry.getDatePurchased());
		final String dateUpdated = ReportUtils.formatDateFromMilis(entry.getDateUpdated());
		final String sessionTime = entry.getSessionTime();
		final String expiredDate = ReportUtils.formatDateFromMilis(entry.getExpiredDate());

		return new SessionFinancialResponse(entry.getSessionUuid(), entry.getProductName(), entry.getOrderUuid(),
				entry.getProductUuid(), price, EMPTY, EMPTY, commissionAmount, serviceFee, commissionUnitPrice,
				datePurchased, dateUpdated, sessionTime, entry.getPtUuid(), entry.getUserUuid(), expiredDate,
				entry.getOldPtUuid(), entry.getUserFullName(), entry.getUserUserName(), entry.getTrainerName(),
				entry.getTrainerUserName(), entry.getOrderCode(), EMPTY, entry.getUserClub(),
				entry.getPurchaseOrderClub(), entry.getTrainerStaffCode(), entry.getMemberBarCode(), 0, 0, 0,
				entry.getLastStatus());
	}

	private SessionFinancialResponse formatOrderFinancialRow(final OrderFinancialRecord entry, final String language) {

		final String price = ReportUtils.formatCurrency(entry.getPrice());
		final String originalPrice = ReportUtils.formatCurrency(entry.getOriginalPrice());
		final String discount = ReportUtils.formatCurrency(entry.getDiscount());

		final String datePurchased = ReportUtils.formatDateFromMilis(entry.getDatePurchase());
		final String expiredDate = ReportUtils.formatDateFromMilis(entry.getDateExpire());

		int totalSession = entry.getTotalSession();
		int usedSession = entry.getBurnedSession();
		int remainingSession = totalSession - usedSession;
		final String commissionUnitPrice = getCommissionUnitPrice(entry.getPrice(), totalSession);

		SessionFinancialResponse sessionFinalReport = new SessionFinancialResponse(EMPTY, entry.getProductName(),
				entry.getOrderUuid(), entry.getProductUuid(), price, originalPrice, discount, EMPTY, EMPTY,
				commissionUnitPrice, datePurchased, EMPTY, EMPTY, entry.getPtUuid(), entry.getUserUuid(), expiredDate,
				EMPTY, entry.getUserFullName(), entry.getUserUserName(), entry.getTrainerName(),
				entry.getTrainerUserName(), entry.getOrderCode(), entry.getContractNumber(), entry.getUserClub(),
				entry.getPurchaseOrderClub(), entry.getTrainerStaffCode(), entry.getMemberBarCode(), totalSession,
				usedSession, remainingSession, StringUtils.EMPTY);
		
		sessionFinalReport.setCouponCode(entry.getCouponCode());
		sessionFinalReport.setFreeSession(entry.getFreeSession());
		sessionFinalReport.setOriginalSession(entry.getOriginalSession());
		sessionFinalReport.setOnepayTransaction(entry.isOnepayTransaction());
		return sessionFinalReport;
	}

	private double calculatePTCommission(double commissionUnitPrice, double ptCommissionPercent, double clubCommission, String productName) {
		double result = 0d;
		
		double ptCommission = ((commissionUnitPrice * ptCommissionPercent)/100) - clubCommission;
		double minPtCommission = this.getMinPtCommissionByProductName(productName);
		result = ptCommission < minPtCommission ? minPtCommission : ptCommission;
		
		return result;
	}
	
	private double getMinPtCommissionByProductName(String productName) {
		double result = 0;
		
		if(!StringUtils.isEmpty(productName)) {
			if(productName.compareToIgnoreCase(GOLD) == 0) {
				result = MIN_GOLD;
			} else if(productName.compareToIgnoreCase(PLATINUM) == 0) {
				result = MIN_PLATINUM;
			} else if(productName.compareToIgnoreCase(DIAMOND) == 0) {
				result = MIN_DIAMOND;
			}
		}
		
		return result;
	}
	
	private String calculateAmountAndFormat(final Double price, final int numOfSession, final double commissionPctg,
			final String language) {
		final double amount = (1.0 / numOfSession) * price * commissionPctg / 100;
		return ReportUtils.formatCurrency(amount);
	}
	
	private double calculateCommissionUnitPrice(final Double price, final int numOfSession) {
		if(numOfSession == 0) {
			return 0;
		}
		
		return (price/numOfSession);
	}

	private String getCommissionUnitPrice(final Double price, final int numOfSession) {
		if (numOfSession == 0) {
			return EMPTY;
		}
		return ReportUtils.formatCurrency(price / numOfSession);
	}
	
	private String getMappingStatus(final Map<String, List<String>> mappingSessionStatus, final String status) {
		final Optional<String> statusOpt = mappingSessionStatus.keySet().stream()
				.filter(entry -> mappingSessionStatus.get(entry).contains(status)).findFirst();
		if (statusOpt.isPresent()) {
			return statusOpt.get();
		}
		return null;
	}

}
