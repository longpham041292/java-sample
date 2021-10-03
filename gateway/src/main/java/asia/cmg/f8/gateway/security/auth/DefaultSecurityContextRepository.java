package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.SecurityUtil;
import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static asia.cmg.f8.gateway.SecurityUtil.AUTHORIZATION_HEADER;
import static asia.cmg.f8.gateway.SecurityUtil.fromBearer;
import static java.util.Optional.ofNullable;

/**
 * Hazelcast as in-memory storage solution for security context. It's used by {@link SecurityContextPersistenceFilter}.
 * <p>
 * Always verify token by querying user-grid for current access token.
 * <p>
 * Created on 12/26/16.
 */
public class DefaultSecurityContextRepository implements SecurityContextRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultSecurityContextRepository.class);

    private final AccessTokenRepository accessTokenRepository;
    private final UserDetailService userDetailService;
    private final AuthorityService authorityService;

    public DefaultSecurityContextRepository(final AccessTokenRepository accessTokenRepository, final UserDetailService userDetailService, final AuthorityService authorityService) {
        this.accessTokenRepository = accessTokenRepository;
        this.userDetailService = userDetailService;
        this.authorityService = authorityService;
    }

    @Override
    public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {

        final HttpServletRequest request = requestResponseHolder.getRequest();
        
        final String token = getAuthorization(request).map(SecurityUtil::fromBearer).orElse(null);
        if (token == null) {
            return SecurityContextHolder.createEmptyContext();
        }

        /**
         * Always validate the token with token server.
         */
        final TokenAuthentication authentication = loadToken(token);
        if (authentication == null) {

            // this token is invalid or expired.
            accessTokenRepository.remove(token);

            // return empty context.
            return SecurityContextHolder.createEmptyContext();
        }

        Authentication cachedAuthentication = accessTokenRepository.load(token);
        if (cachedAuthentication == null) {
            // it does not exist in cache yet
            cachedAuthentication = authorityService.loadAuthorities(authentication);
            accessTokenRepository.save(token, cachedAuthentication);
        }

        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(cachedAuthentication);
        return context;
    }

    @Override
    public void saveContext(final SecurityContext context, final HttpServletRequest request, final HttpServletResponse response) {
        getAuthorization(request).ifPresent(token -> {
            final Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                accessTokenRepository.save(fromBearer(token), authentication);
            }
        });
    }

    @Override
    public boolean containsContext(final HttpServletRequest request) {
        return getAuthorization(request).filter(accessTokenRepository::hasAuthentication).isPresent();
    }

    private Optional<String> getAuthorization(final HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION_HEADER));
    }

    private TokenAuthentication loadToken(final String token) {
        return userDetailService.findByAccessToken(token).map(user -> new TokenAuthentication(token, -1, user)).orElse(null);
    }
}
