package asia.cmg.f8.profile.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.exception.ProfileUpdateException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.OperationNotSupportedException;
import java.net.ConnectException;

/**
 * Created by on 10/20/16.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String ERROR_MESSAGE = "Exception: {}";
    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = {QuestionAnswerValidateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode constraintViolationException(final QuestionAnswerValidateException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.REQUEST_INVALID;
    }

    @ExceptionHandler(value = {ConnectException.class, RetryableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode connectException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.INTERNAL_SERVICE_ERROR;
    }

    @ExceptionHandler(value = {OperationNotSupportedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorCode operationNotSupport(final OperationNotSupportedException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return ErrorCode.OPERATOR_NOT_FOUND;
    }
    
    @ExceptionHandler(value = ProfileUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode userRegistrationException(final ProfileUpdateException exception) {
        return exception.getErrorCode();
    }

}
