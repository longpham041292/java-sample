package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.inject.Inject;

import asia.cmg.f8.session.service.CreditBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.session.internal.service.InternalAvailabilityService;
import asia.cmg.f8.session.internal.service.InternalMVOrderReconcileService;
import asia.cmg.f8.session.internal.service.InternalMVSessionDailyService;
import asia.cmg.f8.session.internal.service.InternalMVSessionStatsDailyService;
import asia.cmg.f8.session.internal.service.InternalMVTrainerAnnualRevenueService;
import asia.cmg.f8.session.service.SessionService;
import asia.cmg.f8.session.service.WhosHotPTsService;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@RestController
public class InternalSessionApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(InternalSessionApi.class);

    @Inject
    private InternalMVSessionDailyService internalMVSessionDailyService;

    @Inject
    private InternalMVTrainerAnnualRevenueService revenueService;

    @Inject
    private InternalMVOrderReconcileService orderReconcileService;

    @Inject
    private InternalMVSessionStatsDailyService sessionStatsDailyService;

    @Inject
    private InternalAvailabilityService internalAvailabilityService;

    @Inject
    private WhosHotPTsService whosHotPTsService;
    
    @Inject
    private SessionService sessionService;

    @Inject
    private CreditBookingService creditBookingService;

    @RequestMapping(value = "/internal/session/daily-view-job", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public void runSessionDailyView() {
        internalMVSessionDailyService.runSessionDailyView();
    }

    @RequestMapping(value = "/internal/session/daily-stats-job", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public void runSessionDailyStatsView() {
        sessionStatsDailyService.runSessionStatsDailyView();
    }

    @RequestMapping(value = "/internal/trainer/annual-revenue-job", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public void runTrainerAnnualRevenue() {
        revenueService.runTrainerAnnualRevenue();
    }

    @RequestMapping(value = "/internal/order/reconcile-daily-job", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public void orderReconcilationJob() {
        orderReconcileService.buildOrderReconcile();
    }

    @RequestMapping(value = "/internal/session/add-availability-job", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public void runAddAvailabilitySlot() {
        LOGGER.info("Start add availability job");
        internalAvailabilityService.processAddAvailability();
        LOGGER.info("End add availability job");
    }

    @RequestMapping(value = "/internal/session/remove-old-availability-job",
            method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public void runRemoveOldAvailabilitySlot() {

        internalAvailabilityService.removeOldAvailability();
    }

    @RequestMapping(value = "/internal/whos-hot-pt-select-job",
            method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public void runwhosHotPTSelectAlgorithm() {

    	LOGGER.info("Start runwhosHotPTSelectAlgorithm job");
        whosHotPTsService.runWhosHotPTAlgorithm();
        LOGGER.info("End runwhosHotPTSelectAlgorithm job");
    }
    
    @RequestMapping(value ="/internal/session/auto-burn-confirm-job", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE) 
    public void autoBurnConfirmSessionJob() {
    	LOGGER.info("Start autoBurnConfirmSessionJob");
        sessionService.autoBurnConfirmSession();
        LOGGER.info("End autoBurnConfirmSessionJob");
    }

    @RequestMapping(value ="/internal/session/class-starting-remind-job", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public void remindClassStarting() {
    	LOGGER.info("Start remindClassStartingJob");
        creditBookingService.remindClassStarting();
        LOGGER.info("End remindClassStartingJob");
    }
}
