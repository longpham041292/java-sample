package asia.cmg.f8.session.config;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import asia.cmg.f8.common.message.EnableMessage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import java.io.IOException;

import javax.inject.Inject;

@Configuration
@EnableMessage
public class MessageConverterConfiguration {

    private final AvRoMessageConverterLoader avRoMessageConverterLoader;

    @Inject
    public MessageConverterConfiguration(final AvRoMessageConverterLoader avRoMessageConverterLoader) {
        this.avRoMessageConverterLoader = avRoMessageConverterLoader;
    }

    @Bean(name = "orderCompletedEventConverter")
    public MessageConverter orderCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderCompletedEvent.avsc").get();
    }

    @Bean(name = "sessionStatusEventConverter")
    public MessageConverter sessionStatusEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("ChangeSessionStatusEvent.avsc").get();
    }

    @Bean(name = "newOrderEventConverter")
    public MessageConverter newOrderEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("NewOrderCommerceEvent.avsc").get();
    }

    @Bean(name = "userActivatedEventConverter")
    public MessageConverter userActivatedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("UserActivationEvent.avsc").get();
    }

    @Bean(name = "transferSessionsEventConverter")
    public MessageConverter transferSessionsEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("TransferSessionPackageEvent.avsc").get();
    }

    @Bean(name = "createdPackageEventConverter")
    public MessageConverter createdPackageEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("CreatedSessionPackageEvent.avsc").get();
    }
    
    @Bean(name = "sessionBookingEventConverter")
    public MessageConverter sessionBookingCompletedConverter() throws IOException {
        return avRoMessageConverterLoader.load("SessionBookCompleteEvent.avsc").get();
    }

    @Bean(name = "changeUserInfoEventConverter")
    public MessageConverter changeUserInfoEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("ChangeUserInfoEvent.avsc").get();
    }
    
    @Bean(name = "orderSubscriptionCompletedEventConverter")
    public MessageConverter orderSubscriptionCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderSubscriptionCompletedEvent.avsc").get();
    }
    
    @Bean(name = "scheduleEventOutputConverter")
    public MessageConverter scheduleEventOutputConverter() throws IOException {
        return avRoMessageConverterLoader.load("ScheduleEvent.avsc").get();
    }
    
    @Bean(name = "pushingNotificationEventOutputConverter")
    public MessageConverter pushingNotificationEventOutputConverter() throws IOException {
        return avRoMessageConverterLoader.load("PushingNotificationEvent.avsc").get();
    }
}
