package asia.cmg.f8.profile.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * Created on 2/17/17.
 */
public class ProfileUpdateException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final transient ErrorCode errorCode;

    public ProfileUpdateException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ProfileUpdateException(final Throwable cause, final ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
