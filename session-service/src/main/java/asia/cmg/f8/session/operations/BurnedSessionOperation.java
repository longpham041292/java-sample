package asia.cmg.f8.session.operations;

import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 12/25/16.
 */
public class BurnedSessionOperation implements SessionStatusOperations {

    @Override
    public SessionStatus defaultStatus() {
        return SessionStatus.BURNED;
    }

}
