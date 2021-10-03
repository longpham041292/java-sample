package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class OrderReconcileDailyViewJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReconcileDailyViewJob.class);

    @Inject
    private SessionClient sessionClient;

    @Override
    public void execute(final JobExecutionContext job) throws JobExecutionException {
        LOG.info("*** Start Trigger to build order reconcile *** ");
        sessionClient.orderReconcileDailyViewTrigger();
        LOG.info("*** End Trigger to build order reconcile *** ");
    }

}
