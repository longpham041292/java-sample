package asia.cmg.f8.session.exception;

/**
 * Created on 10/20/16.
 *
 * @deprecated will be moved into common later.
 */
public class ApiErrorResponse {
    public static final String REQUEST_INVALID = "REQUEST_DATA_IS_INVALID";

    private int code;
    private String error;
    private String detail;

    public ApiErrorResponse(final int code, final String error, final String detail) {
        super();
        this.code = code;
        this.error = error;
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }
}