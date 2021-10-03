package asia.cmg.f8.common.security.internal;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.SecurityUtils;

/**
 * Created on 11/1/16.
 */
@SuppressWarnings("PMD")
public class AccountArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return Account.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        return SecurityUtils.getAccount();
    }
}
