package asia.cmg.f8.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;


@Configuration
public class FeignClientConfig {

    private final FeignProperties feignProperties;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 60 * 1000;

    public FeignClientConfig(final FeignProperties feignProperties) {
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
