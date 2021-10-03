package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.exception.EmailExistedException;
import asia.cmg.f8.gateway.security.exception.InValidUserNameOrPasswordException;
import asia.cmg.f8.gateway.security.exception.UserGridException;
import asia.cmg.f8.gateway.security.exception.UserNotActivateException;
import asia.cmg.f8.gateway.security.exception.UserNotConfirmedException;
import asia.cmg.f8.gateway.security.exception.UserNotExistException;
import asia.cmg.f8.gateway.security.utils.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static asia.cmg.f8.gateway.security.utils.ErrorResponse.AUTHENTICATION_FAILED;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.USER_NOT_CONFIRMED;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.USER_NOT_EXIST;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.EMAIL_EXISTED;

/**
 * Created on 10/21/16.
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    public DefaultAuthenticationFailureHandler(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = AUTHENTICATION_FAILED;

        if (exception instanceof UserNotActivateException) {
            error = ErrorResponse.USER_NOT_ACTIVATED;
        } else if (exception instanceof UserGridException) {
            error = ((UserGridException) exception).toErrorResponse();
        } else if (exception instanceof InValidUserNameOrPasswordException) {
            error = AUTHENTICATION_FAILED.createNew("INVALID_USERNAME_OR_PASSWORD", "invalid username or password");
        } else if (exception instanceof UserNotExistException) {
            error = USER_NOT_EXIST.createNew(exception.getMessage());
        } else if (exception instanceof UserNotConfirmedException) {
            error = USER_NOT_CONFIRMED.createNew(exception.getMessage());
        } else if (exception instanceof EmailExistedException) {
        	error = EMAIL_EXISTED.createNew(exception.getMessage());
        }

        try (final Writer writer = response.getWriter()) {
            writer.write(mapper.writeValueAsString(error.createNew(exception.getMessage())));
        }
    }
}
