package asia.cmg.f8.commerce.config;

import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common configuration which is used for all of feign clients.
 * Created on 4/20/17.
 */
@Configuration
public class FeignConfig {

    private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 60 * 1000;

    private final FeignProperties feignProperties;

    public FeignConfig(final FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder();
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(getConnectTimeout(feignProperties), getReadTimeout(feignProperties));
    }

    private int getConnectTimeout(final FeignProperties feignProperties) {
        final Integer timeoutMillis = feignProperties.getConnectTimeoutMillis();
        if (timeoutMillis == null) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
        return timeoutMillis;
    }

    private int getReadTimeout(final FeignProperties feignProperties) {
        final Integer timeoutMillis = feignProperties.getReadTimeoutMillis();
        if (timeoutMillis == null) {
            return DEFAULT_READ_TIMEOUT;
        }
        return timeoutMillis;
    }

    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}