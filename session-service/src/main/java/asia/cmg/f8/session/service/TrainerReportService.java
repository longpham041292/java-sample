package asia.cmg.f8.session.service;

import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.MoneyStatisticResponse;
import asia.cmg.f8.session.dto.PTSessionStatRevenue;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.dto.TrainerActivity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.SessionPackageRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.OrderSession;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;

import org.apache.commons.lang.StringUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created on 1/7/17.
 */
@Service
public class TrainerReportService {

    private static final String NEW_CUSTOMER = "new_customer";
    private static final String SESSION_BURNED = "session_burned";
    private static final String PAID_OUT = "paid_out";
    private final BasicUserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionProperties sessionProperties;
    private final SessionPackageRepository sessionPackageRepository;
    private final OrderSessionWrapperService orderSessionWrapperService;

    @Inject
    public TrainerReportService(final BasicUserRepository userRepository,
                                final SessionRepository sessionRepository, final SessionProperties sessionProperties,
                                final SessionPackageRepository sessionPackageRepository,
                                final OrderSessionWrapperService orderSessionWrapperService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionProperties = sessionProperties;
        this.sessionPackageRepository = sessionPackageRepository;
        this.orderSessionWrapperService = orderSessionWrapperService;
    }

    @Transactional(readOnly = true)
    public List<PTSessionStatRevenue> getTrainerActivityAndRevenue(final String trainerUuid,
                                                                   final TimeInfo timeInfo,
                                                                   final Pageable pageable,
                                                                   final String language) {

        final List<String> openStatus = SessionStatus.getOpenSessionStatus().stream().map(Enum::name).collect(Collectors.toList());

        final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name).collect(Collectors.toList());

        final List<OrderSession> response = orderSessionWrapperService
                .getTrainerActivityAndRevenue(trainerUuid, timeInfo.getMiddleTime(), openStatus,
                        SessionStatus.CONFIRMED.name(), SessionStatus.EU_CANCELLED.name(),
                        SessionStatus.BURNED.name(), SessionStatus.COMPLETED.name(),
                        SessionStatus.EXPIRED.name(), SessionStatus.TRANSFERRED.name(),
                        revenueStatus, pageable);

        return response.stream().map(entry -> process(entry, language))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainerActivity> getTrainerActivityAndRevenueExport(final String trainerUuid, final TimeInfo timeInfo) {

        final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name).collect(Collectors.toList());

