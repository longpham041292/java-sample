package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class AutoBurningClassBookingsJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoBurningClassBookingsJob.class);

	@Inject
	SessionClient sessionClient;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start Trigger auto burning class bookings job *** ");

        sessionClient.autoBurningClassBookingsJob();

        LOG.info("*** End Trigger auto burning class bookings job *** ");
	}

}
