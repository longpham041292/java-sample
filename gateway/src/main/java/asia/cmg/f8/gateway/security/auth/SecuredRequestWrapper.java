package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import static asia.cmg.f8.gateway.SecurityUtil.AUTHORIZATION_HEADER;

/**
 * Created on 10/20/16.
 */
public final class SecuredRequestWrapper extends HttpServletRequestWrapper {

    private final String jwtToken;

    public SecuredRequestWrapper(final HttpServletRequest request, final String jwtToken) {
        super(request);
        this.jwtToken = SecurityUtil.toBearer(jwtToken);
    }

    /**
     * Return JWT token.
     *
     * @param name the header name
     * @return header value.
     */
    @Override
    public String getHeader(final String name) {
        if (AUTHORIZATION_HEADER.equals(name)) {
            return jwtToken;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        if (AUTHORIZATION_HEADER.equals(name)) {
            return Collections.enumeration(Collections.singleton(jwtToken));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        final Set<String> headers = new HashSet<>(Collections.list(super.getHeaderNames()));
        headers.add(AUTHORIZATION_HEADER);
        return Collections.enumeration(headers);
    }
}
