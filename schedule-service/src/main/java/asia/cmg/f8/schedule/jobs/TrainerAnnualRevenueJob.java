package asia.cmg.f8.schedule.jobs;

import asia.cmg.f8.schedule.client.SessionClient;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class TrainerAnnualRevenueJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerAnnualRevenueJob.class);

    @Inject
    private SessionClient sessionClient;

    @Override
    public void execute(final JobExecutionContext job) throws JobExecutionException {
        LOG.info("*** Start Trigger to update trainer revenue *** ");
        sessionClient.trainerAnnualRevenueTrigger();
        LOG.info("*** End Trigger to update trainer revenue *** ");
    }

}
