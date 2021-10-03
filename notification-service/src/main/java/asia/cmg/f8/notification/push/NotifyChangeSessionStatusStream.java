package asia.cmg.f8.notification.push;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 1/9/17.
 */
public interface NotifyChangeSessionStatusStream {

    String NOTIFY_CHANGE_SESSION_STATUS_INPUT = "notifyChangeSessionStatusInput";
    String NOTIFY_CHANGE_SESSION_STATUS_OUTPUT = "notifyChangeSessionStatusOutput";

    @Input(NOTIFY_CHANGE_SESSION_STATUS_INPUT)
    SubscribableChannel handleNotifyChangeSessionStatus();

    @Output(NOTIFY_CHANGE_SESSION_STATUS_OUTPUT)
    MessageChannel notifyChangeSessionStatus();
}
