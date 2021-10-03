package asia.cmg.f8.report.service;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.report.utils.ReportConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author tung.nguyenthanh
 */
@Service
public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    @Inject
    private CounterService counterService;

    public void increaseNumberOrderComplete(final OrderCompletedEvent event) {
        LOG.info("Count event {}", ReportConstant.ORDER_COMPLETE_COUNTER);
        counterService.updateCounter(ReportConstant.ORDER_COMPLETE_COUNTER, ReportConstant.INCREASE,
                event.getSubmittedAt());
    }

    public void increaseNumOfSessions(final OrderCompletedEvent event) {
        LOG.info("Count event {}", ReportConstant.TOTAL_SESSION_COUNTER);
        counterService.updateCounter(ReportConstant.TOTAL_SESSION_COUNTER,
                event.getNumberOfSession(), event.getSubmittedAt());
    }
}
