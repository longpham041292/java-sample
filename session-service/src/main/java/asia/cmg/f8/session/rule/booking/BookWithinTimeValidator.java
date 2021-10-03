package asia.cmg.f8.session.rule.booking;

import asia.cmg.f8.session.rule.config.ValidatorProperties;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 12/15/16.
 */
public class BookWithinTimeValidator implements ValidationStrategy<BooleanValidationResult> {

    private final ValidatorProperties validatorProperties;

    public BookWithinTimeValidator(final ValidatorProperties validatorProperties) {
        this.validatorProperties = validatorProperties;
    }

    @Override
    public BooleanValidationResult validate(final BookingInput input) {

        final double maxTime = validatorProperties.getMaxAllowedTimeInHours();

        final Boolean result = input.getReservationSlotList().stream()
                .anyMatch(slot -> (ChronoUnit.HOURS.between(LocalDateTime.now(), slot.getStartTime()) < maxTime)) &&
                input.getAccount().isEu();
        return new BooleanValidationResult(result, getValidationType().getMessage());
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.BOOK_IN_24_HOURS;
    }
}
