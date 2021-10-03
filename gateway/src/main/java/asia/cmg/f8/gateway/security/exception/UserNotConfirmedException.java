package asia.cmg.f8.gateway.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created on 2/15/17.
 */
public class UserNotConfirmedException extends AuthenticationException {

    public UserNotConfirmedException(final String message) {
        super(message);
    }
}
