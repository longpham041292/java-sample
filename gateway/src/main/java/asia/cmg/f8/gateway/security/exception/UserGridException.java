package asia.cmg.f8.gateway.security.exception;

import asia.cmg.f8.gateway.security.utils.ErrorResponse;
import org.springframework.security.core.AuthenticationException;

import static asia.cmg.f8.gateway.security.utils.ErrorResponse.AUTHENTICATION_FAILED;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.REQUEST_DATA_IS_INVALID;

/**
 * Created on 1/13/17.
 */
public class UserGridException extends AuthenticationException {

    public static final String INVALID_GRANT_ERROR = "invalid_grant";
    public static final String AUTH_INVALID_ERROR = "auth_invalid";
    public static final String AUTH_BAD_ACCESS_TOKEN_ERROR = "auth_bad_access_token";

    private final UserGridErrorResponse content;

    public UserGridException(final String message, final UserGridErrorResponse response) {
        super(message);
        this.content = response;
    }

    public String getError() {
        return content == null ? null : content.getError();
    }

    public final ErrorResponse toErrorResponse() {

        ErrorResponse errorResponse = REQUEST_DATA_IS_INVALID;

        final String error = getError();
        if (INVALID_GRANT_ERROR.equals(error)) {
            errorResponse = AUTHENTICATION_FAILED.createNew("INVALID_USERNAME_OR_PASSWORD", "invalid username or password");
        } else if (AUTH_BAD_ACCESS_TOKEN_ERROR.equals(error)) {
            errorResponse = REQUEST_DATA_IS_INVALID.createNew("BAD_TOKEN", "Access token is invalid");
        } else if (AUTH_INVALID_ERROR.equals(error)) {
            errorResponse = AUTHENTICATION_FAILED;
        }
        return errorResponse;
    }
}
