package asia.cmg.f8.schedule.notification;

import asia.cmg.f8.schedule.client.SessionNotificationClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Push notification to user before the session will be started.
 * Created on 4/17/17.
 */
public class NotifyBeforeSessionStartJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyBeforeSessionStartJob.class);

    @Inject
    private SessionNotificationClient sessionNotificationClient;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        sessionNotificationClient.triggerNotification();
        LOGGER.info("Triggered sending session start notification");
    }
}
