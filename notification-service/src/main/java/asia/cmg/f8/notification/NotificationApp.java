package asia.cmg.f8.notification;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.message.EnableMessage;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.common.web.EnableWebSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * @author tung.nguyenthanh
 */
@Configuration
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
@EnableAsync
@EnableMessage
@EnableWebSupport
@EnableUserContext
@SuppressWarnings("PMD")
public class NotificationApp {

    public static void main(final String[] args) {
        new F8Application(NotificationApp.class.getSimpleName()).with(new SpringApplicationBuilder(NotificationApp.class).web(true)).run(args);
    }
}
