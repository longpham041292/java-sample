package asia.cmg.f8.report.api;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.report.dto.BookingHistoryDTO;
import asia.cmg.f8.report.dto.PTClientsDTO;
import asia.cmg.f8.report.dto.PageResponse;
import asia.cmg.f8.report.dto.SessionExportDTO;
import asia.cmg.f8.report.repository.SessionReportRepository;
import asia.cmg.f8.report.service.CreditBookingService;
import asia.cmg.f8.report.service.CreditSessionBookingService;

/**
 * @author User
 *
 */
@RestController
public class AdminReportApi {

	@Autowired
	SessionReportRepository sessionReportRepo;
	
	@Autowired
	CreditSessionBookingService creditSessionBookingService;
	
	@Autowired
	CreditBookingService creditBookingService;

	
	private static final Logger LOG = LoggerFactory.getLogger(AdminReportApi.class);
	
	private static final String[] SESSION_EXPORT_HEADERS = {"sessionId", "sessionStartTime", "sessionEndTime", "sessionStatus", "userUuid", "userFullname", "trainerFullname",
            				"trainerLevel", "bookingClubName", "bookingClubAddress", "checkingClubName", "checkingClubAddress", "checkinTime"};
	private static final String[] BOOKING_HISTORY_EXPORT_HEADERS = {"date", "time", "partnerName", "serviceType", "creditAmount", "serviceFee", "commission", "status"};
	
	@GetMapping(value = "/admin/v1/sessions/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody adminExportSessionsDetail(@RequestParam(value = "startTime", required = true) long startTime, 
															@RequestParam(value = "endTime", required = true) long endTime,
															@RequestParam(value = "checkingClubUuid", required = false) String checkingClubUuid, 
															final HttpServletResponse response) {
		final String filename = "sessions_detail.csv";
		
		try {
			List<SessionExportDTO> sessions = Collections.emptyList();
			SessionStatus COMPLETED_STATUS = SessionStatus.COMPLETED;
			LocalDateTime ldtStartTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(startTime);
			LocalDateTime ldtEndTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(endTime);
			
			if(checkingClubUuid == null || checkingClubUuid.isEmpty()) {
				sessions = sessionReportRepo.exportSessionQuery(COMPLETED_STATUS, ldtStartTime, ldtEndTime);
			} else {
				sessions = sessionReportRepo.exportSessionQueryFilterByCheckingClubUuid(COMPLETED_STATUS, ldtStartTime, ldtEndTime, checkingClubUuid);
			}
			
			return FileExportUtils.exportCSV(sessions, SESSION_EXPORT_HEADERS, filename, response);
		} catch (Exception e) {
			LOG.error("[adminExportSessionsDetail] failed: {}", e.getMessage());
			return null;
		}
	}
	
	@GetMapping(value = "/admin/v1/sessions/report", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> adminReportSessionsDetail(@RequestParam(value = "startTime", required = true) long startTime, 
															@RequestParam(value = "endTime", required = true) long endTime,
															@RequestParam(value = "checkingClubUuid", required = false) String checkingClubUuid, 
															final HttpServletResponse response) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<SessionExportDTO> sessions = Collections.emptyList();
			SessionStatus COMPLETED_STATUS = SessionStatus.COMPLETED;
			LocalDateTime ldtStartTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(startTime);
			LocalDateTime ldtEndTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(endTime);
			
			if(checkingClubUuid == null || checkingClubUuid.isEmpty()) {
				sessions = sessionReportRepo.exportSessionQuery(COMPLETED_STATUS, ldtStartTime, ldtEndTime);
			} else {
				sessions = sessionReportRepo.exportSessionQueryFilterByCheckingClubUuid(COMPLETED_STATUS, ldtStartTime, ldtEndTime, checkingClubUuid);
			}
			
			apiResponse.setData(sessions);
			
		} catch (Exception e) {
			LOG.error("[adminExportSessionsDetail] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	/** @apiNote: get booking history
	 * @author long
	 * @email longpham@leep.app
	 * @param uuid, fromDate, toDate, partnerName, pageable
	 * @return ResponesEntity<Object>
	 **/
	@GetMapping(value = "/admin/v1/wallets/credit/bookings/client/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getBookingsHistory(@PathVariable(value = "uuid", required = true) final String uuid,
			@RequestParam(value = "fromDate", required = true) final long fromDate,
			@RequestParam(value = "toDate", required = true) final long toDate,
			@RequestParam(value = "partnerName", required = false) String partnerName,
			@RequestParam("page") int page,
			@RequestParam("per_page") int perPage) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PageResponse<BookingHistoryDTO> serviceHistory = creditBookingService.getBookingsHistory(uuid, fromDate, toDate,
					partnerName, page, perPage);
			apiResponse.setData(serviceHistory);
		} catch (Exception e) {
			LOG.error("[getBookingsHistory] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	/**
	 * @apiNote: export booking history
	 * @author long
	 * @email longpham@leep.app
	 * @param uuid, fromDate, toDate, partnerName
	 * @return csv file
	 */
	@GetMapping(value = "/admin/v1/wallets/credit/bookings/client/{uuid}/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody exportBookingHistory(@PathVariable(value = "uuid", required = true) final String uuid,
			@RequestParam(value = "fromDate", required = true) final long fromDate,
			@RequestParam(value = "toDate", required = true) final long toDate,
			@RequestParam(value = "partnerName", required = false) String partnerName,
			final HttpServletResponse response) {
		final String filename = "booking_history.csv";
		try {
			PageResponse<BookingHistoryDTO> serviceHistory = creditBookingService.getBookingsHistory(uuid, fromDate, toDate,
					partnerName, 0, Integer.MAX_VALUE);
			List<BookingHistoryDTO> listBookingHistory = serviceHistory.getEntities();
			return FileExportUtils.exportCSV(listBookingHistory, BOOKING_HISTORY_EXPORT_HEADERS, filename, response);
		} catch (Exception e) {
			LOG.error("[exportBookingHistory] failed: {}", e.getMessage());
			return null;
		}
	}
	
	/**
	 * @apiNote get list of trainer's clients
	 * @author long
	 * @email longpham@leep.app
	 * @param uuid, filter, pageable
	 * @return ResponseEntity<Object>
	 */
	@GetMapping(value = "/admin/v1/wallets/credit/booking/trainer/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getPTClients(@PathVariable(value = "uuid", required = true) final String uuid,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam("page") int page,
			@RequestParam("per_page") int perPage) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PageResponse<PTClientsDTO> ptClients = creditSessionBookingService.getPTClients(uuid, filter, page, perPage);
			apiResponse.setData(ptClients);
		} catch (Exception e) {
			LOG.error("[getPTClients] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
