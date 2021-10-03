package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.ClientSessionInfo;
import asia.cmg.f8.session.dto.PtExport;
import asia.cmg.f8.session.dto.PtListingInfo;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.dto.TrainerActivity;
import asia.cmg.f8.session.service.TrainerReportService;
import asia.cmg.f8.session.service.TrainerReportStatsService;
import asia.cmg.f8.session.service.UserService;
import asia.cmg.f8.session.utils.ReportUtils;

@RestController
@SuppressWarnings("PMD.ExcessiveImports")
public class TrainerReportApi {

    private static final String[] TRAINER_SCOPED_REPORT_HEADER = {"sessionUuid", "productName",
            "orderUuid", "serviceFee", "status", "purchasedDate","purchasedTime", "usedDate","usedTime", "name",
            "userName","expiredDate"};
    private static final String[] CLIENTS_EXPORT_HEADERS = {"userName", "fullName",
            "numberOfBurnedSessions", "numberOfSessions", "packagePrice", "status",
            "expirationDate"};

//    private static final String[] TRAINER_HEADER = {"uuid", "name", "city", "country",
//            "activated", "displayPtRevenue", "displayF8Revenue", "joinDate","joinTime", "email", "documentStatus",
//            "level"};
    private static final String[] TRAINER_HEADER = {"name","username","staffCode","city", "country",
            "activated", "displayPtRevenue", "displayF8Revenue", "joinDate","joinTime", "email", "documentStatus",
            "level"};

    private final static String FULL_NAME = "full_name";
    private final static String KEYWORD = "keyword";
    private static final String START_TIME = "from";
    private static final String END_TIME = "to";

    private static final Logger LOG = LoggerFactory.getLogger(TrainerReportApi.class);
    
    public static final SortFieldMapping SORT_FIELD_MAPPING_FOR_CLIENT_OF_PT = new SortFieldMapping() {
        @Override
        public String getSessionBurnedSortField() {
            return "client.num_of_burned";
        }

        @Override
        public String getExpiredDateSortField() {
            return "client.expired_date";
        }

        @Override
        public String getPriceSortField() {
            return "client.price";
        }

        @Override
        public String getCommissionSortField() {
            return "client.commission";
        }

        @Override
        public String getFullNameSortField() {
            return "client.full_name";
        }
    };

    private static final String FILE_NAME_CLIENTS_REPORT = "financial-report";
    private static final String TRAINER_ID = "trainer_id";
    private static final String RANGE = "range";
    

    private final UserService userService;

    @Inject
    private SessionProperties sessionProps;

    private final TrainerReportService trainerReportService;

    private final TrainerReportStatsService trainerReportStatsService;

    @Inject
    public TrainerReportApi(final TrainerReportService trainerReportService,
                            final UserService userService, final TrainerReportStatsService trainerReportStatsService) {
        this.trainerReportService = trainerReportService;
        this.userService = userService;
        this.trainerReportStatsService = trainerReportStatsService;
    }

