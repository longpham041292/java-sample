package asia.cmg.f8.user.config;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import asia.cmg.f8.common.message.EnableMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import java.io.IOException;

/**
 * Created by on 11/3/16.
 */
@Configuration
@EnableMessage
public class MessageConverterConfiguration {

    private final AvRoMessageConverterLoader avRoMessageConverterLoader;

    public MessageConverterConfiguration(
            final AvRoMessageConverterLoader avRoMessageConverterLoader) {
        this.avRoMessageConverterLoader = avRoMessageConverterLoader;
    }

    @Bean(name = "submitDocumentEventConverter")
    public MessageConverter submitDocumentEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("SubmitDocumentAdminEvent.avsc").get();
    }

    @Bean(name = "changeUserInfoEventConverter")
    public MessageConverter changeUserInfoEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("ChangeUserInfoEvent.avsc").get();
    }

    @Bean(name = "signUpUserEventConverter")
    public MessageConverter signUpUserEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("SignUpUserEvent.avsc").get();
    }

    @Bean(name = "userActivatedEventConverter")
    public MessageConverter userActivatedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("UserActivationEvent.avsc").get();
    }

    @Bean(name = "approvedDocumentEventConverter")
    public MessageConverter approvedDocumentEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("AdminApprovedDocumentEvent.avsc").get();
    }
    
    @Bean(name = "unApprovedDocumentEventConverter")
    public MessageConverter unApprovedDocumentEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("UnapproveDocumentEvent.avsc").get();
    }
}
