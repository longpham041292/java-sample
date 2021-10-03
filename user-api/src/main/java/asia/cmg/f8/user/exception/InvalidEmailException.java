package asia.cmg.f8.user.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * @author tung.nguyenthanh
 */
public class InvalidEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient ErrorCode errorCode;

    public InvalidEmailException(final ErrorCode errorCode, final Throwable cause) {
        super(errorCode.getDetail(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
