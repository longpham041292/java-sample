package asia.cmg.f8.session.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 12/25/16.
 */
public class PTCancelledSessionOperation implements SessionStatusOperations {

    @Override
    public SessionStatus book(final SessionEntity sessionEntity, final Account account) {
        return SessionStatus.PENDING;
    }

    @Override
    public SessionStatus defaultStatus() {
        return SessionStatus.TRAINER_CANCELLED;
    }

}
