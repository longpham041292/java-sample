package asia.cmg.f8.gateway.security.test;

import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Created on 11/1/16.
 */
public class TestWithSecurityContextFactory implements WithSecurityContextFactory<TestOauthToken> {

    @Override
    public SecurityContext createSecurityContext(final TestOauthToken annotation) {
        final TestUserDetail userDetail = new TestUserDetail(annotation);
        final TokenAuthentication authentication = new TokenAuthentication(annotation.uuid(), annotation.exp(), userDetail);
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
