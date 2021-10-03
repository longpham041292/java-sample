package asia.cmg.f8.common.security.annotation;

import asia.cmg.f8.common.security.config.RoleConfiguration;
import asia.cmg.f8.common.security.config.SecurityConfiguration;
import asia.cmg.f8.common.security.internal.AccountArgumentResolverConfiguration;
import asia.cmg.f8.common.security.token.TokenConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 11/1/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {TokenConfiguration.class, SecurityConfiguration.class, AccountArgumentResolverConfiguration.class, RoleConfiguration.class})
public @interface EnableClientSecurity {
}
