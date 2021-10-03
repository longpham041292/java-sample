package asia.cmg.f8.gateway.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created on 2/14/17.
 */
public class EmailExistedException extends AuthenticationException {

    public EmailExistedException(final String msg) {
        super(msg);
    }
}
