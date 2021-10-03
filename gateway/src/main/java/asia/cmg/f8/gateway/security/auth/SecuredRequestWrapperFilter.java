package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.token.JwtTokenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created on 11/1/16.
 */
public class SecuredRequestWrapperFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredRequestWrapperFilter.class);

    private final JwtTokenFactory jwtTokenFactory;

    public SecuredRequestWrapperFilter(final JwtTokenFactory jwtTokenFactory) {
        this.jwtTokenFactory = jwtTokenFactory;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !authentication.isAuthenticated();
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest wrappedRequest = request;

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof TokenAuthentication) {
            final TokenAuthentication authentication = (TokenAuthentication) auth;
            try {
                final String jwtToken = jwtTokenFactory.encode(authentication);
                wrappedRequest = new SecuredRequestWrapper(request, jwtToken);
            } catch (final IOException e) {
                LOGGER.warn("Error on creating jwt token.", e);
            }
        }

        filterChain.doFilter(wrappedRequest, response);
    }
}
