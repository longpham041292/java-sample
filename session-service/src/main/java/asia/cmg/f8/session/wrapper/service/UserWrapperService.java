package asia.cmg.f8.session.wrapper.service;

import asia.cmg.f8.session.repository.SessionHistoryRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.repository.TrainerUserRepository;
import asia.cmg.f8.session.wrapper.dto.TrainerRevenueDaily;
import asia.cmg.f8.session.wrapper.dto.TrainerStatsAvg;
import asia.cmg.f8.session.wrapper.dto.UserSession;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Service
public class UserWrapperService {

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private TrainerUserRepository trainerUserRepository;

    @Inject
    private SessionHistoryRepository sessionHistoryRepository;

    public List<UserSession> getClientBurnedInOneDayOfTrainer(final String ptUuid,
            final long startTime, final long endTime, final List<String> revenueStatus) {
        final List<Object[]> rows = sessionRepository.getClientBurnedInOneDayOfTrainer(ptUuid,
                startTime, endTime, revenueStatus);
        return rows.stream().map(this::processUserSession).collect(Collectors.toList());
    }

    public List<TrainerStatsAvg> getStatsTrainerClientScheduledCancelled(final long startTime,
            final long endTime, final List<String> scheduledStatus,
            final List<String> cancelledStatus) {
        final List<Object[]> rows = trainerUserRepository.getStatsTrainerClientScheduledCancelled(
                startTime, endTime,
                scheduledStatus, cancelledStatus);
        return rows.stream().map(this::processTrainerStats).collect(Collectors.toList());

    }

    public List<TrainerRevenueDaily> getTrainerRevenueDaily(final long fromDate,
            final long endTime, final List<String> revenueStatus, final String timezone) {
        final List<Object[]> rows = sessionHistoryRepository
                .getTrainerRevenueReport(fromDate, endTime, revenueStatus, timezone);
        return rows.stream().map(this::processTrainerRevenueDaily).collect(Collectors.toList());
    }

    private UserSession processUserSession(final Object[] row) {
        return new UserSession((String) row[0], (String) row[1], (String) row[2], (Integer) row[3],
                (Integer) row[4]);
    }

    private TrainerStatsAvg processTrainerStats(final Object[] row) {
        return new TrainerStatsAvg((Date) row[0], (BigInteger) row[1], (BigInteger) row[2],
                (BigInteger) row[3], (BigInteger) row[4]);
    }

    private TrainerRevenueDaily processTrainerRevenueDaily(final Object[] row) {
        return new TrainerRevenueDaily((Integer) row[0], (String) row[1], (Double) row[2],
                (Double) row[3]);
    }

}
