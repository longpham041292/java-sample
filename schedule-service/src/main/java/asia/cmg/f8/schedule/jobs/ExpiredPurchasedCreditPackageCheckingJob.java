package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.CommerceClient;

public class ExpiredPurchasedCreditPackageCheckingJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(ExpiredPurchasedCreditPackageCheckingJob.class);
	
	@Inject
	private CommerceClient commerceClient;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOG.info("*** Start Trigger checking expired purchased credit packages *** ");
		
        commerceClient.checkExpiredPurchasedCreditPackage();
        
        LOG.info("*** End Trigger checking expired purchased credit packages *** ");
	}

}
