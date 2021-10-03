package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.session.dto.ReportActivitySessionRevenue;
import asia.cmg.f8.session.dto.SessionFinancialResponse;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.service.SessionReportService;
import asia.cmg.f8.session.utils.ReportUtils;

/**
 * Created on 12/15/16.
 */
@RestController
public class SessionReportApi {

    private static final String EU_USER_NAME = "EUuserName";
	private static final String EU_NAME = "EUname";
	private static final String PT_USER_NAME = "PTuserName";
	private static final String PT_NAME = "PTname";
    private static final String PRODUCT_NAME = "productName";
    private static final String PRICE = "price";
    private static final String ORIGINAL_PRICE = "originalPrice";
    private static final String DISCOUNT = "discount";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_UNIT_PRICE = "commissionUnitPrice";
    private static final String PT_COMMISSION_PERCENT = "ptCommissionPercent";
    private static final String CLUB_COMMISSION = "clubCommission";
    private static final String PT_COMMISSION = "ptCommission";
    private static final String PT_NET_INCOME = "ptNetIncome";
    private static final String PT_GROSS_INCOME = "ptGrossIncome";
    private static final String CLUB_CHECKIN_NAME = "checkinClubName";
    private static final String DATE_PURCHASE = "datePurchased";
    private static final String DATE_UPDATE = "dateUpdated";
    private static final String EXPIRED_DATE = "expiredDate";
    private static final String TRAINING_DATE = "trainingDate";
    private static final String OLD_PT_UUID = "oldPtUuid";

    private static final String PT_CLUB = "userclub";
    private static final String PURCHASE_ORDER_CLUB = "posClub";
    private static final String SESSION_TIME = "sessionTime";
    private static final String ORDER_CODE = "orderCode";
    private static final String CONTRACT_NUMBER = "contractNumber";
    private static final String TRAINER_STAFF = "trainerStaffCode";
    private static final String MEMBER_BAR_CODE = "memberBarCode";

    private static final String TOTAL_SESSION = "totalSession";
    private static final String USED_SESSION = "usedSession";
    private static final String REMAINING_SESSION = "remainingSession";
    private static final String LAST_STATUS = "lastStatus";
    private static final String COUPON_CODE = "couponCode";
    private static final String ONEPAY_TRANSACTION = "onepayTransaction";
    private static final String FREE_SESSION = "freeSession";
    private static final String ORIGINAL_SESSION = "originalSession";
    

	private static final String[] FINANCIAL_EXPORT_COMPLETED_HEADERS = {
	        SESSION_TIME, PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB, PRICE, COMMISSION, /*PRODUCT_UUID,*/
			COMMISSION_UNIT_PRICE, PT_COMMISSION_PERCENT, CLUB_COMMISSION, PT_COMMISSION, PT_GROSS_INCOME, PT_NET_INCOME, CLUB_CHECKIN_NAME, COUPON_CODE, ONEPAY_TRANSACTION, CONTRACT_NUMBER,
			DATE_PURCHASE, TRAINING_DATE, EXPIRED_DATE, PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE };
	
	private static final String[] FINANCIAL_EXPORT_HEADERS = {
		    SESSION_TIME, PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB, /*PRODUCT_UUID,*/
		    PRICE, COMMISSION, COMMISSION_UNIT_PRICE, DATE_PURCHASE, TRAINING_DATE, EXPIRED_DATE,
		    PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE };
	
	private static final String[] FINANCIAL_EXPORT_BURNED_HEADERS = {
	        SESSION_TIME, PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB, 
			PRICE, COMMISSION, COMMISSION_UNIT_PRICE, DATE_PURCHASE, TRAINING_DATE, EXPIRED_DATE,
            PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE, LAST_STATUS };
	
