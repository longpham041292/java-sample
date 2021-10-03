package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

public class ResetAutoFollowJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(ResetAutoFollowJob.class);

	@Value("${jobs.resetAutoFollowJob.durationInHour}")
	private int durationInHour;

	@Inject
	private SessionClient sessionClient;

	@Override
	public void execute(final JobExecutionContext job) throws JobExecutionException {
		LOG.info("*** Start Reset Auto Follow Job *** ");
		try {
			sessionClient.resetAutoFollowTrigger(durationInHour);
		} catch (final Exception ex) {
			LOG.error("Reset Auto Follow Job has error ", ex);
		}
		LOG.info("*** End Rest Auto Follow Job *** ");
	}

}
