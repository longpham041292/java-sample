package asia.cmg.f8.session.event;

import asia.cmg.f8.session.CreatedSessionPackageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Created on 1/6/17.
 */
@Component
@EnableBinding(CreatedPackageEventStream.class)
public class CreatedPackageHandler {

    @Autowired
    @Qualifier("createdPackageEventConverter")
    private MessageConverter createdPackageEventConverter;

    private final CreatedPackageEventStream eventStream;

    public CreatedPackageHandler(final CreatedPackageEventStream eventStream) {
        this.eventStream = eventStream;
    }

    public void publish(final CreatedSessionPackageEvent event) {
        eventStream.createdPackage().send(createdPackageEventConverter.toMessage(event, null));
    }

}
