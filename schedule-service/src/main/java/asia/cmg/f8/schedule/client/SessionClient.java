package asia.cmg.f8.schedule.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "session", url = "${service.sessionUrl}",
        fallback = SessionClientFallback.class)
public interface SessionClient {

    @RequestMapping(value = "/internal/session/daily-view-job", method = RequestMethod.POST)
    void sessionDailyViewTrigger();

    @RequestMapping(value = "/internal/session/daily-stats-job", method = RequestMethod.POST)
    void sessionStatsDailyViewTrigger();

    @RequestMapping(value = "/internal/trainer/annual-revenue-job", method = RequestMethod.POST)
    void trainerAnnualRevenueTrigger();

    @RequestMapping(value = "/internal/order/reconcile-daily-job", method = RequestMethod.POST)
    void orderReconcileDailyViewTrigger();

    @RequestMapping(value = "/internal/session/add-availability-job", method = RequestMethod.POST)
    void sessionAddAvailabilityTrigger();

    @RequestMapping(value = "/internal/session/remove-old-availability-job", method = RequestMethod.POST)
    void sessionRemoveOldAvailabilityTrigger();


    @RequestMapping(value = "/internal/whos-hot-pt-select-job", method = RequestMethod.POST)
    void whosHotAlgorithmRunJobTrigger();

    @RequestMapping(value = "/updateautofollow-reset-job?durationInHour={durationInHour}", method = RequestMethod.POST)
    void resetAutoFollowTrigger(@PathVariable("durationInHour") final int durationInHour);

    @RequestMapping(value = "/internal/session/auto-burn-confirm-job", method = RequestMethod.POST)
    void sessionAutoBurnConfirmTrigger();

    @RequestMapping(value = "/internal/v1/credit/booking/sessions/auto-burned", method = RequestMethod.POST)
    void autoBurnConfirmedCreditSessions();

    @RequestMapping(value = "/internal/v1/credit/booking/sessions/auto-deducted", method = RequestMethod.POST)
    void autoDeductBurnedCreditSessions();

    @RequestMapping(value = "/internal/v1/credit/booking/classes/auto-burned", method = RequestMethod.POST)
    void autoBurningClassBookingsJob();

    @RequestMapping(value = "/internal/v1/credit/booking/etickets/auto-burned", method = RequestMethod.POST)
    void autoBurningEticketBookingsJob();
    
    @RequestMapping(value = "/internal/v1/credit/booking/session/auto-cancel-booking", method = RequestMethod.POST)
    void autoCancelSessionBooking();

    @RequestMapping(value = "/internal/session/class-starting-remind-job", method = RequestMethod.POST)
    void remindClassStartingTrigger();
}
