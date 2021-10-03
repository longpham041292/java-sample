package asia.cmg.f8.usergridconfig;

import asia.cmg.f8.gateway.security.exception.UserGridErrorResponse;
import asia.cmg.f8.gateway.security.exception.UserGridException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static java.lang.String.format;

/**
 * Specified configuration that is used for User-Grid authentication.
 * <p>
 * Created on 1/13/17.
 */
@Configuration
public class UserGridAuthConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGridAuthConfig.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String message = format("status %s reading %s", response.status(), methodKey);
            UserGridErrorResponse errorResponse = null;

            try {
                final Response.Body body = response.body();
                if (body != null) {
                    errorResponse = mapper.readValue(Util.toString(body.asReader()), UserGridErrorResponse.class);
                    message = errorResponse.getDescription();
                }
            } catch (final IOException e) {
                LOGGER.warn("Error on convert ErrorDecoder", e);
            }
            return new UserGridException(message, errorResponse);
        };
    }

    @Bean
    public Feign.Builder builder() {
        return Feign.builder().errorDecoder(errorDecoder());
    }
}
