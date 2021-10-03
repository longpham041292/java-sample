package asia.cmg.f8.session.credit.operations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.exception.BookingErrorCode;
import asia.cmg.f8.session.exception.BookingValidationException;

/**
 * Created on 11/24/16.
 */
public class ConfirmedSessionOperation implements SessionStatusOperations {

	/**
	 * Only EU can scan PT's QC code
	 */
    @Override
    public CreditBookingSessionStatus checkIn(final CreditSessionBookingEntity sessionEntity, final Account account) {
        if (account.isPt() || account.isAdmin()) {
        	throw new BookingValidationException(BookingErrorCode.UNSUPPORTED);
        }

        if (ChronoUnit.MINUTES.between(LocalDateTime.now(), sessionEntity.getStartTime()) > 15 || 
        		ChronoUnit.MINUTES.between(sessionEntity.getEndTime(), LocalDateTime.now()) > 15) {
            throw new BookingValidationException(BookingErrorCode.INVALID_CHECKIN_TIME);
        }

        return CreditBookingSessionStatus.COMPLETED;
    }

    @Override
    public CreditBookingSessionStatus noShow(final CreditSessionBookingEntity sessionEntity, final Account account) {
        if (!account.isAdmin()) {
            throw new IllegalArgumentException("EU/PT can not do this action.");
        }

        if (ChronoUnit.MINUTES.between(LocalDateTime.now(), sessionEntity.getStartTime()) > 0) {
            throw new IllegalArgumentException("Can not change session status to no-show before start time of this session");
        }

        return CreditBookingSessionStatus.CANCELLED;
    }

    @Override
    public CreditBookingSessionStatus cancel(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return cancelConfirm(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return CreditBookingSessionStatus.CONFIRMED;
    }

    private CreditBookingSessionStatus cancelConfirm(final CreditSessionBookingEntity sessionEntity, final Account account) {
        if (LocalDateTime.now().compareTo(sessionEntity.getStartTime()) > 0) {
            throw new IllegalArgumentException("Can not cancel this session after start time");
        }

        if (account.isPt()) {
            return CreditBookingSessionStatus.TRAINER_CANCELLED;
        }

        final boolean isWithIn24Hours = ChronoUnit.HOURS.between(LocalDateTime.now(),
                sessionEntity.getStartTime()) < 24;
		if (isWithIn24Hours && !account.isAdmin()) {
            return CreditBookingSessionStatus.CONFIRMED;
        }

        return CreditBookingSessionStatus.EU_CANCELLED;
    }

}
