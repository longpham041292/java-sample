package asia.cmg.f8.session.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.operations.ClientActions;

/**
 * Created on 12/21/16.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public enum SessionAction {
	NO_SHOW, APPROVED, CHECK_IN, CANCEL, USER_CANCEL_WITHIN_24H, PT_CANCEL, CREATED, BOOKED, TRANSFER, AUTO_EXPIRED, DEACTIVATED, AUTO_DEDUCTED, ETICKET_UPGRADED, AUTO_CANCELED, ADMIN_CANCELLED, ADMIN_COMPLETED, AUTO_BURNED;

	public static SessionAction mapSessionActionByClientAction(final SessionEntity sessionEntity,
			final ClientActions action, final Account account) {
		boolean isInternalCall = account == null;

		switch (action) {
		case NOSHOW:
			return SessionAction.NO_SHOW;
		case CHECKIN:
			return SessionAction.CHECK_IN;
		case CANCEL:
			if (isInternalCall || account.isPt()) {
				return PT_CANCEL;
			}

			final boolean isWithIn24Hours = ChronoUnit.HOURS.between(LocalDateTime.now(),
					sessionEntity.getStartTime()) < 24;
			if (isWithIn24Hours) {
				return SessionAction.USER_CANCEL_WITHIN_24H;
			}

			return SessionAction.CANCEL;
		case ACCEPT:
			return SessionAction.APPROVED;
		case DECLINE:
			return SessionAction.CANCEL;
		case RESERVE:
			return SessionAction.BOOKED;
		default:
			return SessionAction.CREATED;
		}
	}

	public static SessionAction mapSessionActionByClientAction(final CreditSessionBookingEntity sessionEntity,
			final ClientActions action, final Account account) {
		boolean isInternalCall = account == null;

		switch (action) {
		case NOSHOW:
			return SessionAction.NO_SHOW;
		case CHECKIN:
			return SessionAction.CHECK_IN;
		case CANCEL:
			if (isInternalCall || account.isPt()) {
				return PT_CANCEL;
			}

			final boolean isWithIn24Hours = ChronoUnit.HOURS.between(LocalDateTime.now(),
					sessionEntity.getStartTime()) < 24;
			if (isWithIn24Hours) {
				return SessionAction.USER_CANCEL_WITHIN_24H;
			}

			return SessionAction.CANCEL;
		case ACCEPT:
			return SessionAction.APPROVED;
		case DECLINE:
			return SessionAction.CANCEL;
		case RESERVE:
			return SessionAction.BOOKED;
		default:
			return SessionAction.CREATED;
		}
	}
}
