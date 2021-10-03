package asia.cmg.f8.notification.exception;

/**
 * Created on 10/20/16.
 */
public class ApiErrorResponse {
    public static final String REQUEST_INVALID = "Request data is invalid";
    public static final String INTERNAL_SERVICE_ERROR = "Internal service error";
    public static final String INTERNAL_ERROR = "Internal server error";

    private int code;
    private String error;
    private String detail;

    public ApiErrorResponse(final int code, final String error) {
        super();
        this.code = code;
        this.error = error;
    }

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