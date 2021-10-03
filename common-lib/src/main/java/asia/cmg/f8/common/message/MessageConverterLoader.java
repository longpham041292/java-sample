package asia.cmg.f8.common.message;

import org.springframework.messaging.converter.MessageConverter;

import java.util.Optional;

/**
 * Created on 11/17/16.
 */
public interface MessageConverterLoader {

    Optional<MessageConverter> load(String messageName);
}
