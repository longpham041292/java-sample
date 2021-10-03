package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/9/16.
 */
public interface ValidationStrategy<V extends ValidationResult> {

    V validate(BookingInput input);

    ValidationType getValidationType();
}
