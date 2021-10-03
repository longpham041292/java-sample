package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/9/16.
 */
public enum ValidationType {

    NOT_VALID_TIME_SLOT("One of time slot is invalid."),

    BOOK_IN_24_HOURS("One of time slot is lesser than 24 hours."),

    BOOK_IN_PAST("One of time slot is past time."),

    DOUBLE_BOOKING("One of time slot is lesser than 24 hours."),

    OVERLAP_BOOKING("One of time slot is overlapping with booked or pending sessions."),

    BOOK_NOT_IN_PT_AVAILABILITY("One of time slot is not in availabilities of trainer."),

    BOOK_OVER_EXPIRED_DATE_SESSION_PACKAGE("One of time slot is over expired date of session package"),

    UNSUPPORTED("One of time slot is not eligible to book."),

    NOT_ENOUGH_AVAILABLE_SESSION("Don't have enough available session to book."),

    DEACTIVATED_USER("Current user is deactivated."),;

    private final String message;

    ValidationType(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
