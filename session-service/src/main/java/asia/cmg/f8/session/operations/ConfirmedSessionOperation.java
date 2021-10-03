package asia.cmg.f8.session.operations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 11/24/16.
 */
public class ConfirmedSessionOperation implements SessionStatusOperations {

    @Override
    public SessionStatus checkIn(final SessionEntity sessionEntity, final Account account) {
        if (account.isEu() && !account.isAdmin()) {
            throw new IllegalArgumentException("End User can not do this action.");
        }

        if (ChronoUnit.MINUTES.between(LocalDateTime.now(), sessionEntity.getStartTime()) > 30) {
            throw new IllegalArgumentException("Can not change session status to check-in before start time in 30 minutes of this session");
        }

        return SessionStatus.COMPLETED;
    }

    @Override
    public SessionStatus noShow(final SessionEntity sessionEntity, final Account account) {
        if (account.isEu() && !account.isAdmin()) {
            throw new IllegalArgumentException("End User can not do this action.");
        }

        if (ChronoUnit.MINUTES.between(LocalDateTime.now(), sessionEntity.getStartTime()) > 0) {
            throw new IllegalArgumentException("Can not change session status to no-show before start time of this session");
        }

        return SessionStatus.BURNED;
    }

    @Override
    public SessionStatus cancel(final SessionEntity sessionEntity, final Account account) {
        return cancelConfirm(sessionEntity, account);
    }

    @Override
    public SessionStatus decline(final SessionEntity sessionEntity, final Account account) {
        return cancelConfirm(sessionEntity, account);
    }

    @Override
    public SessionStatus defaultStatus() {
        return SessionStatus.CONFIRMED;
    }

    private SessionStatus cancelConfirm(final SessionEntity sessionEntity, final Account account) {
        if (LocalDateTime.now().compareTo(sessionEntity.getStartTime()) > 0) {
            throw new IllegalArgumentException("Can not cancel this session after start time");
        }

        if (account.isPt()) {
            return SessionStatus.TRAINER_CANCELLED;
        }

        final boolean isWithIn24Hours = ChronoUnit.HOURS.between(LocalDateTime.now(),
                sessionEntity.getStartTime()) < 24;
		if (isWithIn24Hours && !account.isAdmin()) {
            return SessionStatus.EU_CANCELLED;
        }

        return SessionStatus.CANCELLED;
    }

}
