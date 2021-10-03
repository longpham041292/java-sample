package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

public class DeductedSessionOperation implements SessionStatusOperations {

	@Override
	public CreditBookingSessionStatus defaultStatus() {
		return CreditBookingSessionStatus.DEDUCTED;
	}
}
