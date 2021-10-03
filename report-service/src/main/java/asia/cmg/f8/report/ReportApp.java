package asia.cmg.f8.report;

import asia.cmg.f8.common.context.EnableUserContext;
import asia.cmg.f8.common.web.EnableWebSupport;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 9/28/16.
 */
@Configuration
@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients
@EnableUserContext
@EnableWebSupport
@SuppressWarnings("PMD")
public class ReportApp {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(ReportApp.class).web(true).run(args);
    }
}
