package asia.cmg.f8.notification.client;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 1/7/17.
 */
@Configuration
public class UserGridClientConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserGridClientConfiguration.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            LOGGER.error("Method := {}, response status := {}", methodKey, response.status());
            final Response.Body body = response.body();
            if (body != null) {
                try {
                    final Map bodyAsMap = mapper.readValue(body.asInputStream(), Map.class);
                    LOGGER.error("Detailed error := {}", bodyAsMap);
                } catch (final IOException e) {
                    LOGGER.error("Error on convert response body to json", e);
                }
            }
            return FeignException.errorStatus(methodKey, response);
        };
    }
}
