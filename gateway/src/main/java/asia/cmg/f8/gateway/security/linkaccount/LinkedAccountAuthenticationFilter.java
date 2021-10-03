package asia.cmg.f8.gateway.security.linkaccount;

import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.usergrid.UserGridProperties;
import asia.cmg.f8.gateway.security.usergrid.UserGridResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;

import static asia.cmg.f8.gateway.security.utils.ErrorResponse.FAILED_TO_GRANT_ACCESS_TOKEN;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.FAILED_TO_LOAD_USER_WITH_TOKEN;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.LINKED_ACCOUNT_NOT_FOUNT;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.REQUEST_DATA_IS_INVALID;
import static asia.cmg.f8.gateway.security.utils.ErrorResponse.USER_NOT_ACTIVATED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * Authenticate an account that's linked with current logged-in user.
 * <p>
 * Created on 1/9/17.
 */
@SuppressWarnings("PMD")
public class LinkedAccountAuthenticationFilter extends GenericFilterBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(LinkedAccountAuthenticationFilter.class);

    private final RequestMatcher pathMatcher;
    private final LinkedUserClient linkedUserClient;
    private final LinkedTokenClient linkedTokenClient;
    private final AccessTokenRepository accessTokenRepository;
    private final UserDetailService userDetailService;
    private final ObjectMapper objectMapper;
    private final AuthorityService authorityService;
    private final UserGridProperties userGridProperties;


    public LinkedAccountAuthenticationFilter(final LinkedUserClient linkedUserClient, final LinkedTokenClient linkedTokenClient, final AccessTokenRepository accessTokenRepository, final UserDetailService userDetailService, final ObjectMapper objectMapper, final AuthorityService authorityService, final UserGridProperties userGridProperties) {
        this.linkedUserClient = linkedUserClient;
        this.linkedTokenClient = linkedTokenClient;
        this.accessTokenRepository = accessTokenRepository;
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
        this.authorityService = authorityService;
        this.userGridProperties = userGridProperties;
        this.pathMatcher = new AntPathRequestMatcher("/switchUser");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        if (isEligible(req)) {

            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.isAuthenticated() || !(authentication instanceof TokenAuthentication)) {

                writeTo(res, REQUEST_DATA_IS_INVALID, BAD_REQUEST);
                return;
            }

            final TokenAuthentication token = (TokenAuthentication) authentication;
            final UserDetail currentUserDetail = token.getUserDetail();
            final String userUuid = currentUserDetail.getUuid();
            final String accessToken = token.getToken();

            /**
             * find linked user of current logged-in user.
             */
            final Optional<LinkedUserInfo> linkedUser = findLinkedUser(userUuid, accessToken);
            if (!linkedUser.isPresent()) {

                writeTo(res, LINKED_ACCOUNT_NOT_FOUNT, BAD_REQUEST);
                return;
            }

            final LinkedUserInfo linkedUserInfo = linkedUser.get();
            final Long ttl = userGridProperties.getAccessTokenTtl();
            final Optional<AccessTokenResponse> linkedTokenInfo = switchUser(linkedUserInfo.getLinkedUserUuid(), accessToken, ttl);
            if (!linkedTokenInfo.isPresent()) {
                writeTo(res, FAILED_TO_GRANT_ACCESS_TOKEN, BAD_REQUEST);
                return;
            }


            final AccessTokenResponse tokenInfo = linkedTokenInfo.get();

            LOGGER.info("Re-generate token for user {}, ttl = {}", tokenInfo.getUser().get("username"), ttl);

            final Optional<UserDetail> userDetail = userDetailService.findByAccessToken(tokenInfo.getAccessToken());
            if (!userDetail.isPresent()) {

                writeTo(res, FAILED_TO_LOAD_USER_WITH_TOKEN, BAD_REQUEST);
                return;
            }

            final UserDetail detail = userDetail.get();
            if (!detail.isActivated()) {

                writeTo(res, USER_NOT_ACTIVATED, BAD_REQUEST);
                return;
            }

            // Build TokenAuthentication including load user's role
            final TokenAuthentication tokenAuthentication = authorityService.loadAuthorities(new TokenAuthentication(tokenInfo.getAccessToken(), tokenInfo.getExpiresIn(), detail));
            SecurityContextHolder.getContext().setAuthentication(tokenAuthentication);

            // Persist to use later.
            accessTokenRepository.save(tokenAuthentication.getToken(), tokenAuthentication);

            LOGGER.info("Switched account from {} to user {}", currentUserDetail.getUserName(), detail.getUserName());

            // Write response to end user.
            writeTo(res, tokenAuthentication, OK);
            return;
        }
        chain.doFilter(request, response);
    }

    private void writeTo(final HttpServletResponse response, final Object object, final HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (final Writer writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(object));
        }
    }

    private Optional<AccessTokenResponse> switchUser(final String linkedUserUuid, final String currentAccessToken, final long ttl) {
        return Optional.ofNullable(linkedTokenClient.switchUser(linkedUserUuid, currentAccessToken, ttl));
    }

    private Optional<LinkedUserInfo> findLinkedUser(final String userUuid, final String accessToken) {

        final UserGridResponse<LinkedUserInfo> response = linkedUserClient.findLinkedAccounts("linking_user=" + userUuid, accessToken);
        if (response == null || response.getEntities() == null) {
            return Optional.empty();
        }

        final List<LinkedUserInfo> entities = response.getEntities();

        if (entities.size() == 1) { // we expect only one account is allowed to linked to the current logged-in account
            return Optional.of(entities.iterator().next());
        }

        LOGGER.warn("Found {} accounts are linked to {}", entities.size(), userUuid);

        return Optional.empty();
    }


    /**
     * Verify if current http request is eligible for this filter.
     *
     * @param request the current http request
     * @return true if current request is eligible.
     */
    private boolean isEligible(final HttpServletRequest request) {
        return pathMatcher.matches(request);
    }
}
