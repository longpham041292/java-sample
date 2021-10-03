package asia.cmg.f8.session.exception;

import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.operations.ClientActions;

/**
 * Created by on 11/24/16.
 */
public class UnsupportedStatusTransitionException extends RuntimeException {

    private static final long serialVersionUID = -2043478563218252448L;

    private final ClientActions action;
    private final SessionStatus currentStatus;

    public UnsupportedStatusTransitionException(final ClientActions action, final SessionStatus currentStatus) {
        super();
        this.action = action;
        this.currentStatus = currentStatus;
    }

    public ClientActions getAction() {
        return action;
    }

    public SessionStatus getCurrentStatus() {
        return currentStatus;
    }

    @Override
    public String getMessage() {
        return String.format("Don't support action \'%1$s\' on status %2$s", getAction(), getCurrentStatus());
    }
}
