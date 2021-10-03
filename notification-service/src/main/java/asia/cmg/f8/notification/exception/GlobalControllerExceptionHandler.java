package asia.cmg.f8.notification.exception;

import feign.RetryableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

/**
 * Created by on 10/20/16.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final String ERROR_MESSAGE = "Exception: {}";
    private static final Logger LOG = LoggerFactory
            .getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = {ConnectException.class, RetryableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse connectException(final Exception exception) {
        LOG.error(ERROR_MESSAGE, exception);
        return new ApiErrorResponse(5001, ApiErrorResponse.INTERNAL_SERVICE_ERROR);
    }
}
