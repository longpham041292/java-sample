package asia.cmg.f8.common.web.util;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.OperationNotSupportedException;

/**
 * Created on 11/17/16.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_MESSAGE = "Exception: {}";

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorCode accessDeniedException() {
        return ErrorCode.FORBIDDEN;
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode illegalArgumentException(final IllegalArgumentException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.REQUEST_INVALID;
    }

    @ExceptionHandler(value = {OperationNotSupportedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorCode operationNotSupport(final OperationNotSupportedException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.OPERATOR_NOT_FOUND;
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode generalException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    @ExceptionHandler(value = {UserGridException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode userGridException(final UserGridException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ErrorCode(4003, exception.getError(), exception.getMessage());
    }
}
