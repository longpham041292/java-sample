package asia.cmg.f8.profile;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.common.web.EnableWebSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 10/17/16.
 */
@Configuration
@SpringBootApplication
@EnableFeignClients
@EnableBinding
@EnableWebSupport
@EnableUserContext
@SuppressWarnings("PMD")
public class ProfileApp {

    public static void main(final String[] args) {
        new F8Application(ProfileApp.class.getSimpleName()).with(new SpringApplicationBuilder(ProfileApp.class).web(true)).run(args);
    }

}
