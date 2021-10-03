package asia.cmg.f8.session.rule.booking;

/**
 * Created on 12/10/16.
 */
public class BooleanValidationResult extends ValidationResult {

    public BooleanValidationResult(final boolean hasError, final String message) {
        super(message, hasError, null);
    }
}
