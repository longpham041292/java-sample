package asia.cmg.f8.notification.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 1/6/17.
 */
public interface CreatedPackageEventStream {

    String CREATED_PACKAGE_CHANNEL = "createdPackageEventInput";

    @Input(CREATED_PACKAGE_CHANNEL)
    SubscribableChannel consumeCreatedPackageEvent();
}
