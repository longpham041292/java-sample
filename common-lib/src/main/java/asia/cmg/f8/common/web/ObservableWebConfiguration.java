package asia.cmg.f8.common.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created on 12/26/16.
 */
@EnableWebMvc
@Configuration
public class ObservableWebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addReturnValueHandlers(final List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new ObservableReturnValueHandler());
    }
}
