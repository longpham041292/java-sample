package asia.cmg.f8.user.exception;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.net.ConnectException;

/**
 * Created by on 10/20/16.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String ERROR_MESSAGE = "Exception: {}";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode constraintViolationException(final ConstraintViolationException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.REQUEST_INVALID;
    }

    @ExceptionHandler(value = {InvalidFormatException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode invalidFormatException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.REQUEST_INVALID;
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorCode noHandlerFoundException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.OPERATOR_NOT_FOUND;
    }


    @ExceptionHandler(value = {ConnectException.class, RetryableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode connectException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.INTERNAL_SERVICE_ERROR;
    }
    
    @ExceptionHandler(value = {UserGridException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode userGridException(final UserGridException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        final String error = exception.getError();
        final String detail = exception.getMessage();
        ErrorCode errorCode = ErrorCode.REQUEST_INVALID.withError(error, detail);
        if (detail.contains("email")) {
            errorCode = new ErrorCode(9002, error, detail);
        } else if (detail.contains("username")) {
            
            errorCode = new ErrorCode(9001, error, detail);
        } else if (detail.contains("fbId")){
            errorCode = new ErrorCode(9003, error, detail);
        }
        return errorCode;
    }

    @ExceptionHandler(value = LinkUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode linkUserExceptionHandler(final LinkUserException exception) {
        return exception.getErrorCode();
    }

    @ExceptionHandler(value = InvalidEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode invalidEmailExceptionHandler(final InvalidEmailException exception) {
        return exception.getErrorCode();
    }

    @ExceptionHandler(value = UserRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode userRegistrationException(final UserRegistrationException exception) {
        return exception.getErrorCode();
    }
}
