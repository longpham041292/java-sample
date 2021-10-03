package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

/**
 * Created on 11/24/16.
 */
public class CancelledSessionOperation implements SessionStatusOperations {

    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return CreditBookingSessionStatus.CANCELLED;
    }
}
