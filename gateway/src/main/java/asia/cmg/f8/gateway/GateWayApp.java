package asia.cmg.f8.gateway;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 9/28/16.
 */
@Configuration
@SpringBootApplication
@EnableZuulProxy
@EnableAutoConfiguration
@SuppressWarnings("PMD")
@EnableFeignClients
public class GateWayApp {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(GateWayApp.class).web(true).run(args);
    }
}

