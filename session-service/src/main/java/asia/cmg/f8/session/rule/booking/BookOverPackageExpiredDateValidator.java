package asia.cmg.f8.session.rule.booking;


/**
 * Created on 12/15/16.
 */
public class BookOverPackageExpiredDateValidator implements ValidationStrategy<BooleanValidationResult> {

    @Override
    public BooleanValidationResult validate(final BookingInput input) {
        final boolean hasError = input.getValidationService()
                .validateBookingInputTimeRange(input);

        return new BooleanValidationResult(hasError, getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.BOOK_OVER_EXPIRED_DATE_SESSION_PACKAGE;
    }
}
