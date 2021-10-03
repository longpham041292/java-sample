package asia.cmg.f8.schedule;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 10/17/16.
 */
@Configuration
@SpringBootApplication
@EnableFeignClients
@SuppressWarnings("PMD")
public class ScheduleApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(ScheduleApplication.class).web(true).run(args);
    }
}