    @RequestMapping(value = "/admin/trainers", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> listTrainers(
            @RequestParam(value = KEYWORD, required = false) String keyword,
            @PageableDefault(sort = {FULL_NAME}) Pageable pageable) {

        keyword = keyword == null ? "" : keyword;
        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();
        final String resolveSortValue = SortFieldConverter
                .getSortField(sortField, ReportUtils.SORT_FIELD_MAPPING_FOR_PT_LISTING);

        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                order.getDirection(), resolveSortValue);

        final int year = LocalDateTime.now().getYear();
        final List<PtListingInfo> trainers;
        if (StringUtils.isEmpty(keyword)) {
            trainers = userService.getAllTrainers(year, pageable);
        } else {
            trainers = userService.searchTrainers(year, keyword, pageable);
        }
        return new ResponseEntity<>(trainers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/admin/trainers/v1", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> listTrainers(
            @RequestParam(value = KEYWORD, required = false) String keyword,
            @RequestParam(value = START_TIME) final Long fromDateValue,
            @RequestParam(value = END_TIME) final Long toDateValue,
            @PageableDefault(sort = {FULL_NAME}) Pageable pageable) {

        keyword = keyword == null ? "" : keyword;
        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();
        final String resolveSortValue = SortFieldConverter
                .getSortField(sortField, ReportUtils.SORT_FIELD_MAPPING_FOR_PT_LISTING);

        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                order.getDirection(), resolveSortValue);

        final int year = LocalDateTime.now().getYear();
        final List<PtListingInfo> trainers;
        final LocalDateTime fromDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDateValue), TimeZone.getDefault().toZoneId());
        final LocalDateTime toDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDateValue), TimeZone.getDefault().toZoneId());
        
        if (StringUtils.isEmpty(keyword)) {
            trainers = userService.getAllTrainers(fromDate, toDate, pageable);
        } else {
            trainers = userService.searchTrainers(fromDate, toDate, keyword, pageable);
        }
        return new ResponseEntity<>(trainers, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/trainers/export", method = RequestMethod.GET)
    @RequiredAdminRole
    public StreamingResponseBody exportAllTrainers(final HttpServletResponse response)
            throws IOException {
        final List<PtExport> trainers = userService.exportTrainers(LocalDateTime.now().getYear());

        return FileExportUtils.exportCSV(trainers, TRAINER_HEADER, sessionProps.getExport()
                .getMembers(), response);
    }

    /**
     * @param trainerId Trainer Id
     * @param pageable
     * @return list of client of given trainer
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/contracting/users",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> listClientOfPTUser(
            @PathVariable(TRAINER_ID) final String trainerId,
            @RequestParam(value = KEYWORD, required = false, defaultValue = "") final String keyword,
            @RequestParam(value = "active_only", required = false, defaultValue = "false") final boolean activeOnly,
            final LanguageContext language,
            @PageableDefault(sort = {FULL_NAME}) final Pageable pageable) {
        final Sort.Order order = pageable.getSort().iterator().next();

        final String sortField = order.getProperty();

        final String resolveSortValue = SortFieldConverter.getSortField(sortField, SORT_FIELD_MAPPING_FOR_CLIENT_OF_PT);

        final Pageable wrappedPageable = new PageRequest(pageable.getPageNumber(),
                pageable.getPageSize(), order.getDirection(), resolveSortValue);

        if (activeOnly) {
            return new ResponseEntity<>(
                    userService.getClientOfPtWithActiveOrder(
                            trainerId, keyword, language.language(), wrappedPageable),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                userService.getClientOfPt(trainerId, keyword, language.language(), wrappedPageable),
                HttpStatus.OK);
    }

    /**
     * This API get list of clients for specific trainer.
     *
     * @param pageable
     * @return list of client of given trainer
     */
    @RequestMapping(value = "/admin/trainers/me/contracting/users", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredPTRole
    public ResponseEntity<?> listClientOfTrainer(@RequestParam(value = KEYWORD, required = false,
            defaultValue = "") final String keyword,
                                                 @PageableDefault(sort = {FULL_NAME}) final Pageable pageable,
                                                 final LanguageContext language, final Account account) {
        final Sort.Order order = pageable.getSort().iterator().next();

        final String sortField = order.getProperty();

        final String resolveSortValue = SortFieldConverter.getSortField(sortField,
                SORT_FIELD_MAPPING_FOR_CLIENT_OF_PT);

        final Pageable wrappedPageable = new PageRequest(pageable.getPageNumber(),
                pageable.getPageSize(), order.getDirection(), resolveSortValue);

        return new ResponseEntity<>(userService.getClientOfPt(account.uuid(), keyword,
                language.language(), wrappedPageable), HttpStatus.OK);
    }

    /**
     * This API export list of clients for specific trainer. This require Admin
     * role
     *
     * @param keyword
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/admin/trainers/me/contracting/users/export",
            method = RequestMethod.GET)
    @RequiredPTRole
    public StreamingResponseBody exportListClientOfTrainer(@RequestParam(value = KEYWORD,
            required = false, defaultValue = "") final String keyword,
                                                           final LanguageContext language, final Account account,
                                                           final HttpServletResponse response) throws IOException {
    	
    	final List<ClientSessionInfo> clientOfPt = userService.getClientOfPt(account.uuid(),
                keyword, language.language(), null);
        final List<ClientSessionInfo.ClientSessionInfoExport> clientSessionInfoExportList = clientOfPt
                .stream()
                .map(info -> info.getExportResponse(Locale.forLanguageTag(language.language())))
                .collect(Collectors.toList());

        return FileExportUtils.exportCSV(clientSessionInfoExportList, CLIENTS_EXPORT_HEADERS,
                FILE_NAME_CLIENTS_REPORT, response);
    }

    /**
     * This API get all activities of specific trainer in range which is used by
     * Admin.
     *
     * @param trainerId
     * @param range
     * @param pageable
     * @param language
     * @return
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/activities/{range}",
            method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> getTrainerActivitySessions(
            @PathVariable(TRAINER_ID) final String trainerId,
            @PathVariable(RANGE) final String range, @PageableDefault(
            sort = {ReportUtils.FULL_NAME}) final Pageable pageable,
            final LanguageContext language) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerActivityAndRevenue(trainerId,
                timeInfo, pageable, language.language()), HttpStatus.OK);
    }

    /**
     * This API get all activities of current trainer in range which is used by
     * PT.
     *
     * @param range
     * @param pageable
     * @param language
     * @return
     */
    @RequestMapping(value = "/admin/trainers/me/activities/{range}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredPTRole
    public ResponseEntity<?> getTrainerActivitySessions(@PathVariable(RANGE) final String range,
                                                        @PageableDefault(sort = {ReportUtils.FULL_NAME}) final Pageable pageable,
                                                        final LanguageContext language, final Account account) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportService.getTrainerActivityAndRevenue(
                account.uuid(), timeInfo, pageable, language.language()), HttpStatus.OK);
    }

    /**
     * This API export all activities of specific trainer in range which is used
     * by Admin.
     *
     * @param trainerId
     * @param range
     * @param response
     * @param language
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/admin/trainers/{trainer_id}/activities/{range}/export",
            method = RequestMethod.GET)
    @RequiredAdminRole
    public StreamingResponseBody exportTrainerActivitySessions(
            @PathVariable(TRAINER_ID) final String trainerId,
            @PathVariable(RANGE) final String range, final HttpServletResponse response,
            final LanguageContext language) throws IOException {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        final List<TrainerActivity> trainerActivityAndRevenue = trainerReportService
                .getTrainerActivityAndRevenueExport(trainerId, timeInfo);

        final String reportName = "trainer-scoped-report" + trainerId;
        return FileExportUtils.exportCSV(trainerActivityAndRevenue, TRAINER_SCOPED_REPORT_HEADER,
                reportName, response);
    }

    /**
     * This API export all activities of current trainer in range which is used
     * by Trainer.
     *
     * @param range
     * @param response
     * @param language
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/admin/trainers/me/activities/{range}/export",
            method = RequestMethod.GET)
    @RequiredPTRole
    public StreamingResponseBody exportTrainerActivitySessions(
            @PathVariable(RANGE) final String range, final HttpServletResponse response,
            final LanguageContext language, final Account account) throws IOException {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);
        LOG.info("### --- >>> --- >>> Report 2");
        final List<TrainerActivity> trainerActivityAndRevenue = trainerReportService
                .getTrainerActivityAndRevenueExport(account.uuid(), timeInfo);

        final String reportName = "trainer-scoped-report" + account.uuid();
        return FileExportUtils.exportCSV(trainerActivityAndRevenue, TRAINER_SCOPED_REPORT_HEADER,
                reportName, response);
    }

    @RequestMapping(value = "/admin/trainers/active/total", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Map<String, Integer>> getTotalActiveTrainer() {
        final int total = trainerReportStatsService.countActiveTrainers();
        return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/trainers/stats/new-certified/{range}",
            method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<StatisticResponse> getTrainerStatsNewCerts(
            @PathVariable(RANGE) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(trainerReportStatsService.getTrainerStatsNewCerts(timeInfo),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/trainers/stats/scheduled/{range}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<StatisticResponse> getTrainerStatsScheduled(
            @PathVariable(RANGE) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(
                trainerReportStatsService.getTrainerStatsScheduledSession(timeInfo), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/trainers/stats/session-pt-client/{range}",
            method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<StatisticResponse>> getStatsTrainerClientScheduledCancelled(
            @PathVariable(RANGE) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(
                trainerReportStatsService.getStatsTrainerClientScheduledCancelled(timeInfo,
                        timeRange), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/trainers/stats/pending", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Map<String, Integer>> getPendingStatus() {
        return new ResponseEntity<>(Collections.singletonMap("total",
                trainerReportStatsService.countTrainerPendingStatus()), HttpStatus.OK);
    }

}
