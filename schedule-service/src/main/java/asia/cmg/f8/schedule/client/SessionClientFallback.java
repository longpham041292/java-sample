package asia.cmg.f8.schedule.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionClientFallback implements SessionClient {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFallback.class);

    @Override
    public void sessionDailyViewTrigger() {
        LOG.info("Error when calling session daily view");
    }

    @Override
    public void trainerAnnualRevenueTrigger() {
        LOG.info("Error when calling update trainer annual revenue");
    }

    @Override
    public void orderReconcileDailyViewTrigger() {
        LOG.info("Error when calling order reconcile");
    }

    @Override
    public void sessionStatsDailyViewTrigger() {
        LOG.info("Error when calling session stats daily view");
    }

    @Override
    public void sessionAddAvailabilityTrigger() {
        LOG.info("Error when calling session add availability to trainer.");
    }

    @Override
    public void sessionRemoveOldAvailabilityTrigger() {
        LOG.info("Error when calling session to remove old availability to trainer.");
    }

	@Override
	public void whosHotAlgorithmRunJobTrigger() {
		LOG.info("Error when calling whosHotAlgorithmRunJobTrigger.");

	}

	@Override
	public void resetAutoFollowTrigger(int durationInHour) {
		LOG.warn("Error when calling resetAutoFollowTrigger duration " + durationInHour);

	}

	@Override
	public void sessionAutoBurnConfirmTrigger() {
		LOG.warn("Error when calling sessionAutoBurnConfirmTrigger");
	}

	@Override
	public void autoBurnConfirmedCreditSessions() {
		LOG.info("Error when calling autoBurnConfirmedCreditSessions");
	}

	@Override
	public void autoDeductBurnedCreditSessions() {
		LOG.info("Error when calling autoDeductBurnedCreditSessions");
	}

    @Override
    public void autoBurningClassBookingsJob() {
        LOG.info("Error when calling bookingClassCancelJob");
    }

    @Override
    public void autoBurningEticketBookingsJob() {
        LOG.info("Error when calling bookingEticketCancelJob");
    }
    
    @Override
	public void autoCancelSessionBooking() {
		LOG.info("Error when calling cancelSessionBookingJob");
	}

	@Override
    public void remindClassStartingTrigger()  {
        LOG.info("Error when calling remindClassStartingTrigger");
    }
}
