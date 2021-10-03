package asia.cmg.f8.common.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

import java.util.Optional;

/**
 * Created on 11/17/16.
 */
public class AvRoMessageConverterLoader implements MessageConverterLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroSchemaMessageConverter.class);

    private static final MimeType MINE_TYPE = MimeType.valueOf("avro/bytes");

    @Override
    public Optional<MessageConverter> load(final String messageName) {
        final ClassPathResource resource = new ClassPathResource("/avro/" + messageName, this.getClass());
        if (!resource.exists()) {
            LOGGER.warn("Not found message resource /avro/{}", messageName);
            return Optional.empty();
        }
        final AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MINE_TYPE);
        converter.setSchemaLocation(resource);
        return Optional.of(converter);
    }
}
