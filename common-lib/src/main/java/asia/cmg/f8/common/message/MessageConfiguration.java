package asia.cmg.f8.common.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 11/17/16.
 */
@Configuration
public class MessageConfiguration {

    @Bean
    public AvRoMessageConverterLoader avRoMessageConverterLoader() {
        return new AvRoMessageConverterLoader();
    }
}
