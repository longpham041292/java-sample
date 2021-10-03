package asia.cmg.f8.commerce.exception;

public class DuplicateSubscriptionTypeException extends CommerceException {

    private static final long serialVersionUID = -3353932125082736055L;

    public DuplicateSubscriptionTypeException(final String message) {
        super(message);
    }
}
