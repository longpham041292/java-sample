package asia.cmg.f8.gateway.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created on 10/21/16.
 */
public class UserNotActivateException extends AuthenticationException {

    public UserNotActivateException(final String msg) {
        super(msg);
    }
}

