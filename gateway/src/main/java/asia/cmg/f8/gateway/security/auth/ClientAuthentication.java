package asia.cmg.f8.gateway.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;
import java.util.Map;

/**
 * Created on 11/3/16.
 */
public class ClientAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 4278918740075713110L;

    private final transient Map<String, Object> clientInfo;

    public ClientAuthentication(final Map<String, Object> clientInfo) {
        super(Collections.emptyList());
        this.clientInfo = clientInfo;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public String getGrantType() {
        return (String) clientInfo.get("grant_type");
    }

    public String get(final String prop) {
        return (String) clientInfo.get(prop);
    }
}
