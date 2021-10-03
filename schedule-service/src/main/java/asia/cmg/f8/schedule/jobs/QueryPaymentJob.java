package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.CommerceClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.List;

/**
 * @author tung.nguyenthanh
 */
public class QueryPaymentJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(QueryPaymentJob.class);

    @Inject
    private CommerceClient commerceClient;

    @Value("${jobs.paymentQueryJob.queryAfter}")
    private int queryAfter;

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) {
        LOG.info("*** START QUERY PAYMENT STATUS *** {}");
        final List<String> orders = commerceClient.getPendingOrders(queryAfter);
        orders.stream().forEach(uuid -> {
            LOG.info("Start query payment for order {}", uuid);
            final Boolean status = commerceClient.queryPaymentStatus(uuid);
            if (status != null) {
                LOG.info("Finish query payment for order {} with payment result: {}", uuid,
                        status);
            }
        });

        LOG.info("*** END QUERY PAYMENT STATUS ***");
    }


}
