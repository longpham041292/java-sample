package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.SessionClient;

public class AutoDeductingOfBurnedCreditSessionJob implements Job {

private static final Logger LOG = LoggerFactory.getLogger(AutoDeductingOfBurnedCreditSessionJob.class);
	
	@Inject
	SessionClient sessionClient;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start Trigger auto deducting burned credit sessions *** ");
		
        sessionClient.autoDeductBurnedCreditSessions();
        
        LOG.info("*** End Trigger auto deducting burned credit sessions *** ");
	}
}
