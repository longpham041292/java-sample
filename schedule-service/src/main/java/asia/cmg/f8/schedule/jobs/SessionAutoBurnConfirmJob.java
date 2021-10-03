package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.SessionClient;

public class SessionAutoBurnConfirmJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(SessionAutoBurnConfirmJob.class);
	@Inject
	private SessionClient sessionClient;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start SessionAutoBurnConfirmJob *** ");
		try {
			sessionClient.sessionAutoBurnConfirmTrigger();
		} catch (final Exception ex) {
			LOG.error("SessionAutoBurnConfirmJob has error ", ex);
		}
		LOG.info("*** End SessionAutoBurnConfirmJob *** ");
	}

}
