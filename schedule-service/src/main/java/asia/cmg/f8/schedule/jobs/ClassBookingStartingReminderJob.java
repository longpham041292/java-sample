package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ClassBookingStartingReminderJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(ClassBookingStartingReminderJob.class);
    @Inject
    private SessionClient sessionClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("*** Start ClassBookingStartingReminderJob *** ");
        try {
            sessionClient.remindClassStartingTrigger();
        } catch (final Exception ex) {
            LOG.error("ClassBookingStartingReminderJob has error ", ex);
        }
        LOG.info("*** End ClassBookingStartingReminderJob *** ");
    }
}
