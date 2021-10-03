package asia.cmg.f8.gateway.security.utils;

/**
 * Created on 1/9/17.
 */
public class ErrorResponse {

    public static final ErrorResponse LINKED_ACCOUNT_NOT_FOUNT = new ErrorResponse(4002, "LINKED_ACCOUNT_NOT_FOUNT", "Not found any linked account");
    public static final ErrorResponse FAILED_TO_LOAD_USER_WITH_TOKEN = new ErrorResponse(4002, "FAILED_TO_LOAD_USER_WITH_TOKEN", "Failed to load user detail with access token");
    public static final ErrorResponse FAILED_TO_GRANT_ACCESS_TOKEN = new ErrorResponse(4002, "FAILED_TO_GRANT_ACCESS_TOKEN", "Failed to grant access token for linked user");
    public static final ErrorResponse USER_NOT_ACTIVATED = new ErrorResponse(4012, "USER_NOT_ACTIVATED", "Account is not activated");
    public static final ErrorResponse USER_NOT_EXIST = new ErrorResponse(4013, "USER_NOT_EXIST", "Account does not exist");
    public static final ErrorResponse EMAIL_EXISTED = new ErrorResponse(9002, "EMAIL_EXISTED", "Email existed");
    public static final ErrorResponse USER_NOT_CONFIRMED = new ErrorResponse(4014, "USER_NOT_CONFIRMED", "Account does not confirm email");
    public static final ErrorResponse AUTHENTICATION_FAILED = new ErrorResponse(4011, "AUTHENTICATION_FAILED", "Authentication is failed");
    public static final ErrorResponse REQUEST_DATA_IS_INVALID = new ErrorResponse(4001, "REQUEST_DATA_IS_INVALID", "User is not authenticated or Token is not supported");

    private int code;
    private String error;
    private String detail;

    public ErrorResponse(final int code, final String error, final String detail) {
        this.code = code;
        this.error = error;
        this.detail = detail;
    }

    public ErrorResponse() {
        // default
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }

    public ErrorResponse createNew(final String error, final String detail) {
        return new ErrorResponse(this.getCode(), error, detail);
    }

    public ErrorResponse createNew(final String detail) {
        return new ErrorResponse(this.getCode(), this.getError(), detail);
    }
}
