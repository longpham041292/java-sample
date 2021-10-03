package asia.cmg.f8.session.rule.booking;

import java.util.Iterator;
import java.util.Set;

/**
 * Created on 12/9/16.
 */
@SuppressWarnings("squid:S2384")
public class BookingValidationContext {
    private final Set<ValidationStrategy> bookingValidationStrategies;

    public BookingValidationContext(final Set<ValidationStrategy> bookingValidationStrategies) {
        this.bookingValidationStrategies = bookingValidationStrategies;
    }

    public ValidationResult execute(final BookingInput bookingInput) {
        ValidationStrategy bookingValidation;
        for (final Iterator<ValidationStrategy> iterator = bookingValidationStrategies.iterator(); iterator.hasNext(); ) {
            bookingValidation = iterator.next();
            final ValidationResult result = bookingValidation.validate(bookingInput);
            result.setType(bookingValidation.getValidationType());
            if (result.hasError()) {
                return result;
            }
        }
        return ValidationResult.SUCCESS_RESULT; // return success as default.
    }
}
