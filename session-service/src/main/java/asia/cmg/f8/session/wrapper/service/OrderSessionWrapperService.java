package asia.cmg.f8.session.wrapper.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.TrainerActivity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.MVOrderReconcileRepository;
import asia.cmg.f8.session.repository.MVSessionDailyRepository;
import asia.cmg.f8.session.repository.MVSessionStatsDailyRepository;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.repository.SessionHistoryRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.ActiveOrderTimeRange;
import asia.cmg.f8.session.wrapper.dto.ActivitySessionRevenue;
import asia.cmg.f8.session.wrapper.dto.AvailableSessionWithOrder;
import asia.cmg.f8.session.wrapper.dto.OrderFinancialRecord;
import asia.cmg.f8.session.wrapper.dto.OrderSession;
import asia.cmg.f8.session.wrapper.dto.OrderSessionStatusDaily;
import asia.cmg.f8.session.wrapper.dto.SessionFinancial;

@Service
@SuppressWarnings("PMD.ExcessiveParameterList")
public class OrderSessionWrapperService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderSessionWrapperService.class);
    public static final String NOT_STARTED = "NOT_STARTED";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String DD_LLLL_YYYY = "dd/LLLL/yyyy";

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private MVSessionDailyRepository mVsessionDailyRepository;

    @Inject
    private SessionHistoryRepository sessionHistoryRepository;

    @Inject
    private MVOrderReconcileRepository orderReconcileRepository;

    @Inject
    private MVSessionStatsDailyRepository sessionStatsDailyRepository;

    @Inject
    private OrderRepository orderRepository;

    public List<OrderSession> getTrainerActivityAndRevenue(final String trainerUuid,
                                                           final long startTime, final List<String> open,
                                                           final String confirmed, final String euCancelled, final String burned,
                                                           final String completed, final String expired,
                                                           final String transferred, final List<String> revenueStatus,
                                                           final Pageable pageable) {

        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();
        final String resolveSortValue = resolveSortValueOfPTScope(sortField);

        final Pageable wrappedPageable = new PageRequest(pageable.getPageNumber(),
                pageable.getPageSize(), order.getDirection(), resolveSortValue);

        final List<Object[]> response = sessionRepository.getActionStatisticByTrainer(trainerUuid,
                startTime, open, confirmed, euCancelled, burned, completed, expired,
                transferred, revenueStatus, wrappedPageable);

        LOG.info("Response data {}", response.toString());
        return response.stream().map(this::process).collect(Collectors.toList());
    }

    public List<TrainerActivity> getTrainerActivityAndRevenueExport(final String trainerUuid,
                                                                    final long startTime,
                                                                    final long endTime,
                                                                    final List<String> revenueStatus) {

        final List<Object[]> response = sessionRepository.getActivitiesByTrainer(trainerUuid, startTime, endTime, revenueStatus);

        LOG.info("Response data {}", response.toString());
        return response.stream().map(this::trainerActivity).collect(Collectors.toList());
    }


    private OrderSession process(final Object[] row) {
        return new OrderSession((String) row[0], (String) row[1], (String) row[2], (Double) row[3],
                (Double) row[4], (Integer) row[5], (BigInteger) row[6], (BigInteger) row[7],
                (BigInteger) row[8], (BigInteger) row[9], (BigInteger) row[10],
                (BigInteger) row[11], (BigInteger) row[12], (BigInteger) row[13],
                (BigInteger) row[14]);
    }

    private TrainerActivity trainerActivity(final Object[] row) {

		//final Timestamp orderDateTimestamp = (Timestamp) row[5];
		// final String orderDate = (orderDateTimestamp != null) ?
		// ZoneDateTimeUtils.convertFromUTCToLocalDateTime(orderDateTimestamp.toInstant().getEpochSecond()).toString()
		// : null;
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_LLLL_YYYY);

		final Timestamp expiredDateTimestamp = (Timestamp) row[7];
		String expiredDate = (expiredDateTimestamp != null) ? ZoneDateTimeUtils
				.convertFromUTCToLocalDateTime(expiredDateTimestamp.toInstant().getEpochSecond()).toString() : null;

		String usedDate = (String) row[8];
		String usedTime = (String) row[8];
		if (row[8] != null && !NOT_STARTED.equalsIgnoreCase(usedDate)) {
			final Timestamp usedDateTimestamp = Timestamp.valueOf(usedDate);
			usedDate = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(usedDateTimestamp.toInstant().getEpochSecond())
					.toString();

			final LocalDateTime usedDatedateTime = LocalDateTime.parse(method(row[8].toString()),
					DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));

			final LocalDate usedDatelocalDate = usedDatedateTime.toLocalDate();
			final LocalTime usedDatelocalTime = usedDatedateTime.toLocalTime();
			usedDate = usedDatelocalDate.format(formatter);
			usedTime = usedDatelocalTime.toString();

		}

		final LocalDateTime orderDatedateTime = LocalDateTime.parse(method(row[5].toString()),
				DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
		final LocalDate orderDatelocalDate = orderDatedateTime.toLocalDate();
		final LocalTime orderDatelocalTime = orderDatedateTime.toLocalTime();
		

		if(row[7] != null){
			final LocalDateTime expiredDatedateTime = LocalDateTime.parse(method(row[7].toString()),
					DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));		
			expiredDate = expiredDatedateTime != null ? (expiredDatedateTime.toLocalDate().format(formatter)).toString()
					: null;
		}

		final double serviceFee = Math.round((Double) row[4]);
		
		return new TrainerActivity((String) row[0], (String) row[1], (String) row[2], String.valueOf(serviceFee),
				(String) row[3], orderDatelocalDate.format(formatter), orderDatelocalTime.toString(), usedDate,
				usedTime, (String) row[6], expiredDate, (String)row[9],(String)row[10]);
		
		
		
	}

    public List<ActivitySessionRevenue> getSessionStatusRevenue(final LocalDate startDate,
                                                                final LocalDate endDate) {
        final List<Object[]> response = orderReconcileRepository.getActivityFinancialReport(
                startDate, endDate);
    		
        return response.stream().map(this::processActivityDateRow)
                .collect(Collectors.toList());
    }

	public List<SessionFinancial> getSessionFinancialReport(final long startSecond, final long endSecond,
			final String timeZone) {
		return mVsessionDailyRepository.getSessionFinancialReport(startSecond, endSecond, timeZone);
	}

	public List<SessionFinancial> getFreeSessionFinancialReport(final long startSecond, final long endSecond,
			final String timeZone) {
		return mVsessionDailyRepository.getFreeSessionFinancialReport(startSecond, endSecond, timeZone);
	}

	public List<SessionFinancial> getTransferStatusFinancialReport(final LocalDate startDate, final LocalDate toDate,
			final String timeZone) {
		return sessionStatsDailyRepository.getTransferredFinancialReport(startDate, toDate, timeZone);
	}

    public List<SessionFinancial> getOpenStatusFinancialReport(final LocalDate startDate,
                                                               final LocalDate toDate,
                                                               final String timeZone) {
        return sessionStatsDailyRepository.getOpenFinancialReport(startDate, toDate, timeZone);
    }

    public List<OrderFinancialRecord> getOrderFinancialReport(final LocalDate fromDate,
                                                              final LocalDate toDate) {
        return mVsessionDailyRepository.getOrderFinancialReport(fromDate, toDate);
    }

    public List<OrderSessionStatusDaily> getOrderSessionReconcile(final LocalDate date,
                                                                  final String offset) {
        final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());

        final List<Object[]> response = sessionHistoryRepository.getOrderWithStatusInDate(
                SessionStatus.OPEN.name(), SessionStatus.PENDING.name(),
                SessionStatus.CONFIRMED.name(), SessionStatus.EU_CANCELLED.name(),
                SessionStatus.TRAINER_CANCELLED.name(), SessionStatus.CANCELLED.name(),
                SessionStatus.BURNED.name(), SessionStatus.COMPLETED.name(),
                SessionStatus.EXPIRED.name(), SessionStatus.TRANSFERRED.name(), revenueStatus,
                date, offset);
        return response.stream().map(this::processOrderStatusReconcile)
                .collect(Collectors.toList());
    }

    public List<ActiveOrderTimeRange> getActiveOrdersAndTimeRange(final String userId,
                                                                  final String trainerId,  final String packageUuid, final List<String> sessionStatus, final List<String> pkgStatus) {
        final List<Object[]> response = orderRepository.getActiveOrdersAndTimeRange(userId,
                trainerId, packageUuid, sessionStatus, pkgStatus);
        return response.stream().map(this::processActivePackageRow)
                .collect(Collectors.toList());
    }

    public List<AvailableSessionWithOrder> getAvailableSessionWithOrder(final String userId,
                                                                        final String trainerId, final List<String> availableStatus) {
        final List<Object[]> response = sessionRepository
                .getAvailableSessionAndOrderByUserAndTrainer(userId, trainerId, availableStatus);
        return response.stream().map(this::processAvailableSessionWithOrder)
                .collect(Collectors.toList());
    }
    
    public List<AvailableSessionWithOrder> getAvailableSessionWithOrderByPackageId(final String userId,
            final String packageId, final List<String> availableStatus) {
			final List<Object[]> response = sessionRepository
			.getAvailableSessionAndOrderByUserAndPackage(userId, packageId, availableStatus);
			return response.stream().map(this::processAvailableSessionWithOrder)
			.collect(Collectors.toList());
	}

    public static String resolveSortValueOfPTScope(final String sortField) {
        switch (sortField) {
            case "uuid":
                return "ss.user_uuid";
            case "revenue":
                return "so.price";
            default:
                return "su." + ReportUtils.FULL_NAME;
        }
    }

    private ActivitySessionRevenue processActivityDateRow(final Object[] row) {
        return new ActivitySessionRevenue((LocalDate) row[0], (Long) row[1], (Long) row[2],
                (Long) row[3], (Long) row[4], (Long) row[5], (Long) row[6], (Long) row[7],
                (Double) row[8], (Double) row[9]);
    }

    private OrderSessionStatusDaily processOrderStatusReconcile(final Object[] row) {
        return new OrderSessionStatusDaily((String) row[0], (BigInteger) row[1],
                (BigInteger) row[2], (BigInteger) row[3], (BigInteger) row[4], (BigInteger) row[5],
                (BigInteger) row[6], (BigInteger) row[7], (BigInteger) row[8], (BigInteger) row[9],
                (BigInteger) row[10], (BigInteger) row[11], (BigInteger) row[12],
                (BigInteger) row[13], (BigInteger) row[14], (BigInteger) row[15],
                (BigInteger) row[16], (Integer) row[17], (Double) row[18], (Double) row[19]);
    }

    private ActiveOrderTimeRange processActivePackageRow(final Object[] row) {
        return new ActiveOrderTimeRange((String) row[0], (Timestamp) row[1], (Integer) row[2],
                (Timestamp) row[3], (String) row[4]);
    }

    private AvailableSessionWithOrder processAvailableSessionWithOrder(final Object[] row) {
        return new AvailableSessionWithOrder((String) row[0], (Timestamp) row[1], (String) row[2],
                (Timestamp) row[3], (Integer) row[4]);
    }
    
    private String method(String str) {
    	 if (str != null && str.length() > 0 && str.charAt(str.length() -2) == '.'  && str.charAt(str.length() -1) == '0') {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }

}
