package asia.cmg.f8.commerce.config;

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
    public MessageConverterConfiguration(
            final AvRoMessageConverterLoader avRoMessageConverterLoader) {
        this.avRoMessageConverterLoader = avRoMessageConverterLoader;
    }

    @Bean(name = "orderCompletedEventConverter")
    public MessageConverter orderCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderCompletedEvent.avsc").get();
    }
    
    @Bean(name = "orderSubscriptionCompletedEventConverter")
    public MessageConverter orderSubscriptionCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderSubscriptionCompletedEvent.avsc").get();
    }
    
    @Bean(name = "orderCreditCompletedEventConverter")
    public MessageConverter orderCreditCompletedEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("OrderCreditCompletedEvent.avsc").get();
    }

    @Bean(name = "walletEventConverter")
    public MessageConverter walletEventConverter() throws IOException {
        return avRoMessageConverterLoader.load("WalletEvent.avsc").get();
    }
}
