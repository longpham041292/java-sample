package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.SessionClient;

public class AutoBurningOfConfirmedCreditSessionJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoBurningOfConfirmedCreditSessionJob.class);
	
	@Inject
	SessionClient sessionClient;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start Trigger auto burning confirmed credit sessions *** ");
		
        sessionClient.autoBurnConfirmedCreditSessions();
        
        LOG.info("*** End Trigger auto burning confirmed credit sessions *** ");
	}

}
