package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import java.io.IOException;

/**
 * Created on 1/7/17.
 */
@Configuration
public class ChangeUserInfoConfiguration {

    @Autowired
    private AvRoMessageConverterLoader avroMessageConverterLoader;

    @Bean(name = "changeUserInfoEventConverter")
    public MessageConverter changeUserInfoEventConverter() throws IOException {
        return avroMessageConverterLoader.load("ChangeUserInfoEvent.avsc").get();
    }
}
