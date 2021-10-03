package asia.cmg.f8.common.exception;

/**
 * user-grid exception
 */
public class UserGridException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String error;
    private final String message;

    public UserGridException(final String error, final String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
