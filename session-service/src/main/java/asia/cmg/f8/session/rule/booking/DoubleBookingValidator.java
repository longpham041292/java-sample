package asia.cmg.f8.session.rule.booking;

import asia.cmg.f8.session.dto.BookingResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Created on 12/15/16.
 */
public class DoubleBookingValidator implements ValidationStrategy<ValidationResult> {

    @Override
    public ValidationResult validate(final BookingInput bookingInput) {

        // TODO check null?
        final BookingResponse bookingResponse = bookingInput.getValidationService().checkDoubleBooking(bookingInput);
        return new ValidationResult(StringUtils.EMPTY, !bookingResponse.getResult(), bookingResponse);
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.DOUBLE_BOOKING;
    }
}
