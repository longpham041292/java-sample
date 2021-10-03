package asia.cmg.f8.gateway.security.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import asia.cmg.f8.gateway.config.GatewayProperties;
import asia.cmg.f8.gateway.security.exception.InValidAuthenticationException;

/**
 * Filter chain for Actuator
 * Created on 14/9/18.
 */
public class ActuatorFilter extends GenericFilterBean {

	@Autowired
	private GatewayProperties gatewayProperties;

	private final RequestMatcher matcher;

	public ActuatorFilter(final String pattern) {
		this.matcher = new AntPathRequestMatcher(pattern);
	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		if (matcher.matches(request) && !hasAccessRole()) {
			throw new InValidAuthenticationException("Access Denied");
		}

		chain.doFilter(request, response);
	}

	private boolean hasAccessRole() {
		boolean hasRole = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				hasRole = authority.getAuthority().equals(gatewayProperties.getAccessActuatorRole());
				if (hasRole) {
					break;
				}
			}
		}

		return hasRole;
	}
}
