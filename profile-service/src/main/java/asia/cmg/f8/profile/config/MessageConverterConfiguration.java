package asia.cmg.f8.profile.config;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import asia.cmg.f8.common.message.EnableMessage;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

import javax.inject.Inject;
import java.io.IOException;

@Configuration
@EnableMessage
public class MessageConverterConfiguration {

    private static final MimeType AVRO_MINE_TYPE = MimeType.valueOf("avro/bytes");

    private final AvRoMessageConverterLoader avRoMessageConverterLoader;

    @Inject
    public MessageConverterConfiguration(final AvRoMessageConverterLoader avRoMessageConverterLoader) {
        this.avRoMessageConverterLoader = avRoMessageConverterLoader;
    }

    @Bean(name = "answerSubmittedEventConverter")
    public MessageConverter answerSubmittedEventConverter() throws IOException {
        final AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(AVRO_MINE_TYPE);
        converter.setSchemaLocation(new ClassPathResource("avro/AnswerSubmittedEvent.avsc"));
        return converter;
    }

    @Bean(name = "completeProfileEventConverter")
    public MessageConverter resumeRegisterEmailEventConverter() throws IOException {
        // TODO Move to common lib
        return avRoMessageConverterLoader.load("CompleteProfileEvent.avsc").get();
    }

    @Bean(name = "changeUserInfoEventConverter")
    public MessageConverter changeUserInfoEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("ChangeUserInfoEvent.avsc").get();
    }

    @Bean(name = "changeFollowingEventConverter")
    public MessageConverter changeFollowingEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("FollowingConnectionEvent.avsc").get();
    }

    @Bean(name = "orderCompletedEventConverter")
    public MessageConverter orderCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderCompletedEvent.avsc").get();
    }
    
    @Bean(name = "userUnFollowingEventConverter")
    public MessageConverter userUnFollowingEventConverter() {
        return avRoMessageConverterLoader.load("UserUnFollowingConnectionEvent.avsc").orElseThrow(() -> new IllegalStateException("Failed to load UserUnFollowingConnectionEvent.avsc"));
    }

}
