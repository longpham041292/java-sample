package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

/**
 * Created on 1/3/17.
 */
public class EUCancelledSessionOperation implements SessionStatusOperations {
    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return CreditBookingSessionStatus.EU_CANCELLED;
    }
}
