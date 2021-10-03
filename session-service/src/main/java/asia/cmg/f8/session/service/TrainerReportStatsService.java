package asia.cmg.f8.session.service;

import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.TrainerUserRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.TrainerStatsAvg;
import asia.cmg.f8.session.wrapper.service.UserWrapperService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@Service
public class TrainerReportStatsService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainerReportStatsService.class);

    private final TrainerUserRepository trainerUserRepository;
    private final UserWrapperService userWrapperService;

    @Inject
    public TrainerReportStatsService(final TrainerUserRepository trainerUserRepository,
            final UserWrapperService userWrapperService) {
        this.trainerUserRepository = trainerUserRepository;
        this.userWrapperService = userWrapperService;
    }


    @Transactional(readOnly = true)
    public int countActiveTrainers() {
        final List<String> revenueStatus = SessionStatus.getScheduledSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        return trainerUserRepository.countCurrentActiveTrainers(revenueStatus);
    }

    public StatisticResponse getTrainerStatsNewCerts(final TimeInfo timeInfo) {
        final List<Object[]> rows = trainerUserRepository
                .getStatsTrainerNewCert(timeInfo.getStartTime(), timeInfo.getMiddleTime(),
                        timeInfo.getEndTime(), DocumentStatusType.APPROVED.name());
        final StatisticResponse newCertResponse = new StatisticResponse("NEW_CERT", 0, null);
        if (!rows.isEmpty()) {
            // this query only return 1 row
            final Object[] row = rows.get(0);
            LOG.info("Last {} , current {}", row[0], row[1]);
            final int last = ((BigInteger) row[0]).intValue();
            final int current = ((BigInteger) row[1]).intValue();

            newCertResponse.setTotal(current);
            newCertResponse.setTrend(ReportUtils.calculateTrend(last, current));
        }
        return newCertResponse;
    }

    public StatisticResponse getTrainerStatsScheduledSession(final TimeInfo timeInfo) {
        final List<String> scheduledStatus = SessionStatus.getScheduledSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        final List<Object[]> rows = trainerUserRepository.getStatsTrainerScheduledSession(
                timeInfo.getStartTime(), timeInfo.getMiddleTime(), timeInfo.getEndTime(),
                scheduledStatus);
        final StatisticResponse scheduledResponse = new StatisticResponse("SCHEDULED_SESSION", 0,
                null);

        if (!rows.isEmpty()) {
            final Object[] row = rows.get(0);
            LOG.info("Last {} , current {}", row[0], row[1]);
            final int last = ((BigInteger) row[0]).intValue();
            final int current = ((BigInteger) row[1]).intValue();

            scheduledResponse.setTotal(current);
            scheduledResponse.setTrend(ReportUtils.calculateTrend(last, current));
        }
        return scheduledResponse;
    }

    public List<StatisticResponse> getStatsTrainerClientScheduledCancelled(final TimeInfo timeInfo,
            final TimeRange timeRange) {
        LOG.info("Stats for Client Schedule Cancelled from {}, middle {}, to {}", timeInfo.getStartTime(),
                timeInfo.getMiddleTime(), timeInfo.getEndTime());
        final List<String> scheduledStatus = SessionStatus.getScheduledSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        final List<String> cancelledStatus = SessionStatus.getUserCancelledSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        final List<TrainerStatsAvg> rows = userWrapperService
                .getStatsTrainerClientScheduledCancelled(
                timeInfo.getStartTime(), timeInfo.getEndTime(), scheduledStatus, cancelledStatus);

        final StatisticResponse avgSessionTrainerResp = new StatisticResponse(
                "AVG_SESSION_TRAINER", 0, null);
        final StatisticResponse avgClientTrainerResp = new StatisticResponse("AVG_CLIENT_TRAINER",
                0, null);
        final StatisticResponse avgCancelledSessionResp = new StatisticResponse(
                "AVG_CANCELLED_SESSION", 0, null);

        int daysOfRange = 7;
        switch (timeRange) {
        case WEEK:
            daysOfRange = 7;
            break;
        case MONTH:
            daysOfRange = 30;
            break;
        default:
            break;
        }

        final LocalDate middle = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(
                timeInfo.getMiddleTime()).toLocalDate();

        calculateAvgSessionTrainer(avgSessionTrainerResp, rows, middle,
                daysOfRange);
        calculateClientTrainerTrainer(avgClientTrainerResp, rows, middle,
                daysOfRange);
        calculateCancelledSessionTrainer(avgCancelledSessionResp, rows, middle,
                daysOfRange);

        return Arrays.asList(avgSessionTrainerResp, avgClientTrainerResp, avgCancelledSessionResp);
    }

    public int countTrainerPendingStatus() {
        return trainerUserRepository.countTrainerByDocStatus(DocumentStatusType.PENDING);
    }

    private void calculateAvgSessionTrainer(final StatisticResponse avgSessionTrainerResp,
            final List<TrainerStatsAvg> rows, final LocalDate middle, final int daysOfRange) {
        double prevAvg = 0.0;
        double currentAvg = 0.0;

        for(final TrainerStatsAvg row : rows){
            if (row.getTotalSchedule() > 0 && row.getTotalTrainer() > 0) {
                final double dayAvg = (1.0 * row.getTotalSchedule()) / row.getTotalTrainer();
                if (middle.isAfter(row.getLocalDate())) {
                    prevAvg += dayAvg;
                } else {
                    currentAvg += dayAvg;
                }
            }
        }

        avgSessionTrainerResp.setTotal((int) currentAvg / daysOfRange);
        avgSessionTrainerResp.setTrend(ReportUtils.calculateTrend(prevAvg / daysOfRange, currentAvg
                / daysOfRange));
    }

    private void calculateClientTrainerTrainer(final StatisticResponse avgSessionTrainerResp,
            final List<TrainerStatsAvg> rows, final LocalDate middle, final int daysOfRange) {
        double prevAvg = 0.0;
        double currentAvg = 0.0;

        for (final TrainerStatsAvg row : rows) {
            if (row.getTotalClient() > 0 && row.getTotalTrainer() > 0) {
                final double dayAvg = (1.0 * row.getTotalClient()) / row.getTotalTrainer();
                if (middle.isAfter(row.getLocalDate())) {
                    prevAvg += dayAvg;
                } else {
                    currentAvg += dayAvg;
                }
            }
        }

        avgSessionTrainerResp.setTotal((int) currentAvg / daysOfRange);
        avgSessionTrainerResp.setTrend(ReportUtils.calculateTrend(prevAvg / daysOfRange, currentAvg
                / daysOfRange));
    }

    private void calculateCancelledSessionTrainer(final StatisticResponse avgSessionTrainerResp,
            final List<TrainerStatsAvg> rows, final LocalDate middle, final int daysOfRange) {
        int prevAvg = 0;
        int currentAvg = 0;

        for (final TrainerStatsAvg row : rows) {
            if (middle.isAfter(row.getLocalDate())) {
                prevAvg += row.getTotalCancelledSession();
            } else {
                currentAvg += row.getTotalCancelledSession();
            }
        }

        avgSessionTrainerResp.setTotal(currentAvg / daysOfRange);
        avgSessionTrainerResp.setTrend(ReportUtils.calculateTrend(prevAvg / daysOfRange, currentAvg
                / daysOfRange));
    }

}
