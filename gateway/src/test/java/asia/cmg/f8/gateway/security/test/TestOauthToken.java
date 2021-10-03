package asia.cmg.f8.gateway.security.test;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 11/1/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestWithSecurityContextFactory.class)
public @interface TestOauthToken {

    String uuid() default "uuid";

    String username() default "username";

    String userType() default "pt";

    int exp() default 3600;

    boolean activated() default true;

    String token() default "access_token";
}
