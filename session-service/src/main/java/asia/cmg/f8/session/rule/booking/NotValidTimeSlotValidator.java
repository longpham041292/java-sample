package asia.cmg.f8.session.rule.booking;

import java.time.temporal.ChronoUnit;

/**
 * Created on 12/15/16.
 */
public class NotValidTimeSlotValidator implements ValidationStrategy<BooleanValidationResult> {

    @Override
    public BooleanValidationResult validate(final BookingInput bookingInput) {
        final boolean isValid = bookingInput.getReservationSlotList().stream().anyMatch(slot ->
                ChronoUnit.MINUTES.between(slot.getStartTime(), slot.getEndTime()) < 60);
        return new BooleanValidationResult(isValid, getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.NOT_VALID_TIME_SLOT;
    }
}
