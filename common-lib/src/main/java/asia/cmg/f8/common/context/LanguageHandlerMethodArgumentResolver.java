package asia.cmg.f8.common.context;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.SecurityUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@SuppressWarnings("PMD")
public class LanguageHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private static final String LANGUAGE = "lang";
    private static final String DEFAULT_LANGUAGE = "en";

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(LanguageContext.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {
        String lang = webRequest.getParameter(LANGUAGE);
        if (isNotSet(lang)) {
            final Account account = (Account) SecurityUtils.getAccount();
            if (account != null && !isNotSet(account.language())) {
                lang = account.language();
            } else {
                lang = DEFAULT_LANGUAGE;
            }
        }
        return new DefaultLanguageContext(lang);
    }

    private boolean isNotSet(final String value) {
        return StringUtils.isEmpty(value);
    }
}
