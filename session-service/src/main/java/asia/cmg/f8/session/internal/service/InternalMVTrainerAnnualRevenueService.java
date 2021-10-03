package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.TimeRange;
import asia.cmg.f8.session.entity.MVTrainerAnnualRevenueEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.TrainerAnnualRevenueRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.TrainerRevenueDaily;
import asia.cmg.f8.session.wrapper.service.UserWrapperService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Service
@SuppressWarnings("PMD")
public class InternalMVTrainerAnnualRevenueService {

    public static final Logger LOG = LoggerFactory
            .getLogger(InternalMVTrainerAnnualRevenueService.class);

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Inject
    private TrainerAnnualRevenueRepository trainerAnnualRevenueRepository;

    @Inject
    private UserWrapperService userWrapperService;

    /**
     * If this is the first time running. Get all data until end of yesterday
     * Otherwise, get the latest run date: If latest run date is before
     * yesterday, then get date from next day to end of yesterday. Else, job has
     * already run. Do nothing.
     * 
     * @return time range
     */
    public TimeRange getSessionInTimeRange() {
        final Optional<Object> latestRunOpt = trainerAnnualRevenueRepository.findMaxByLastRun();

        final TimeRange timeRange = new TimeRange();
        if (latestRunOpt.isPresent()) {
            final LocalDateTime latestRun = (LocalDateTime) latestRunOpt.get();
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
    public void runTrainerAnnualRevenue() {
        final TimeRange timeRange = this.getSessionInTimeRange();
        if (timeRange == null) {
            LOG.info("Job has already run for yesterday data!");
            return;
        }
        LOG.info("Start query session history in time range {} to {}", timeRange.getFromDateTime(),
                timeRange.getToDateTime());

        final LocalDateTime now = LocalDateTime.now();

        final String currentZone = ZoneDateTimeUtils.getCurrentZoneOffset().getId();

        final List<String> revenueStatus = SessionStatus.getRevenueSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());

        final List<TrainerRevenueDaily> entities = userWrapperService.getTrainerRevenueDaily(
                timeRange.getFromDateTime(), timeRange.getToDateTime(), revenueStatus, currentZone);

        LOG.info("Find {} records for trainer revenue", entities.size());
        for (int count = 0; count < entities.size();) {
            final MVTrainerAnnualRevenueEntity entity = buildEntity(entities.get(count));
            if (entity != null) {
                LOG.info("Write or update record {}: {}", entity.getYear(), entity.getPtUuid());
                entity.setLastRun(now);
                trainerAnnualRevenueRepository.save(entity);
                if (++count % batchSize == 0) {
                    trainerAnnualRevenueRepository.flush();
                }
            }
        }

        // flush remaining records, if any
        trainerAnnualRevenueRepository.flush();
    }

    /**
     * Check if current entity exists, if yes we just update pt fee and
     * commission. Otherwise, create new entity
     * 
     * @param record
     * @return
     */
    private MVTrainerAnnualRevenueEntity buildEntity(final TrainerRevenueDaily record) {
        final Integer year = record.getYear();
        final String ptUuid = record.getPtUuid();
        if (!StringUtils.isEmpty(ptUuid) && year != null) {
            final Optional<MVTrainerAnnualRevenueEntity> entityOpt = trainerAnnualRevenueRepository
                    .findOneByYearAndPtUuid(year, ptUuid);
            final MVTrainerAnnualRevenueEntity entity;
            final double ptFee = ReportUtils.formatDoubleNullToZero(record.getPtFee());
            final double commission = ReportUtils.formatDoubleNullToZero(record.getCommission());
            if (entityOpt.isPresent()) {
                entity = entityOpt.get();
                entity.setPtTotalFee(ReportUtils.formatDoubleNullToZero(entity.getPtTotalFee())
                        + ptFee);
                entity.setTotalCommission(ReportUtils.formatDoubleNullToZero(entity
                        .getTotalCommission()) + commission);
            } else {
                entity = new MVTrainerAnnualRevenueEntity();
                entity.setYear(year);
                entity.setPtUuid(ptUuid);
                entity.setPtTotalFee(ptFee);
                entity.setTotalCommission(commission);
            }
            return entity;
        }

        return null;
    }

}
