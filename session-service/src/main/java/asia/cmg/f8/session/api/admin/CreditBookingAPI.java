package asia.cmg.f8.session.api.admin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.BookingManagementDTO;
import asia.cmg.f8.session.dto.CreditBookingResponse;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.service.CreditBookingService;

@RestController
public class CreditBookingAPI {
	final String START_TIME = "from";
	final String END_TIME = "to";
	private static final String SESSION_BOOKING_MANAGAMENT = "session_booking_management.csv";
	private static final String ETICKET_BOOKING_MANAGAMENT = "eticket_booking_management.csv";
	private static final String CLASS_BOOKING_MANAGAMENT = "class_booking_management.csv";
	private static final String[] SESSION_BOOKING_MANAGAMENT_HEADER = { "ptUsername", "ptName", "createdTimeFormat", "bookingTimeFormat",
			"clientUuid", "clientName", "status" };
	
	private static final String[] CLASS_BOOKING_MANAGAMENT_HEADER = { "studioUuid", "studioName", "classService", "createdTimeFormat", "bookingTimeFormat",
			"clientUuid", "clientName", "status" };
	private static final String[] ETICKET_BOOKING_MANAGAMENT_HEADER = { "studioUuid", "studioName", "createdTimeFormat", "bookingTimeFormat",
			"clientUuid", "clientName", "status" };
	
	
	@Autowired
	private CreditBookingService creditBookingService;

	/**
	 * @author phong
	 * @param uuid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/wallets/credit/booking/client/{uuid}/calendar/schedules", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<Object> getBookingSchedule(@PathVariable("uuid") final String uuid,
													@RequestParam(value = START_TIME) final Long startTime,
													@RequestParam(value = END_TIME) final Long endTime) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditBookingResponse<?>> scheduledBookings = creditBookingService.getScheduledBookingsByUuuid(uuid, startTime, endTime);
			apiResponse.setData(scheduledBookings);
		} catch (Exception e) {
			apiResponse.setData(e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	/**
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @param keyword
	 * @param status
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/credit/booking/eticket", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> eticketBookingManagement(
						@RequestParam(name = "from") Long startTime,
						@RequestParam(name = "to") Long endTime,
						@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword, 
						@RequestParam(name = "status") List<Integer> status,
						@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		try {
			List<BookingServiceType> type = Arrays.asList(BookingServiceType.ETICKET);
			List<BookingManagementDTO> result = creditBookingService.eticketBookingManagement(keyword ,start ,end , status, type ,pageable);
			apiResponse.setData(result);
			if(result == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Can not change the status of session!"));
			}
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	
	/**
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @param keyword
	 * @param status
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/credit/booking/class", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> classBookingManagement(
						@RequestParam(name = "from") Long startTime,
						@RequestParam(name = "to") Long endTime,
						@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword, 
						@RequestParam(name = "status") List<Integer> status,
						@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		try {
			List<BookingServiceType> type = Arrays.asList(BookingServiceType.CLASS);
			List<BookingManagementDTO> result = creditBookingService.eticketBookingManagement(keyword ,start ,end , status, type ,pageable);
			apiResponse.setData(result);
			if(result == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Can not change the status of session!"));
			}
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/admin/v1/wallets/credit/booking/eticket/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody eticketBookingManagementExport(
						@RequestParam(name = "from") Long startTime,
						@RequestParam(name = "to") Long endTime,
						@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
						@RequestParam(name = "status") List<Integer> status,
						@PageableDefault(page = 0, size = Integer.MAX_VALUE) Pageable pageable,
						final HttpServletResponse response) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		
		try {
			List<BookingServiceType> type = Arrays.asList(BookingServiceType.CLASS);
			List<BookingManagementDTO> result = creditBookingService.eticketBookingManagement(keyword ,start ,end , status, type ,pageable);
			return FileExportUtils.exportCSV(result, ETICKET_BOOKING_MANAGAMENT_HEADER,
					ETICKET_BOOKING_MANAGAMENT, response);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	@GetMapping(value = "/admin/v1/wallets/credit/booking/class/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody classBookingManagementExport(
						@RequestParam(name = "from") Long startTime,
						@RequestParam(name = "to") Long endTime,
						@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
						@RequestParam(name = "status") List<Integer> status,
						@PageableDefault(page = 0, size = Integer.MAX_VALUE) Pageable pageable,
						final HttpServletResponse response) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		
		try {
			List<BookingServiceType> type = Arrays.asList(BookingServiceType.CLASS);
			List<BookingManagementDTO> result = creditBookingService.eticketBookingManagement(keyword ,start ,end , status, type ,pageable);
			return FileExportUtils.exportCSV(result, CLASS_BOOKING_MANAGAMENT_HEADER,
					CLASS_BOOKING_MANAGAMENT, response);
		} catch (Exception e) {
			return null;
		}
		
	}
}
