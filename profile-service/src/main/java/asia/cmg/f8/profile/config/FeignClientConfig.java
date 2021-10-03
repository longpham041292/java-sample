package asia.cmg.f8.profile.config;

import feign.Feign;
import feign.Request;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    private final FeignProperties feignProperties;

    public FeignClientConfig(final FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Bean
    public Feign.Builder feignBuilder() {

        final Integer timeoutMillis = feignProperties.getConnectTimeoutMillis();
        final Integer readTimeoutMillis = feignProperties.getReadTimeoutMillis();
        final Request.Options options = new Request.Options(timeoutMillis == null ? 10 * 1000 : timeoutMillis, readTimeoutMillis == null ? 60 * 1000 : readTimeoutMillis);
        return HystrixFeign.builder().contract(new SpringMvcContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).options(options);
    }
}
