package asia.cmg.f8.session.operations;

import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 1/3/17.
 */
public class ExpiredSessionOperation implements SessionStatusOperations {
    @Override
    public SessionStatus defaultStatus() {
        return SessionStatus.EXPIRED;
    }
}
