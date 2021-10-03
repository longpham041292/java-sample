package asia.cmg.f8.report.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.report.service.OrderService;
import asia.cmg.f8.report.service.SessionStatusService;
import asia.cmg.f8.session.ChangeSessionStatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author tung.nguyenthanh
 */
@Component
@EnableBinding(ReportEventStream.class)
public class ReportEventConsumer {

    @Autowired
    @Qualifier("changeSessionStatusEventConverter")
    private MessageConverter changeSessionStatusEventConverter;

    @Autowired
    @Qualifier("orderCompletedEventConverter")
    private MessageConverter orderCompletedEventConverter;

    @Autowired
    private SessionStatusService sessionStatusService;

    @Inject
    private OrderService orderService;


    /**
     * Handle session change status event
     *
     * @param message
     */
    @StreamListener(ReportEventStream.CHANGE_SESSION_STATUS_CHANNEL)
    public void handleChangeSessionStatusEvent(final Message<?> message) {
        final ChangeSessionStatusEvent event =
                (ChangeSessionStatusEvent) changeSessionStatusEventConverter.fromMessage(
                        message, ChangeSessionStatusEvent.class);
        sessionStatusService.handle(event);
    }

    @StreamListener(ReportEventStream.COUNT_ORDER_COMPLETE_CHANNEL)
    public void countOrderComplete(final Message message) {
        final OrderCompletedEvent orderCompletedEvent = (OrderCompletedEvent) orderCompletedEventConverter
                .fromMessage(message, OrderCompletedEvent.class);
        orderService.increaseNumberOrderComplete(orderCompletedEvent);
    }

    @StreamListener(ReportEventStream.COUNT_ORDER_TOTAL_SESSION_CHANNEL)
    public void countOrderTotalSession(final Message message) {
        final OrderCompletedEvent orderCompletedEvent = (OrderCompletedEvent) orderCompletedEventConverter
                .fromMessage(message, OrderCompletedEvent.class);
        orderService.increaseNumOfSessions(orderCompletedEvent);
    }

}