        return orderSessionWrapperService
                .getTrainerActivityAndRevenueExport(trainerUuid, timeInfo.getStartTime(),
                        timeInfo.getEndTime(), revenueStatus);
    }

    private PTSessionStatRevenue process(final OrderSession entry, final String language) {
        final PTSessionStatRevenue row = new PTSessionStatRevenue();
        row.setUuid(entry.getUuid());
        row.setAvatar(entry.getAvatar());
        row.setFullName(entry.getFullName());

        row.setOpen(entry.getOpen());
        row.setConfirmed(entry.getConfirmed());
        row.setCancelled(entry.getEuCancelled());
        row.setNoShow(entry.getBurned());
        row.setUsed(entry.getCompleted());
        row.setTransferred(entry.getTransferred());
        row.setExpired(entry.getExpired());


        final double totalPrice = entry.getPrice();

        final double revenue = Math.round((100.0 * entry.getRevenueSession() / entry.getSessionNumber())
                * totalPrice) / 100.0;
        row.setRevenue(CurrencyUtils.formatCurrency(sessionProperties.getCurrency(), language, revenue));

        final double serviceFee = Math.round((100.0 * entry.getPtFeeSessions() / entry
                .getSessionNumber()) * totalPrice)
                * (100 - entry.getCommission()) / 10000.0;
        row.setServiceFee(CurrencyUtils.formatCurrency(sessionProperties.getCurrency(), language,
                serviceFee));

        return row;
    }

    @Transactional(readOnly = true)
    public List<Map<String, List<String>>> getTrainerPurchaseHistory(final String trainerUuid,
                                                                     final TimeRange timeRange,
                                                                     final TimeInfo timeInfo,
                                                                     final String language) {
        final List<Object[]> purchaseResponse = userRepository.getCompletedOrdersOfTrainerInRange(
                trainerUuid, timeInfo.getMiddleTime(), timeInfo.getEndTime(),
                SessionStatus.EXPIRED.name());

        final Map<String, BigDecimal> purchaseHistory = ReportUtils.initPurchaseHistory(timeRange);
        for (final Object[] col : purchaseResponse) {
            buildTrainerPurchaseRecord(col, purchaseHistory, timeRange);
        }

        return purchaseHistory
                .entrySet()
                .stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), Arrays.asList(entry
                        .getValue().toString(), CurrencyUtils.formatCurrency(
                        sessionProperties.getCurrency(), language, entry.getValue()))))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, List<String>>> getTrainerServiceFeeHistory(final String trainerUuid,
                                                                       final TimeRange timeRange,
                                                                       final TimeInfo timeInfo,
                                                                       final String language) {
        final List<Object[]> purchaseResponse = userRepository.getCompletedOrdersOfTrainerInRange(
                trainerUuid, timeInfo.getMiddleTime(), timeInfo.getEndTime(),
                SessionStatus.EXPIRED.name());

        final Map<String, BigDecimal> serviceFeeHistory = ReportUtils.initPurchaseHistory(timeRange);
        for (final Object[] col : purchaseResponse) {
            final Timestamp timeStamp = (Timestamp) col[0];

            final String key = ReportUtils.buildKeyPurchaseHistory(timeRange, timeStamp);

            final BigDecimal currentAmount = serviceFeeHistory.get(key);
            if (currentAmount != null) {
                serviceFeeHistory.put(key, currentAmount.add(BigDecimal.valueOf((double) col[2])));
            }
        }

        return serviceFeeHistory
                .entrySet()
                .stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), Arrays.asList(entry
                        .getValue().toString(), CurrencyUtils.formatCurrency(
                        sessionProperties.getCurrency(), language, entry.getValue()))))
                .collect(Collectors.toList());
    }

    private void buildTrainerPurchaseRecord(final Object[] col,
                                            final Map<String, BigDecimal> purchaseHistory,
                                            final TimeRange timeRange) {
        final Timestamp timeStamp = (Timestamp) col[0];

        final String key = ReportUtils.buildKeyPurchaseHistory(timeRange, timeStamp);

        final BigDecimal currentAmount = purchaseHistory.get(key);
        if (currentAmount != null) {
            purchaseHistory.put(key, currentAmount.add(BigDecimal.valueOf((double) col[1])));
        }
    }

    @Transactional(readOnly = true)
    public StatisticResponse getNewCustomerByTrainerInRange(final String trainerUuid,
                                                            final TimeInfo timeInfo) {
        //TODO: Need to merge two requests in one.
        final int previous = sessionPackageRepository.countNewCustomerOfTrainerInRange(trainerUuid, timeInfo.getStartTime(), timeInfo.getMiddleTime());
        final int current = sessionPackageRepository.countNewCustomerOfTrainerInRange(trainerUuid, timeInfo.getMiddleTime(), timeInfo.getEndTime());

        final StatisticResponse newCustomerStatistic = new StatisticResponse(NEW_CUSTOMER, 0, null);
        newCustomerStatistic.setTotal(current);
        newCustomerStatistic.setTrend(ReportUtils.calculateTrend(previous, current));

        return newCustomerStatistic;
    }

    @Transactional(readOnly = true)
    public StatisticResponse getBurnedSessionStatisticByTrainerInRange(final String trainerUuid,
                                                                       final TimeInfo timeInfo) {
        final List<String> revenueSessionStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name).collect(Collectors.toList());
        final Object[] stats = sessionRepository.calculateBurnedSessionOfTrainerByTimeRange(trainerUuid, timeInfo.getStartTime(),
                timeInfo.getMiddleTime(), timeInfo.getEndTime(), revenueSessionStatus);

        final StatisticResponse burnedSessionResponse = new StatisticResponse(SESSION_BURNED, 0, null);

        if (!Objects.isNull(stats) && stats.length > 0) {
            final Object[] row = (Object[]) stats[0];
            burnedSessionResponse.setTotal(((BigInteger) row[1]).intValue());
            burnedSessionResponse.setTrend(ReportUtils.calculateTrend(
                    ((BigInteger) row[0]).intValue(), ((BigInteger) row[1]).intValue()));
        }

        return burnedSessionResponse;
    }

    @Transactional(readOnly = true)
    public MoneyStatisticResponse getPaidOutStatisticByTrainerInRange(final String trainerUuid,
                                                                      final TimeInfo timeInfo,
                                                                      final String language) {
        final List<String> revenueSessionStatus = SessionStatus.getRevenueSessionStatus().stream().map(Enum::name).collect(Collectors.toList());
        final List<Object[]> stats = sessionRepository.calculatePaidOutOfTrainerByTimeRange(trainerUuid, timeInfo.getStartTime(),
                timeInfo.getMiddleTime(), timeInfo.getEndTime(), revenueSessionStatus);

        final MoneyStatisticResponse paidOutResponse = new MoneyStatisticResponse(PAID_OUT, StringUtils.EMPTY, null);
        if (!stats.isEmpty()) {
            final Object[] row = stats.get(0);
            final Double current = (Double) row[1];
            final Double previous = (Double) row[0];
            if (current == null && previous == null) {
                return paidOutResponse;
            }

            paidOutResponse.setTotal(CurrencyUtils.formatCurrency(sessionProperties.getCurrency(),
                    language, ReportUtils.formatDoubleNullToZero(current)));

            if (previous > 0 && current > 0) {
                paidOutResponse.setTrend((int) Math.round((current - previous) / previous * 100));
            }
        }

        return paidOutResponse;
    }


}
