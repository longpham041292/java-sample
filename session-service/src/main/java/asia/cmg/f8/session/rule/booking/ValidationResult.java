package asia.cmg.f8.session.rule.booking;

import java.util.Optional;

/**
 * Created on 12/10/16.
 */
public class ValidationResult {

    public static final ValidationResult SUCCESS_RESULT = new ValidationResult("", Boolean.FALSE, null);

    private final String message;
    private final boolean failed;
    private final Object data;
    private ValidationType type;

    public ValidationResult(final String message, final boolean failed, final Object data) {
        this.message = message;
        this.failed = failed;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasError() {
        return failed;
    }

    public Optional<Object> getValidationData() {
        return Optional.ofNullable(data);
    }

    public ValidationType getType() {
        return type;
    }

    public void setType(final ValidationType type) {
        this.type = type;
    }
}
