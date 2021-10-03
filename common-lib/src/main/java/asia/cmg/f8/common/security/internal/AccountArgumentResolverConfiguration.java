package asia.cmg.f8.common.security.internal;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created on 11/2/16.
 */
@Configuration
@EnableWebMvc
public class AccountArgumentResolverConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AccountArgumentResolver());
    }
}
