package asia.cmg.f8.commerce;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.common.web.EnableWebSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Created on 10/17/16.
 */
@Configuration
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "commerceAuditorAware")
@EnableFeignClients
@EnableUserContext
@EnableWebSupport
@SuppressWarnings("PMD")
public class CommerceApp {

    public static void main(final String[] args) {
        new F8Application(CommerceApp.class.getSimpleName()).with(new SpringApplicationBuilder(CommerceApp.class).web(true)).run(args);
    }

 }
