package asia.cmg.f8.common.context;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author tung.nguyenthanh
 */
@Configuration
@EnableWebMvc
public class ContextArgumentResolverConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LanguageHandlerMethodArgumentResolver());
    }
}
