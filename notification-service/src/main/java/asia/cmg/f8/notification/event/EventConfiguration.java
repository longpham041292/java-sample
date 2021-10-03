package asia.cmg.f8.notification.event;

import asia.cmg.f8.common.message.AvRoMessageConverterLoader;
import asia.cmg.f8.common.message.EnableMessage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import javax.inject.Inject;
import java.io.IOException;

/**
 * TODO: we should not refer to schema file (avsc files) from here. Refactor later.
 * <p>
 * Created on 1/6/17.
 */
@Configuration
@EnableMessage
@EnableBinding(value = {EmailEventStream.class})
public class EventConfiguration {

    private final AvRoMessageConverterLoader avroMessageConverterLoader;

    @Inject
    public EventConfiguration(final AvRoMessageConverterLoader avroMessageConverterLoader) {
        this.avroMessageConverterLoader = avroMessageConverterLoader;
    }

    @Bean(name = "submitDocumentEventConverter")
    public MessageConverter submitDocumentEventConverter() throws IOException {
        return avroMessageConverterLoader.load("SubmitDocumentAdminEvent.avsc").get();
    }

    @Bean(name = "completeProfileEventConverter")
    public MessageConverter completeProfileEventConverter() throws IOException {
        return avroMessageConverterLoader.load("CompleteProfileEvent.avsc").get();
    }

    @Bean(name = "orderCompletedEventConverter")
    public MessageConverter orderCompletedEventConverter() throws IOException {
        return avroMessageConverterLoader.load("OrderCompletedEvent.avsc").get();
    }

    @Bean(name = "changeSessionStatusEventConverter")
    public MessageConverter changeSessionStatusEventConverter() throws IOException {
        return avroMessageConverterLoader.load("ChangeSessionStatusEvent.avsc").get();
    }

    @Bean(name = "createdPackageEventConverter")
    public MessageConverter createdPackageEventConverter() throws IOException {
        return avroMessageConverterLoader.load("CreatedSessionPackageEvent.avsc").get();
    }

    @Bean(name = "transferSessionsEventConverter")
    public MessageConverter createdTransferSessionEventConverter() throws IOException {
        return avroMessageConverterLoader.load("TransferSessionPackageEvent.avsc").get();
    }

    @Bean(name = "orderSubscriptionCompletedEventConverter")
    public MessageConverter orderSubscriptionCompletedEventConverter() throws IOException {
        return avroMessageConverterLoader.load("OrderSubscriptionCompletedEvent.avsc").get();
    }
    
    @Bean(name = "orderCreditCompletedEventConverter")
    public MessageConverter orderCreditCompletedEventConverter() throws IOException {
        return avroMessageConverterLoader.load("OrderCreditCompletedEvent.avsc").get();
    }

    @Bean(name = "walletEventConverter")
    public  MessageConverter walletEventConverter() throws IOException {
        return  avroMessageConverterLoader.load("WalletEvent.avsc").get();
    }

    @Bean(name = "scheduleEventConverter")
    public  MessageConverter scheduleEventConverter() throws IOException {
        return  avroMessageConverterLoader.load("ScheduleEvent.avsc").get();
    }
    
    @Bean(name = "pushingNotificationEventConverter")
    public MessageConverter pushingNotificationEventConverter() throws IOException {
    	return avroMessageConverterLoader.load("PushingNotificationEvent.avsc").get();
    }
}
