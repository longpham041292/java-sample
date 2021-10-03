package asia.cmg.f8.commerce.exception;

public class ActiveOrderExistedException extends CommerceException {

    private static final long serialVersionUID = 1L;

    public ActiveOrderExistedException(final String message) {
        super(message);
    }

}
