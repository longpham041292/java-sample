package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.CommerceClient;

public class AutoWithdrawalCreditsByWeeklyJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoWithdrawalCreditsByWeeklyJob.class);

	@Inject
	CommerceClient commerceClient;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOG.info("*** Start Trigger auto withdrawal credits job *** ");

		commerceClient.autoWithdrawalUserCreditByWeeklyJob();

		LOG.info("*** End Trigger uto withdrawal credits job *** ");
	}

}
