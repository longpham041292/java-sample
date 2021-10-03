package asia.cmg.f8.notification.push;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import java.io.IOException;

/**
 * Created on 1/7/17.
 */
@Configuration
@EnableBinding(NotificationEventStream.class)
public class MessageConverterConfiguration {

    @Autowired
    private AvRoMessageConverterLoader avroMessageConverterLoader;

    @Bean(name = "deviceRegisteredEventConverter")
    public MessageConverter deviceRegisterEventConverter() throws IOException {
        return avroMessageConverterLoader.load("DeviceRegisteredEvent.avsc").get();
    }

    @Bean(name = "changeSessionStatusEventConverter")
    public MessageConverter sessionStatusChangeEventConverter() throws IOException {
        return avroMessageConverterLoader.load("ChangeSessionStatusEvent.avsc").get();
    }

    @Bean(name = "userPostStatusEventConverter")
    public MessageConverter userPostStatusConverter() throws IOException {
        return avroMessageConverterLoader.load("SocialPostCreatedEvent.avsc").get();
    }

    @Bean(name = "likePostEventConverter")
    public MessageConverter likePostConverter() throws IOException {
        return avroMessageConverterLoader.load("LikePostEvent.avsc").get();
    }

    @Bean(name = "commentPostEventConverter")
    public MessageConverter commentPostConverter() throws IOException {
        return avroMessageConverterLoader.load("CommentPostEvent.avsc").get();
    }

    @Bean(name = "likeCommentEventConverter")
    public MessageConverter likeCommentConverter() throws IOException {
        return avroMessageConverterLoader.load("LikeCommentEvent.avsc").get();
    }

    @Bean(name = "approvedDocumentEventConverter")
    public MessageConverter approvedDocumentEventConverter() throws IOException {
        return avroMessageConverterLoader.load("AdminApprovedDocumentEvent.avsc").get();
    }

    @Bean(name = "userFollowing")
    public MessageConverter userFollowingEventConverter() throws IOException {
        return avroMessageConverterLoader.load("FollowingConnectionEvent.avsc").get();
    }

    @Bean(name = "sessionBookCompleteEventConverter")
    public MessageConverter sessionBookCompleteEventConverter() throws IOException {
        return avroMessageConverterLoader.load("SessionBookCompleteEvent.avsc").get();
    }

    @Bean(name = "notifyChangeSessionStatusConverter")
    public MessageConverter notifyChangeSessionStatusEventConverter() throws IOException {
        return avroMessageConverterLoader.load("NotifyChangeSessionStatusEvent.avsc").get();
    }
    
    @Bean("userCreatedPostEventConverter")
    public MessageConverter userCreatedPostEventConverter() throws IOException {
        return avroMessageConverterLoader.load("SocialPostCreatedEvent.avsc").get();
    }
}
