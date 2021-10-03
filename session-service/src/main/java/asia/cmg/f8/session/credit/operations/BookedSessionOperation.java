package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;

/**
 * Created on 11/24/18.
 */
public class BookedSessionOperation implements SessionStatusOperations {

	@Override
	public CreditBookingSessionStatus cancel(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (isAllowedOnCancelAction(sessionEntity, account)) {
			return CreditBookingSessionStatus.CANCELLED;
		}
		return CreditBookingSessionStatus.BOOKED;
	}
	
	@Override
	public CreditBookingSessionStatus confirm(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (isAllowedOnConfirmAction(sessionEntity, account)) {
			return CreditBookingSessionStatus.CONFIRMED;
		}
		return CreditBookingSessionStatus.BOOKED;
	}

	@Override
	public CreditBookingSessionStatus reject(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (isAllowedOnRejectAction(sessionEntity, account)) {
			return CreditBookingSessionStatus.REJECTED;
		}
		return CreditBookingSessionStatus.BOOKED;
	}

	@Override
	public CreditBookingSessionStatus defaultStatus() {
		return CreditBookingSessionStatus.BOOKED;
	}

	private boolean isAllowedOnRejectAction(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (account.isAdmin()) {
			return true;
		}
		
		return account.type() != null && !account.uuid().equalsIgnoreCase(sessionEntity.getBookedBy());
	}
	
	private boolean isAllowedOnConfirmAction(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (account.isAdmin()) {
			return true;
		}
		
		return account.type() != null && !account.uuid().equalsIgnoreCase(sessionEntity.getBookedBy());
	}
	
	private boolean isAllowedOnCancelAction(final CreditSessionBookingEntity sessionEntity, final Account account) {
		if (account.isAdmin()) {
			return true;
		}
		
		return account.type() != null && account.uuid().equalsIgnoreCase(sessionEntity.getBookedBy());
	}
}
