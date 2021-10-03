package asia.cmg.f8.gateway.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created on 1/13/17.
 */
public class InValidUserNameOrPasswordException extends AuthenticationException {

    public InValidUserNameOrPasswordException(final String msg) {
        super(msg);
    }

    public InValidUserNameOrPasswordException(final String msg, final Throwable throwable) {
        super(msg, throwable);
    }
}
