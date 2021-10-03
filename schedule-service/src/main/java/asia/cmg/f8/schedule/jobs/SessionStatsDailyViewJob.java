package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SessionStatsDailyViewJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(SessionStatsDailyViewJob.class);

    @Inject
    private SessionClient sessionClient;

    @Override
    public void execute(final JobExecutionContext job) throws JobExecutionException {
        LOG.info("*** Start Trigger create session stats daily view *** ");
        sessionClient.sessionStatsDailyViewTrigger();
        LOG.info("*** End Trigger create session stats daily view *** ");
    }

}
