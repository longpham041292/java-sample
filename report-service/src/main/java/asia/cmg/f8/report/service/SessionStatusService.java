package asia.cmg.f8.report.service;

import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.report.utils.ReportConstant;
import asia.cmg.f8.report.utils.ReportUtils;
import asia.cmg.f8.session.ChangeSessionStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

/**
 * @author tung.nguyenthanh
 */
@Service
public class SessionStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionStatusService.class);

    @Inject
    private CounterService counterService;

    public void handle(final ChangeSessionStatusEvent event) {
        final String status = event.getNewStatus().toString();
        if (isSupportStatus(SessionStatus.valueOf(status))) {
            final String counterName = ReportUtils.getSessionCounterName(status);
            if (!StringUtils.isEmpty(counterName)) {
                LOG.info("Count event {}", counterName);
                counterService.updateCounter(counterName, ReportConstant.INCREASE,
                        event.getSubmittedAt());
            }
        }
    }

    private boolean isSupportStatus(final SessionStatus status) {
        switch (status) {
            case COMPLETED:
            case BURNED:
            case EU_CANCELLED:
                return true;
            default:
                return false;
        }
    }

}
