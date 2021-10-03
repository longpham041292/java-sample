package asia.cmg.f8.session.exception;

import asia.cmg.f8.session.rule.booking.ValidationType;

/**
 * Created by on 11/24/16.
 */
public class BookingConstraintViolationException extends RuntimeException {

    private static final long serialVersionUID = -2043478563218252448L;

    private final ValidationType validationType;

    public BookingConstraintViolationException(final ValidationType type) {
        super(type.getMessage());
        this.validationType = type;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public BookingErrorCode getBookingErrorCode() {
        switch (validationType) {
            case NOT_VALID_TIME_SLOT:
                return BookingErrorCode.NOT_VALID_TIME_SLOT;
            case BOOK_IN_PAST:
                return BookingErrorCode.BOOK_IN_PAST;
            case BOOK_IN_24_HOURS:
                return BookingErrorCode.BOOK_IN_24_HOURS;
            case OVERLAP_BOOKING:
                return BookingErrorCode.OVERLAP_BOOKING;
            case BOOK_NOT_IN_PT_AVAILABILITY:
                return BookingErrorCode.BOOK_NOT_IN_PT_AVAILABILITY;
            case BOOK_OVER_EXPIRED_DATE_SESSION_PACKAGE:
                return BookingErrorCode.BOOK_OVER_EXPIRED_DATE_ORDER;
            case NOT_ENOUGH_AVAILABLE_SESSION:
                return BookingErrorCode.NOT_ENOUGH_AVAILABLE_SESSION;
            case DEACTIVATED_USER:
                return BookingErrorCode.DEACTIVATED_USER;
            default:
                return BookingErrorCode.UNSUPPORTED;
        }
    }
}
