package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.TimeRange;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.MVSessionStatsDailyRepository;
import asia.cmg.f8.session.utils.ReportUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import javax.inject.Inject;

@Service
public class InternalMVSessionStatsDailyService {

    public static final Logger LOG = LoggerFactory
            .getLogger(InternalMVSessionStatsDailyService.class);

    @Inject
    private MVSessionStatsDailyRepository sessionStatsDailyRepository;

    public TimeRange getSessionStatsInTimeRange() {
        final Optional<Object> latestRunOpt = sessionStatsDailyRepository.findMaxByStatsDate();
        LOG.info("--- latestRunOpt {}",latestRunOpt);
        final TimeRange timeRange = new TimeRange();
        if (latestRunOpt.isPresent()) {
            final LocalDateTime latestRun = (LocalDateTime) latestRunOpt.get();
            LOG.info("--- latestRun {}",latestRun);
            if (ReportUtils.isBeforeYesterday(latestRun)) {
                timeRange.setFromDateTime(ReportUtils.getCurrentSecond(ReportUtils
                        .getStartOfRunTime(latestRun)));
            } else {
                return null;
            }
        } else {
            timeRange.setFromDateTime(0L);
        }
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        timeRange.setToDateTime(ReportUtils.getCurrentSecond(yesterday.atTime(LocalTime.MAX)));
        return timeRange;
    }

    @Transactional
    @Async
    public void runSessionStatsDailyView() {
        final TimeRange timeRange = this.getSessionStatsInTimeRange();
        LOG.info("--- session dailyVire");
        if (timeRange == null) {
            LOG.info("Job has already run for yesterday data!");
            return;
        }
        LOG.info("Start query session history STATS {} to {}", timeRange.getFromDateTime(),
                timeRange.getToDateTime());

        final String currentZone = ZoneDateTimeUtils.getCurrentZoneOffset().getId();
        LOG.info(currentZone);

        final int insertRows = sessionStatsDailyRepository.insertSessionStatsByTimeRange(
                SessionStatus.OPEN.name(), SessionStatus.PENDING.name(),
                SessionStatus.CONFIRMED.name(), SessionStatus.TRAINER_CANCELLED.name(),
                SessionStatus.BURNED.name(), SessionStatus.CANCELLED.name(),
                SessionStatus.COMPLETED.name(), SessionStatus.EU_CANCELLED.name(),
                SessionStatus.EXPIRED.name(), SessionStatus.TRANSFERRED.name(),
                timeRange.getFromDateTime(), timeRange.getToDateTime(), currentZone);

        LOG.info("Insert {} rows to session stats daily mv table", insertRows);
    }

}
