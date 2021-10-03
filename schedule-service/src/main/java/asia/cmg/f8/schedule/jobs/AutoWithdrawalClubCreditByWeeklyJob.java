package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.CommerceClient;

public class AutoWithdrawalClubCreditByWeeklyJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoWithdrawalClubCreditByWeeklyJob.class);

	@Inject
	CommerceClient commerceClient;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOG.info("*** Start Trigger auto withdrawal club credits job *** ");

		commerceClient.autoWithdrawalClubCreditByWeeklyJob();

		LOG.info("*** End Trigger uto withdrawal club credits job *** ");
	}
}
