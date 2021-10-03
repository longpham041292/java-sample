package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

/**
 * Created on 12/25/16.
 */
public class BurnedSessionOperation implements SessionStatusOperations {

    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return CreditBookingSessionStatus.BURNED;
    }
}
