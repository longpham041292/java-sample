package asia.cmg.f8.gateway.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Authenticate user with UserGrid.
 * <p>
 * Created on 10/19/16.
 */
public class DefaultAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public DefaultAuthenticationProcessingFilter(final ObjectMapper objectMapper) {
        super("/token");
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        final Map userInfo = objectMapper.readValue(request.getInputStream(), Map.class);
        return getAuthenticationManager().authenticate(new ClientAuthentication(userInfo));
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        if (authResult instanceof TokenAuthentication) {
            final TokenAuthentication auth = (TokenAuthentication) authResult;
            SecurityContextHolder.getContext().setAuthentication(auth);
            getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
        } else {
            super.successfulAuthentication(request, response, chain, authResult);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        setContinueChainBeforeSuccessfulAuthentication(false);
    }
}
