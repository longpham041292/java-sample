package asia.cmg.f8.report.api;

import java.time.Instant;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.report.dto.BookingServiceDTO;
import asia.cmg.f8.report.exception.BookingValidationException;
import asia.cmg.f8.report.service.CreditSessionBookingService;

@RestController
public class CreditSessionBookingAPI {

	private static final String SESSION_BOOKING_MANAGAMENT = "session_booking_management.csv";
	private static final String[] SESSION_BOOKING_MANAGAMENT_HEADER = { "ptUsername", "ptName", "createdTimeFormat",
			"bookingTimeFormat", "clientUuid", "clientName", "status" };
	@Autowired
	private CreditSessionBookingService bookingService;

	@GetMapping(value = "/admin/v1/wallets/credit/booking/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getSessionBooking(@RequestParam(name = "from") Long startTime,
			@RequestParam(name = "to") Long endTime,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "status") List<Integer> status,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		try {
			List<BookingServiceDTO> result = bookingService.sessionBookingManagement(keyword, start, end, status,
					pageable);
			apiResponse.setData(result);
			if (result == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Can not change the status of session!"));
			}
		} catch (BookingValidationException e) {
			apiResponse.setStatus(
					new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/admin/v1/wallets/credit/booking/sessions/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody exportSessionBooking(@RequestParam(name = "from") Long startTime,
			@RequestParam(name = "to") Long endTime,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "status") List<Integer> status,
			@PageableDefault(page = 0, size = Integer.MAX_VALUE) Pageable pageable,
			final HttpServletResponse response) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());

		try {
			List<BookingServiceDTO> result = bookingService.sessionBookingManagement(keyword, start, end, status,
					pageable);
			return FileExportUtils.exportCSV(result, SESSION_BOOKING_MANAGAMENT_HEADER, SESSION_BOOKING_MANAGAMENT,
					response);
		} catch (Exception e) {
			return null;
		}

	}
}
