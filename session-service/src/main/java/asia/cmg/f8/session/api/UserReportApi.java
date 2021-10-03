package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BasicUserOrder;
import asia.cmg.f8.session.dto.PTSessionInfo;
import asia.cmg.f8.session.dto.StatisticResponse;
import asia.cmg.f8.session.service.UserService;
import asia.cmg.f8.session.utils.ReportUtils;

@RestController
@SuppressWarnings("PMD.ExcessiveImports")
public class UserReportApi {

//    private static final String[] MEMBER_HEADER = { "uuid", "name","userName", "city", "country",
//            "sessionBurned", "sessionNumber", "displayExpiredDate", "activated", "email",
//            "displayJoinDate","joinTime" };
    private static final String[] MEMBER_HEADER = {"name","userName", "memberCode","city", "country",
            "sessionBurned", "sessionNumber", "displayExpiredDate", "activated", "email",
            "displayJoinDate","joinTime","phone"};
    private static final String USER_UUID = "userUuid";
    
    private static final String START_TIME = "from";
    private static final String END_TIME = "to";
    private final static String FULL_NAME = "full_name";
    private final static String KEYWORD = "keyword";

    public static final SortFieldMapping SORT_FIELD_MAPPING_LIST_PT_OF_USER = new SortFieldMapping() {
        @Override
        public String getSessionBurnedSortField() {
            return "trainer.num_of_burned";
        }

        @Override
        public String getExpiredDateSortField() {
            return "trainer.expired_date";
        }

        @Override
        public String getPriceSortField() {
            return "trainer.price";
        }

        @Override
        public String getCommissionSortField() {
            return "trainer.commission";
        }

        @Override
        public String getFullNameSortField() {
            return "trainer.full_name";
        }
    };

    @Inject
    private UserService userService;

    @Inject
    private SessionProperties sessionProps;

    @RequestMapping(value = "/admin/users", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> listMember(
            @RequestParam(value = "keyword", required = false) String keyword, @PageableDefault(
                    sort = { ReportUtils.NAME }) Pageable pageable) {
        keyword = keyword == null ? "" : keyword;
        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();
        final String resolveSortValue = ReportUtils.resolveSortValue(sortField);

        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                order.getDirection(), resolveSortValue);

        return new ResponseEntity<>(userService.getAllMembers(keyword, pageable), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/admin/users/v1", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> listMemberByRangeTime(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = START_TIME) final Long fromDateValue,
            @RequestParam(value = END_TIME) final Long toDateValue,
            @PageableDefault(sort = { ReportUtils.FULL_NAME }) Pageable pageable) {
        keyword = keyword == null ? "" : keyword;
        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();
       
        final String resolveSortValue = SortFieldConverter
                .getSortField(sortField, ReportUtils.SORT_FIELD_MAPPING_FOR_USER_LISTING);
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                order.getDirection(), resolveSortValue);
    	final LocalDateTime fromDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDateValue), TimeZone.getDefault().toZoneId());
        final LocalDateTime toDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDateValue), TimeZone.getDefault().toZoneId());
        
        return new ResponseEntity<>(userService.getAllMembersByTimeRange(keyword, pageable, fromDate, toDate), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/users/export", method = RequestMethod.GET)
    @RequiredAdminRole
    public StreamingResponseBody exportMemberList(@RequestParam(name = "from") final long fromDateValue, 
    											@RequestParam(name = "to") final long toDateValue, 
    											final HttpServletResponse response)
            throws IOException {
    	
    	LocalDateTime fromDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDateValue), TimeZone.getDefault().toZoneId());
    	LocalDateTime toDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDateValue), TimeZone.getDefault().toZoneId());
        final List<BasicUserOrder> users = userService.getAllMembers(fromDate, toDate);

        return FileExportUtils.exportCSV(users, MEMBER_HEADER, sessionProps.getExport()
                .getMembers(), response);
    }    

    @RequestMapping(value = "/admin/users/{userUuid}/contracting/trainers",
            method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<PTSessionInfo>> listPTsOfUser(
            @PathVariable(USER_UUID) final String userUuid, @RequestParam(value = "active_only",
                    required = false, defaultValue = "false") final boolean activeOnly,
            @RequestParam(value = "keyword", required = false) String keyword, @PageableDefault(
                    sort = { ReportUtils.NAME }) Pageable pageable, final LanguageContext language) {
        keyword = keyword == null ? "" : keyword;
        final Sort.Order order = pageable.getSort().iterator().next();
        final String sortField = order.getProperty();

        final String resolveSortValue = SortFieldConverter.getSortField(sortField,
                SORT_FIELD_MAPPING_LIST_PT_OF_USER);

        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),
                order.getDirection(), resolveSortValue);

        if (activeOnly) {
            return new ResponseEntity<>(userService.getPTsOfUserWithActiveOrder(userUuid, keyword,
                    language.language(), pageable), HttpStatus.OK);
        }

        return new ResponseEntity<>(userService.getPTsOfUser(userUuid, keyword,
                language.language(), pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/users/{userUuid}/stats/{range}")
    @RequiredAdminRole
    public ResponseEntity<List<StatisticResponse>> getSessionStatistic(@PathVariable(
            name = USER_UUID, required = true) final String userUuid, @PathVariable(name = "range",
            required = true) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(userService.getUserSessionStatistic(userUuid, timeInfo),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/users/{userUuid}/stats/confirmed/{range}")
    @RequiredAdminRole
    public ResponseEntity<StatisticResponse> getSessionConfirmedStatistic(@PathVariable(
            name = USER_UUID, required = true) final String userUuid, @PathVariable(name = "range",
            required = true) final String range) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);

        return new ResponseEntity<>(userService.getUserSessionConfirmStatistic(userUuid, timeInfo),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/users/{userUuid}/purchase/{range}")
    @RequiredAdminRole
    public ResponseEntity<List<Map<String, List<String>>>> getMemberPurchaseHitory(@PathVariable(
            name = USER_UUID, required = true) final String userUuid, @PathVariable(name = "range",
            required = true) final String range, final LanguageContext language) {
        final TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = DateUtils.calculateLocalTimeRange(timeRange);
        return new ResponseEntity<>(userService.getMemberPurchaseHitory(userUuid, timeRange,
                timeInfo, language.language()), HttpStatus.OK);
    }

}
