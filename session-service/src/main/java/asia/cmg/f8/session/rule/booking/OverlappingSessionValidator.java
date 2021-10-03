package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/15/16.
 */
public class OverlappingSessionValidator implements ValidationStrategy<BooleanValidationResult> {

    @Override
    public BooleanValidationResult validate(final BookingInput input) {
        return new BooleanValidationResult(input.getValidationService().checkOverlappingWithBookedSession(input),
                getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.OVERLAP_BOOKING;
    }
}
