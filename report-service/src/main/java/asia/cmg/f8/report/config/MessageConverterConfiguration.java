package asia.cmg.f8.report.config;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import asia.cmg.f8.common.message.EnableMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import javax.inject.Inject;
import java.io.IOException;

@Configuration
@EnableMessage
public class MessageConverterConfiguration {

    private final AvRoMessageConverterLoader avroMessageConverterLoader;

    @Inject
    public MessageConverterConfiguration(
            final AvRoMessageConverterLoader avroMessageConverterLoader) {
        this.avroMessageConverterLoader = avroMessageConverterLoader;
    }

    @Bean(name = "changeSessionStatusEventConverter")
    public MessageConverter changeSessionStatusEventConverter() throws IOException {
        return avroMessageConverterLoader.load("ChangeSessionStatusEvent.avsc").get();
    }

    @Bean(name = "orderCompletedEventConverter")
    public MessageConverter orderCompletedEventConverter() throws IOException {
        return avroMessageConverterLoader.load("OrderCompletedEvent.avsc").get();
    }
}
