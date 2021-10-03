package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.util.NumberUtils;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.entity.MVOrderReconcile;
import asia.cmg.f8.session.repository.MVOrderReconcileRepository;
import asia.cmg.f8.session.repository.SessionHistoryRepository;
import asia.cmg.f8.session.utils.ReportUtils;
import asia.cmg.f8.session.wrapper.dto.OrderSessionStatusDaily;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("PMD")
public class InternalMVOrderReconcileService {

    public static final Logger LOG = LoggerFactory
            .getLogger(InternalMVOrderReconcileService.class);

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    private final MVOrderReconcileRepository orderReconcileRepository;

    private final SessionHistoryRepository sessionHistoryRepository;

    private final OrderSessionWrapperService orderSessionWrapperService;

    public InternalMVOrderReconcileService(
            final MVOrderReconcileRepository orderReconcileRepository,
            final SessionHistoryRepository sessionHistoryRepository,
            final OrderSessionWrapperService orderSessionWrapperService) {
        this.orderReconcileRepository = orderReconcileRepository;
        this.sessionHistoryRepository = sessionHistoryRepository;
        this.orderSessionWrapperService = orderSessionWrapperService;
    }

    /**
     * Get date range to run reconcile.
     * - If find last run, run from next day -> yesterday
     * - if not found, run from start day of session history event -> yesterday
     * @return
     */
    public DateRange getRunReconcileDateRange() {
        final Optional<Object> lastReconcileOpt = orderReconcileRepository
                .findLatestReconcileDate();

        final DateRange dateRange = new DateRange();
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        if (lastReconcileOpt.isPresent()) {
            final LocalDate lastReconcile = (LocalDate) lastReconcileOpt.get();
            if (ReportUtils.isBeforeYesterday(lastReconcile)) {
                dateRange.setFromDate(lastReconcile.plusDays(1));
            } else {
                return null;
            }
        } else {
            final Optional<Object> earlyOpt = sessionHistoryRepository
                    .findFirstSessionHistoryDate();
            if (earlyOpt.isPresent()) {
                final Date date = (Date) earlyOpt.get();
                dateRange.setFromDate(date.toLocalDate());
            } else {
                LOG.info("No session history so far");
                return null;
            }
        }

        dateRange.setToDate(yesterday);
        return dateRange;
    }

    @Transactional
    @Async
    public void buildOrderReconcile() {
        final DateRange dateRange = this.getRunReconcileDateRange();
        if (dateRange == null) {
            LOG.info("Job has already run for yesterday data!");
            return;
        }
        LOG.info("Start query session history in date range {} to {}", dateRange.getFromDate(),
                dateRange.getToDate());

        final String currentOffset = ZoneDateTimeUtils.getCurrentZoneOffset().getId();

        LocalDate runDate = dateRange.getFromDate();
        while (!runDate.isAfter(dateRange.getToDate())) {
            // start clone reconcile orders from previous day
            final LocalDate preRunDate = runDate.minusDays(1);
            LOG.info("Start clone order reconcile from {} to {}", preRunDate, runDate);
            final Integer clonedRow = orderReconcileRepository.cloneDataToNextDay(preRunDate);
            LOG.info("Record cloned {}", clonedRow);

            final List<OrderSessionStatusDaily> orderSessions = orderSessionWrapperService
                    .getOrderSessionReconcile(runDate, currentOffset);
            LOG.info("Total order need to update reconcile: {}", orderSessions.size());
            for (int count = 0; count < orderSessions.size(); count++) {
                final MVOrderReconcile entity = processRecord(orderSessions.get(count), runDate);
                LOG.info("Store record {}:{}", entity.getRecDate(), entity.getOrderUuid());
                orderReconcileRepository.save(entity);
                if ((count + 1) % batchSize == 0) {
                    orderReconcileRepository.flush();
                }
            }

            orderReconcileRepository.flush();
            runDate = runDate.plusDays(1);
        }
    }

    private MVOrderReconcile processRecord(final OrderSessionStatusDaily row,
            final LocalDate runDate) {
        final Optional<MVOrderReconcile> currentOpt = orderReconcileRepository
                .findOneByOrderUuidAndRecDate(row.getUuid(), runDate);
        if (currentOpt.isPresent()) {
            final MVOrderReconcile currentEntity = currentOpt.get();
            LOG.info("Record has been cloned: {} - {}, updating ... ", currentEntity.getRecDate(),
                    currentEntity.getOrderUuid());
            return updateClonedEntity(currentEntity, row);
        } else {
            LOG.info("Not found record for yesterday, creating new ...");
            return buildNewEntity(row, runDate);
        }
    }

    private MVOrderReconcile updateClonedEntity(final MVOrderReconcile currentEntity,
            final OrderSessionStatusDaily row) {
        updateEntity(currentEntity, row);
        return currentEntity;
    }

    private MVOrderReconcile buildNewEntity(final OrderSessionStatusDaily row,
            final LocalDate runDate) {
        final MVOrderReconcile entity = new MVOrderReconcile();
        entity.setOrderUuid(row.getUuid());
        entity.setRecDate(runDate);
        updateEntity(entity, row);
        return entity;
    }

    private void updateEntity(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setNumOfSession(row.getNumOfSession());

        if (row.getCommission() != null) {
            entity.setCommission(NumberUtils.roundUpDouble(row.getCommission()));
        }

        if (row.getPtFee() != null) {
            entity.setPtFee(NumberUtils.roundUpDouble(row.getPtFee()));
        }

        // expired session status is set by a cron job for expired order.
        entity.setOrderActive(row.getExpired() == 0);

        updateOpen(entity, row);
        updatePending(entity, row);
        updateConfirm(entity, row);
        updateEuCancelled(entity, row);
        updatePtCancelled(entity, row);
        updateCancelled(entity, row);
        updateBurned(entity, row);
        updateCompleted(entity, row);
        updateExpired(entity, row);
        updateTransferred(entity, row);
    }

    private void updateOpen(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setOpen(entity.getOpen() + row.getNewOpen() - row.getOldOpen());
    }

    private void updatePending(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setPending(entity.getPending() + row.getNewPending() - row.getOldPending());
    }

    private void updateConfirm(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setConfirmed(row.getNewConfirmed());
    }

    private void updateEuCancelled(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setEucancelled(row.getEucancelled());
    }

    private void updatePtCancelled(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setPtcancelled(entity.getPtcancelled() + row.getNewPtcancelled()
                - row.getOldPtcancelled());
    }

    private void updateCancelled(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setCancelled(entity.getCancelled() + row.getNewCancelled() - row.getOldCancelled());
    }

    private void updateBurned(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setBurned(row.getBurned());
    }

    private void updateCompleted(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setCompleted(row.getCompleted());
    }

    private void updateExpired(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setExpired(row.getExpired());
    }

    private void updateTransferred(final MVOrderReconcile entity, final OrderSessionStatusDaily row) {
        entity.setTransferred(row.getNewTransferred());
    }

    private static class DateRange {

        private LocalDate fromDate;
        private LocalDate toDate;

        public LocalDate getFromDate() {
            return fromDate;
        }

        public void setFromDate(final LocalDate fromDate) {
            this.fromDate = fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public void setToDate(final LocalDate toDate) {
            this.toDate = toDate;
        }

    }

}
