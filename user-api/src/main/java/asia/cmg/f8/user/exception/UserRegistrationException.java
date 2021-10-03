package asia.cmg.f8.user.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * Created on 2/17/17.
 */
public class UserRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final transient ErrorCode errorCode;

    public UserRegistrationException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public UserRegistrationException(final Throwable cause, final ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
