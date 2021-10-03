package asia.cmg.f8.schedule.jobs;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.schedule.client.SessionClient;

public class WhosHotAlgorithmRunJob implements Job{

	private static final Logger LOG = LoggerFactory.getLogger(WhosHotAlgorithmRunJob.class);
	
	@Inject
    private SessionClient sessionClient;
	
	@Override
    public void execute(final JobExecutionContext job) throws JobExecutionException {
		 LOG.info("<< *** Start Trigger who's Hot PT Select Algorithm Run Job *** >>");
	     sessionClient.whosHotAlgorithmRunJobTrigger();
	     LOG.info("<< *** End Trigger who's Hot PTS electAlgorithm Run Job *** >>");
      
        
    }
}
