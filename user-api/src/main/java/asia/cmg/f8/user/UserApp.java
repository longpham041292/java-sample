package asia.cmg.f8.user;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.common.web.EnableWebSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;


/**
 * Created on 9/28/16.
 */
@Configuration
@SpringBootApplication
@EnableFeignClients
@EnableBinding
@EnableWebSupport
@EnableUserContext
@SuppressWarnings("PMD")
public class UserApp {

    public static void main(final String[] args) {
        new F8Application(UserApp.class.getSimpleName()).with(new SpringApplicationBuilder(UserApp.class).web(true)).run(args);
    }

}

