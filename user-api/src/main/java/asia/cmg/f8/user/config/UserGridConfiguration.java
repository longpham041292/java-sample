package asia.cmg.f8.user.config;

import asia.cmg.f8.user.exception.UserApiDecoder;
import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 10/11/16.
 */
@Configuration
public class UserGridConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }

    @Bean
    public UserApiDecoder myErrorDecoder() {
        return new UserApiDecoder();
    }

    //Remove retry when connection timeout because it will make duplicate record in database
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}
