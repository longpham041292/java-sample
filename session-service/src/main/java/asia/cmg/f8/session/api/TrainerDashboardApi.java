package asia.cmg.f8.session.api;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.dto.TrainerSessionStats;
import asia.cmg.f8.session.service.SessionReportService;
import asia.cmg.f8.session.service.TrainerReportService;
import asia.cmg.f8.session.wrapper.dto.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
public class TrainerDashboardApi {

    private static final String TRAINER_ID = "trainer_id";
    private static final String RANGE = "range";

    private final SessionReportService sessionReportService;
    private final TrainerReportService trainerReportService;

    @Inject
    public TrainerDashboardApi(final SessionReportService sessionReportService, final TrainerReportService trainerReportService) {
        this.sessionReportService = sessionReportService;
        this.trainerReportService = trainerReportService;
    }

    @RequiredPTRole
    @GetMapping(value = "/trainers/me/stats/revenue", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<TrainerSessionStats>> getSessionRevenueReport(
            @RequestParam(name = "start_time", required = true) final long startTime,
            @RequestParam(name = "end_time", required = true) final long endTime,
            final Account account) {
        return getSessionRevenueReportOfGivenPt(startTime, endTime, account.uuid());
    }

    @RequiredAdminRole
    @GetMapping(path = "/trainers/{trainer_id}/stats/revenue", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<TrainerSessionStats>> getSessionRevenueReportOfGivenPt(
            @RequestParam(name = "start_time", required = true) final long startTime,
            @RequestParam(name = "end_time", required = true) final long endTime,
            @PathVariable(name = TRAINER_ID, required = true) final String userUuid) {
        return new ResponseEntity<>(sessionReportService.getPtStatsInPeriod(userUuid, startTime, endTime), HttpStatus.OK);
    }

    @RequestMapping(value = "/trainers/me/stats/clients", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredPTRole
    public ResponseEntity<List<UserSession>> getSessionClientReport(@RequestParam(
            name = "start_time", required = true) final long startTime, @RequestParam(
            name = "end_time", required = true) final long endTime, final Account account) {
        return new ResponseEntity<>(sessionReportService.getClientBurnedInOneDayOfTrainer(
                account.uuid(), startTime, endTime), HttpStatus.OK);
    }

    /**
     * Get Trainer Purchase history by range. This api is used in scoped report and PT Overview of Admin role.
     *
     * @param trainerUuid UUID of trainer
     * @param range       Value in week, month, year
     * @param language    Auto injected.
     * @return
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/purchase/{range}")
    @RequiredAdminRole
    public ResponseEntity<List<Map<String, List<String>>>> getTrainerPurchaseHistory(@PathVariable(TRAINER_ID) final String trainerUuid,
                                                                                     @PathVariable(RANGE) final String range,
                                                                                     final LanguageContext language) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerPurchaseHistory(trainerUuid, timeRange,
                timeInfo, language.language()), HttpStatus.OK);
    }

    /**
     * Get Trainer Purchase history by range. This api is used in scoped report and PT Overview of PT role.
     *
     * @param range    Value in week, month, year
     * @param language Auto injected.
     * @return
     */
    @RequestMapping(value = "/admin/trainers/me/purchase/{range}")
    @RequiredPTRole
    public ResponseEntity<List<Map<String, List<String>>>> getTrainerPurchaseHistory(@PathVariable(RANGE) final String range,
                                                                                     final LanguageContext language,
                                                                                     final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerPurchaseHistory(account.uuid(), timeRange,
                timeInfo, language.language()), HttpStatus.OK);
    }

    /**
     * Get Trainer Service Fee history by range. This api is used in scoped report and PT Overview of Admin role.
     * TODO: Add Service Fee to Purchase History
     *
     * @param trainerUuid UUID of trainer
     * @param range       Value in week, month, year
     * @param language    Auto injected.
     * @return
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/service-fee/{range}")
    @RequiredAdminRole
    public ResponseEntity<List<Map<String, List<String>>>> getTrainerServiceFeeHistory(@PathVariable(TRAINER_ID) final String trainerUuid,
                                                                                       @PathVariable(RANGE) final String range,
                                                                                       final LanguageContext language) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerServiceFeeHistory(trainerUuid, timeRange,
                timeInfo, language.language()), HttpStatus.OK);
    }

    /**
     * Get Trainer Service Fee history by range. This api is used in scoped report and require PT role.
     * TODO: Add Service Fee to Purchase History
     *
     * @param range    Value in week, month, year
     * @param language Auto injected.
     * @return
     */
    @RequestMapping(value = "/admin/trainers/me/service-fee/{range}")
    @RequiredPTRole
    public ResponseEntity<List<Map<String, List<String>>>> getTrainerServiceFeeHistory(@PathVariable(RANGE) final String range,
                                                                                       final LanguageContext language,
                                                                                       final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerServiceFeeHistory(account.uuid(), timeRange,
                timeInfo, language.language()), HttpStatus.OK);
    }

    /**
     * Get total new customers statistic for specific trainer by range.
     * This api is used in scoped report and PT Overview of Admin role.
     *
     * @param trainerId UUID of trainer
     * @param range     Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/stats/new-client/{range}")
    @RequiredAdminRole
    public ResponseEntity<?> getTrainerNewClientStatistic(@PathVariable(TRAINER_ID) final String trainerId,
                                                          @PathVariable(RANGE) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getNewCustomerByTrainerInRange(
                trainerId, timeInfo), HttpStatus.OK);
    }

    /**
     * Get total new customers statistic for specific trainer by range. This requires PT role.
     * This api is used in scoped report and PT Overview.
     *
     * @param range Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/me/stats/new-client/{range}")
    @RequiredPTRole
    public ResponseEntity<?> getTrainerNewClientStatistic(@PathVariable(RANGE) final String range,
                                                          final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getNewCustomerByTrainerInRange(
                account.uuid(), timeInfo), HttpStatus.OK);
    }

    /**
     * Get total burned sessions statistic for specific trainer in range. This requires Admin role
     * This api is used in scoped report and PT Overview of Admin role.
     *
     * @param trainerId UUID of trainer
     * @param range     Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/stats/burned/{range}")
    @RequiredAdminRole
    public ResponseEntity<?> getTrainerSessionBurnedStatistic(@PathVariable(TRAINER_ID) final String trainerId,
                                                              @PathVariable(RANGE) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getBurnedSessionStatisticByTrainerInRange(
                trainerId, timeInfo), HttpStatus.OK);
    }

    /**
     * Get total burned sessions statistic for specific trainer in range. This requires PT role.
     * This api is used in scoped report and PT Overview .
     *
     * @param range Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/me/stats/burned/{range}")
    @RequiredPTRole
    public ResponseEntity<?> getTrainerSessionBurnedStatistic(@PathVariable(RANGE) final String range,
                                                              final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getBurnedSessionStatisticByTrainerInRange(
                account.uuid(), timeInfo), HttpStatus.OK);
    }

    /**
     * Get paid out statistic for specific trainer in range. This required Admin role
     * This api is used in scoped report and PT Overview of Admin role.
     *
     * @param trainerId UUID of trainer
     * @param range     Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/stats/paid-out/{range}")
    @RequiredAdminRole
    public ResponseEntity<?> getTrainerPaidOutStatistic(@PathVariable(TRAINER_ID) final String trainerId,
                                                        @PathVariable(RANGE) final String range,
                                                        final LanguageContext language) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getPaidOutStatisticByTrainerInRange(
                trainerId, timeInfo, language.language()), HttpStatus.OK);
    }

    /**
     * Get paid out statistic for specific trainer in range. This required Admin role
     * This api is used in scoped report and PT Overview of Admin role.
     *
     * @param range Value in week, month, year
     * @return {@link StatisticResponse} of new customers in range.
     */
    @RequestMapping(value = "/admin/trainers/me/stats/paid-out/{range}")
    @RequiredPTRole
    public ResponseEntity<?> getTrainerPaidOutStatistic(@PathVariable(RANGE) final String range,
                                                        final LanguageContext language,
                                                        final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getPaidOutStatisticByTrainerInRange(
                account.uuid(), timeInfo, language.language()), HttpStatus.OK);
    }

}
