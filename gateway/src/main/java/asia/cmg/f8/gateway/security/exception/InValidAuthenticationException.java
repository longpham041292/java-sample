package asia.cmg.f8.gateway.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created on 10/21/16.
 */
public class InValidAuthenticationException extends AuthenticationException {

    public InValidAuthenticationException(final String msg) {
        super(msg);
    }
}

