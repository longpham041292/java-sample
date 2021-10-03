package asia.cmg.f8.report.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author tung.nguyenthanh
 */
public interface ReportEventStream {

    String CHANGE_SESSION_STATUS_CHANNEL = "changeSessionStatusInput";
    String COUNT_ORDER_COMPLETE_CHANNEL = "orderCompletedInput";
    String COUNT_ORDER_TOTAL_SESSION_CHANNEL = "orderTotalSessionInput";

    @Input(CHANGE_SESSION_STATUS_CHANNEL)
    SubscribableChannel changeSessionStatus();

    @Input(COUNT_ORDER_COMPLETE_CHANNEL)
    SubscribableChannel countOrderComplete();

    @Input(COUNT_ORDER_TOTAL_SESSION_CHANNEL)
    SubscribableChannel countOrderTotalSession();

}
