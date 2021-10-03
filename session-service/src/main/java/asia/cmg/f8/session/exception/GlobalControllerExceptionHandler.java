package asia.cmg.f8.session.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.OperationNotSupportedException;
import javax.validation.ConstraintViolationException;

/**
 * Created by on 10/20/16.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String ERROR_MESSAGE = "Exception: {}";


    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = {UnsupportedStatusTransitionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse unsupportedStatusTransitionException(final UnsupportedStatusTransitionException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ApiErrorResponse(4001, ApiErrorResponse.REQUEST_INVALID, exception.getMessage());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse constraintViolationException(final ConstraintViolationException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ApiErrorResponse(4001, ApiErrorResponse.REQUEST_INVALID, exception.getMessage());
    }

    @ExceptionHandler(value = {OperationNotSupportedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse operationNotSupport(final OperationNotSupportedException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ApiErrorResponse(4004, exception.getMessage(), exception.getMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse illegalArgumentException(final IllegalArgumentException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ApiErrorResponse(4001, ApiErrorResponse.REQUEST_INVALID, exception.getMessage());
    }

    @ExceptionHandler(value = {BookingConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorCode bookingConstraintViolationException(final BookingConstraintViolationException exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return exception.getBookingErrorCode();
    }

    @ExceptionHandler(BookingValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorCode bookingValidationExceptionHandler(final BookingValidationException exception) {
        return exception.errorCode();
    }
}
