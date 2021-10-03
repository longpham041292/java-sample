package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import asia.cmg.f8.gateway.security.usergrid.UserGridLogoutApi;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static asia.cmg.f8.gateway.security.utils.StringUtil.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 11/3/16.
 */
public class AccessTokenLogoutFilter extends LogoutFilter {

    public static final String TOKEN_PROP = "token";

    private final UserDetailService userDetailService;
    private final UserGridLogoutApi userGridLogoutApi;

    public AccessTokenLogoutFilter(final LogoutSuccessHandler logoutSuccessHandler, final UserDetailService userDetailService, final UserGridLogoutApi userGridLogoutApi, final LogoutHandler... handlers) {
        super(logoutSuccessHandler, handlers);
        this.userDetailService = userDetailService;
        this.userGridLogoutApi = userGridLogoutApi;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        if (requiresLogout(request, response)) {

            final String token = req.getParameter(TOKEN_PROP);
            final String uuid = req.getParameter("uuid");

            if (isEmpty(token) || isEmpty(uuid)) {
                writeError(response);
                return;
            }

            final String userUuid = userDetailService.findByAccessToken(token).map(UserDetail::getUuid).orElse(null);
            if (uuid.equals(userUuid) && logoutUserGrid(uuid)) {
                super.doFilter(req, res, chain);
            } else {
                writeError(response);
            }
        }
        chain.doFilter(req, res);
    }

    private void writeError(final HttpServletResponse response) throws IOException {
        // logout is not allowed.
        response.setCharacterEncoding("UTF-8");
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(400);
        try (final Writer writer = response.getWriter()) {
            writer.write("{\"error\":\"invalid\", \"code\":0, \"detail\":\"token or uuid is invalid or not matched\"}");
        }
    }

    private boolean logoutUserGrid(final String uuid) {
        final Map<String, Object> result = userGridLogoutApi.logout(uuid);
        return result.get("error") == null;
    }
}
