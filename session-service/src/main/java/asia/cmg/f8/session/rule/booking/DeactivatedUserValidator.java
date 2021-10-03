package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/15/16.
 */
public class DeactivatedUserValidator implements ValidationStrategy<BooleanValidationResult> {

    @Override
    public BooleanValidationResult validate(final BookingInput bookingInput) {
        final Boolean result = bookingInput.getValidationService().checkActivatedUser(bookingInput.getUserId(), bookingInput.getTrainerId());
        return new BooleanValidationResult(!result, getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.DEACTIVATED_USER;
    }
}
