package asia.cmg.f8.user.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * Created on 1/23/17.
 */
public class LinkUserException extends RuntimeException {

    private final transient ErrorCode errorCode;

    public LinkUserException(final ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
