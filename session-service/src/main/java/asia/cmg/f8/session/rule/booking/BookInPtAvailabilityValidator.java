package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/15/16.
 */
public class BookInPtAvailabilityValidator implements ValidationStrategy<BooleanValidationResult> {

    @Override
    public BooleanValidationResult validate(final BookingInput input) {
        //TODO : Implement time slot is not in trainer's availabilities
        return new BooleanValidationResult(false, getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.BOOK_NOT_IN_PT_AVAILABILITY;
    }
}
