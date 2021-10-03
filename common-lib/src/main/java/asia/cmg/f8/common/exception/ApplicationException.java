package asia.cmg.f8.common.exception;

/**
 * Created on 11/1/16.
 */
public class ApplicationException extends Exception {

    private static final long serialVersionUID = -4082259295625717229L;

    protected ApplicationException() {
        super();
    }

    protected ApplicationException(final String message) {
        super(message);
    }

    protected ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected ApplicationException(final Throwable cause) {
        super(cause);
    }

    public static InValid inValid(final String message) {
        return new InValid(message);
    }

    public static InValid inValid(final String message, final Throwable cause) {
        return new InValid(message, cause);
    }

    public static InValid inValid(final Throwable cause) {
        return new InValid(cause);
    }

    public static class InValid extends ApplicationException {

        private static final long serialVersionUID = 8074487495284451752L;

        private InValid() {

        }

        private InValid(final String message) {
            super(message);
        }

        private InValid(final String message, final Throwable cause) {
            super(message, cause);
        }

        private InValid(final Throwable cause) {
            super(cause);
        }
    }
}
