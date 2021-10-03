package asia.cmg.f8.commerce.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@ControllerAdvice
@ResponseBody
public class CommerceExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommerceExceptionHandler.class);
    private static final String ERROR_MESSAGE = "{}: {}";

    @ExceptionHandler(value = { DuplicateProductException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode illegalArgumentException(final DuplicateProductException exception) {
        LOG.error(ERROR_MESSAGE, DuplicateProductException.class, exception.getMessage());
        return CommerceErrorCode.PRODUCT_EXIST;
    }

    @ExceptionHandler(value = { DuplicateSubscriptionTypeException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode illegalArgumentException(final DuplicateSubscriptionTypeException exception) {
        LOG.error(ERROR_MESSAGE, DuplicateSubscriptionTypeException.class, exception.getMessage());
        return CommerceErrorCode.SUBSCRIBE_TYPE_EXIST;
    }

    @ExceptionHandler(value = { ActiveOrderExistedException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode activeOrderExistedException(final ActiveOrderExistedException exception) {
        LOG.error(ERROR_MESSAGE, ActiveOrderExistedException.class, exception.getMessage());
        return CommerceErrorCode.ACTIVE_CONTRACT_EXIST;
    }

    @ExceptionHandler(value = { RequestPaymentException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode requestPaymentException(final RequestPaymentException exception) {
        LOG.error(ERROR_MESSAGE, RequestPaymentException.class, exception.getMessage());
        return CommerceErrorCode.PROCESS_PAYMENT_ERROR;
    }

    @ExceptionHandler(value = { PendingOrderException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorCode pendingOrderException(final PendingOrderException exception) {
        LOG.error(ERROR_MESSAGE, exception.getClass(), exception.getMessage());
        return CommerceErrorCode.PENDING_ORDER_ERROR;
    }
    
    @ExceptionHandler(value = { CommerceException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCode internalCommerceException(final CommerceException exception) {
        LOG.error(ERROR_MESSAGE, CommerceException.class, exception.getMessage());
        return CommerceErrorCode.COMMERCE_INTERNAL_ERROR;
    }

}
