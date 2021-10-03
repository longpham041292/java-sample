package asia.cmg.f8.session.event;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created on 1/6/17.
 */
public interface CreatedPackageEventStream {
    String CREATED_PACKAGE_EVENT_OUT = "createdPackageEventOutput";

    @Output(CREATED_PACKAGE_EVENT_OUT)
    MessageChannel createdPackage();
}
