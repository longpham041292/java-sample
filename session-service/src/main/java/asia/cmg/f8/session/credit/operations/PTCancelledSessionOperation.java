package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;

/**
 * Created on 12/25/16.
 */
public class PTCancelledSessionOperation implements SessionStatusOperations {

    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return CreditBookingSessionStatus.TRAINER_CANCELLED;
    }

}
