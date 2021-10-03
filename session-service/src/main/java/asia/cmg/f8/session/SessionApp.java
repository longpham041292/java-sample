package asia.cmg.f8.session;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.common.web.EnableWebSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created on 11/17/16.
 */
@Configuration
@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableUserContext
@EnableWebSupport
@SuppressWarnings("PMD")
public class SessionApp {

    public static void main(final String[] args) {

        final SpringApplicationBuilder builder = new SpringApplicationBuilder(SessionApp.class).web(true);

        new F8Application(SessionApp.class.getSimpleName()).with(builder).run(args);
    }

}
