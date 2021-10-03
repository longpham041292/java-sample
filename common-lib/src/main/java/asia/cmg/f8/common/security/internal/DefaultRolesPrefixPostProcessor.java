package asia.cmg.f8.common.security.internal;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

/**
 * Created on 12/1/16.
 */
public class DefaultRolesPrefixPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof DefaultMethodSecurityExpressionHandler) {
            ((DefaultMethodSecurityExpressionHandler) bean).setDefaultRolePrefix("");
        }
        if (bean instanceof DefaultWebSecurityExpressionHandler) {
            ((DefaultWebSecurityExpressionHandler) bean).setDefaultRolePrefix("");
        }
        if (bean instanceof SecurityContextHolderAwareRequestFilter) {
            ((SecurityContextHolderAwareRequestFilter) bean).setRolePrefix("");
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
