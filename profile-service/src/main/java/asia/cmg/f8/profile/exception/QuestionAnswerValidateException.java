package asia.cmg.f8.profile.exception;

import javax.validation.ValidationException;

/**
 * @author tung.nguyenthanh
 */
public class QuestionAnswerValidateException extends ValidationException {
    private final String error;
    private final String detail;

    public QuestionAnswerValidateException(final String error, final String detail) {
        super();
        this.error = error;
        this.detail = detail;
    }

    public String getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }
}
