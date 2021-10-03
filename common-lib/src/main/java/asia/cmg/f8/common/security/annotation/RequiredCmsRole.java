package asia.cmg.f8.common.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;
import asia.cmg.f8.common.security.util.SecurityConstants;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(value = SecurityConstants.CMS_ADMIN_ROLE_PRE_AUTHORIZE_EXPRESSION)
public @interface RequiredCmsRole {
}
