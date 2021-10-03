package asia.cmg.f8.gateway.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 12/1/16.
 */
class CustomHttpServletRequest extends HttpServletRequestWrapper {
    private final Map<String, String> customHeaders;

    public CustomHttpServletRequest(final HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    public void putHeader(final String name, final String value) {
        this.customHeaders.put(name, value);
    }

    public String getHeader(final String name) {
        final String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public Enumeration<String> getHeaderNames() {
        final Set<String> set = new HashSet<>(customHeaders.keySet());

        final Enumeration<String> headerNames = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String nextElement = headerNames.nextElement();
            set.add(nextElement);
        }

        return Collections.enumeration(set);
    }
}