	private static final String[] OPEN_FINANCIAL_EXPORT_HEADERS = {
            PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB,
            PRICE, COMMISSION, COMMISSION_UNIT_PRICE, DATE_PURCHASE, EXPIRED_DATE,
            PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE };
	private static final String[] TRANSFER_FINANCIAL_EXPORT_HEADERS = {
            PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB,
            PRICE, COMMISSION, COMMISSION_UNIT_PRICE, DATE_PURCHASE, DATE_UPDATE, EXPIRED_DATE, OLD_PT_UUID,
			PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE };
	private static final String[] ORDER_FINANCIAL_EXPORT_HEADERS = {
            PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB,
            PRICE, ORIGINAL_PRICE, DISCOUNT,COUPON_CODE, COMMISSION_UNIT_PRICE, DATE_PURCHASE, EXPIRED_DATE,
            PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE,
            TOTAL_SESSION, ORIGINAL_SESSION,FREE_SESSION, USED_SESSION, REMAINING_SESSION, ONEPAY_TRANSACTION, CONTRACT_NUMBER
    };

    private static final String[] FREE_SESSION_EXPORT_HEADERS = {
            PRODUCT_NAME, ORDER_CODE, PURCHASE_ORDER_CLUB,
            PRICE, COMMISSION, COMMISSION_UNIT_PRICE,
            DATE_PURCHASE, DATE_UPDATE, EXPIRED_DATE,
            PT_NAME, PT_USER_NAME, PT_CLUB, TRAINER_STAFF, EU_NAME, EU_USER_NAME, MEMBER_BAR_CODE
    };

    private static final String[] EXPIRED_SESSION_EXPORT_HEADERS = FREE_SESSION_EXPORT_HEADERS;

    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String ALL_CONTRACT = "all_contracts";

    private final SessionReportService sessionReportService;
    
	private static final Logger LOG = LoggerFactory.getLogger(SessionReportApi.class);

	@Inject
	public SessionReportApi(final SessionReportService sessionReportService) {
		this.sessionReportService = sessionReportService;

	}

    @RequestMapping(value = "/admin/sessions/activity", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<ReportActivitySessionRevenue>> getFinancialActivityReport(
            @RequestParam(name = START_TIME, required = true) final String startDateStr,
            @RequestParam(name = END_TIME, required = true) final String endDateStr,
            final LanguageContext language) {
        final LocalDate startDate = ReportUtils.parseToLocalDate(startDateStr);
        final LocalDate endDate = ReportUtils.parseToLocalDate(endDateStr);
        
        
        return new ResponseEntity<>(sessionReportService.getActivitySessionReport(startDate,
                endDate, language.language()), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/reports/sessions/financial", method = RequestMethod.GET)
    @RequiredAdminRole
	public StreamingResponseBody exportFinancialReport(
			@RequestParam(name = START_TIME, required = true) final String startDateStr,
			@RequestParam(name = END_TIME, required = true) final String endDateStr,
			@RequestParam(name = ALL_CONTRACT, required = false, defaultValue = "false") final boolean allContracts,
			final LanguageContext language, final HttpServletResponse response) {
        final Map<String, List<SessionFinancialResponse>> allSessionTypes = sessionReportService
                .getSessionFinancialReport(startDateStr, endDateStr, language.language(), allContracts);

        
        final Map<String, String[]> headersMap = new HashMap<>();
        headersMap.put(SessionStatus.OPEN.name(), OPEN_FINANCIAL_EXPORT_HEADERS);
        headersMap.put(SessionStatus.CONFIRMED.name(), FINANCIAL_EXPORT_HEADERS);
        headersMap.put(SessionStatus.EU_CANCELLED.name(), FINANCIAL_EXPORT_HEADERS);
        headersMap.put(SessionStatus.BURNED.name(), FINANCIAL_EXPORT_BURNED_HEADERS);
        headersMap.put(SessionStatus.COMPLETED.name(), FINANCIAL_EXPORT_COMPLETED_HEADERS);
        headersMap.put(SessionStatus.EXPIRED.name(), EXPIRED_SESSION_EXPORT_HEADERS);
        headersMap.put(ReportUtils.TRANSFERRED, TRANSFER_FINANCIAL_EXPORT_HEADERS);
        headersMap.put(ReportUtils.FREE_SESSION, FREE_SESSION_EXPORT_HEADERS);
        headersMap.put(ReportUtils.ORDER, ORDER_FINANCIAL_EXPORT_HEADERS);
       

        return FileExportUtils.exportMultipleCSV(allSessionTypes, headersMap,
                "financial-report", response);
    }
}
