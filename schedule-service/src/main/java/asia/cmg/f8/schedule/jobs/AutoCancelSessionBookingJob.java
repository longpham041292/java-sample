package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.SessionClient;

public class AutoCancelSessionBookingJob implements Job {
	private static final Logger LOG = LoggerFactory.getLogger(AutoCancelSessionBookingJob.class);

	@Inject
	SessionClient sessionClient;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start Trigger auto cancel session bookings job *** ");

		sessionClient.autoCancelSessionBooking();

		LOG.info("*** End Trigger auto cancel session bookings job *** ");
	}
}