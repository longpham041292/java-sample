package asia.cmg.f8.gateway.security.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

/**
 * Logout user when they reset their password.
 * <p>
 * Created on 11/8/16.
 */
public class ResetPwdLogoutFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(ResetPwdLogoutFilter.class);

    private final RequestMatcher matcher;

    public ResetPwdLogoutFilter(final String pattern) {
        this.matcher = new AntPathRequestMatcher(pattern);
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        if (matcher.matches(request)) {

            final DelegateHttpResponseWrapper wrapper = new DelegateHttpResponseWrapper(response);

            chain.doFilter(request, wrapper);

            final int status = wrapper.getStatus();
            if (OK.value() != status) {
                LOG.warn("Reset pwd is failed... ");
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    static final class DelegateHttpResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public DelegateHttpResponseWrapper(final HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            final ServletOutputStream out = super.getOutputStream();
            return new ByteArrayServletOutputStream(out, outputStream);
        }
    }

    static final class ByteArrayServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream out;
        private final ServletOutputStream delegate;

        ByteArrayServletOutputStream(final ServletOutputStream delegate, final ByteArrayOutputStream out) {
            this.delegate = delegate;
            this.out = out;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(final WriteListener listener) {
            // don't support
        }

        @Override
        public void write(final int content) throws IOException {
            out.write(content);
            delegate.write(content);
        }
    }
}
