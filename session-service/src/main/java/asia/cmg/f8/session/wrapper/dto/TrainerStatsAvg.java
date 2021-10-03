package asia.cmg.f8.session.wrapper.dto;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

public class TrainerStatsAvg {

    private final LocalDate localDate;
    private final int totalSchedule;
    private final int totalTrainer;
    private final int totalClient;
    private final int totalCancelledSession;

    public TrainerStatsAvg(final Date localDate, final BigInteger totalSchedule,
            final BigInteger totalClient, final BigInteger totalTrainer, 
            final BigInteger totalCancelledSession) {
        super();
        this.localDate = localDate.toLocalDate();
        this.totalSchedule = totalSchedule.intValue();
        this.totalTrainer = totalTrainer.intValue();
        this.totalClient = totalClient.intValue();
        this.totalCancelledSession = totalCancelledSession.intValue();
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public int getTotalSchedule() {
        return totalSchedule;
    }

    public int getTotalTrainer() {
        return totalTrainer;
    }

    public int getTotalClient() {
        return totalClient;
    }

    public int getTotalCancelledSession() {
        return totalCancelledSession;
    }

}
