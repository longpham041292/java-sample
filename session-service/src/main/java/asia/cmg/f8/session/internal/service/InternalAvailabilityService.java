package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.repository.AvailabilityRepository;
import asia.cmg.f8.session.repository.TrainerUserRepository;
import asia.cmg.f8.session.service.AvailabilityService;
import asia.cmg.f8.session.utils.TimeSlotUtil;

import org.apache.commons.collections.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class InternalAvailabilityService {

    public static final Logger LOGGER = LoggerFactory.getLogger(InternalAvailabilityService.class);

    private final AvailabilityService availabilityService;
    private final TrainerUserRepository trainerUserRepository;
    private final AvailabilityRepository availabilityRepository;
    private final SessionProperties sessionProperties;


    public InternalAvailabilityService(final AvailabilityService availabilityService,
            final TrainerUserRepository trainerUserRepository,
            final AvailabilityRepository availabilityRepository,
            final SessionProperties sessionProperties) {
        super();
        this.availabilityService = availabilityService;
        this.trainerUserRepository = trainerUserRepository;
        this.availabilityRepository = availabilityRepository;
        this.sessionProperties = sessionProperties;
    }


    @Transactional
    @Async
    public void processAddAvailability() {
        final List<Object[]> activePTs = this.findAllAvailablePT();
        if (CollectionUtils.isNotEmpty(activePTs)) {
            final int addMonth = Integer.valueOf(sessionProperties.getCurrentPTMonthsAhead());
            if (addMonth < 1) {
                LOGGER.error("Number of month should be greater than 0");
                return;
            }
            LOGGER.info(String.format("Found %s active PT. Ready to add availability.",
                    activePTs.size()));
            activePTs.stream().forEach(row -> this.addPTAvailability(row, addMonth));
        }
    }

    @Transactional
    @Async
    public void removeOldAvailability() {
        final int month = Integer.valueOf(sessionProperties.getRemoveAvailabilityMonthsPast());
        if (month < 1) {
            LOGGER.error("Number of month when delete old availability should be greate than 0");
            return;
        }

        final LocalDateTime pastMonth = LocalDate.now().minusMonths(month - 1).withDayOfMonth(1)
                .atTime(LocalTime.MIDNIGHT);
        final int deleteRows = availabilityRepository.deleteAllOldAvailability(pastMonth);
        LOGGER.info(String.format("Deleted %s rows for old availability until %s", deleteRows,
                pastMonth.toString()));
    }

    @Transactional(readOnly = true)
    private List<Object[]> findAllAvailablePT() {
        return trainerUserRepository.findAllActivePTsWithLatestAvailability();
    }

    /**
     * We create a new transaction for each PT to insert availabilities. Any
     * fails from one PT - that will trigger rollback - will have no effect to
     * others
     * 
     * @param trainer
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void addPTAvailability(final Object[] row, final int month) {
        final String userId = (String) row[0];
        final Timestamp maxDate = (Timestamp) row[1];

        LocalDateTime startTime = LocalDateTime.now();
        if (maxDate != null && startTime.isBefore(maxDate.toLocalDateTime())) {
            startTime = maxDate.toLocalDateTime();
        }

        startTime = TimeSlotUtil.roundToSlotTime(startTime);

        final LocalDateTime endOfMonth = LocalDate.now().plusMonths(month).withDayOfMonth(1)
                .minusDays(1).atTime(LocalTime.MAX);
        if (startTime.isBefore(endOfMonth)) {
            availabilityService.setupAvailablityForTrainer(userId, startTime, endOfMonth);
        }
    }
}
