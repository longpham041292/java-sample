package asia.cmg.f8.session.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.ChangeBookingSessionStatusDto;
import asia.cmg.f8.session.dto.CreditBookingSessionDTO;
import asia.cmg.f8.session.dto.MatchedTrainersDTO;
import asia.cmg.f8.session.dto.RecentPartnerDTO;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.exception.OverlappedTimeBookingException;
import asia.cmg.f8.session.exception.WalletNotEnoughException;
import asia.cmg.f8.session.internal.service.CreditSessionBookingService;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.wrapper.dto.CreditSessionBookingRequestDTO;
import asia.cmg.f8.session.wrapper.dto.CreditSessionBookingResponseDTO;

@RestController
public class CreditSessionBookingApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreditSessionBookingApi.class);

	@Autowired
	private CreditSessionBookingService bookingService;

	@GetMapping(value = "/mobile/v1/credit/booking/sessions/history/user/{partner_uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getSessionsHistory(@PathVariable(name = "partner_uuid") String partnerUuid,
			@PageableDefault(page = 0, size = 10) Pageable pageable, final Account account) {

		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		try {
			String clientUuid = "";
			String trainerUuid = "";
			if (account.isEu()) {
				clientUuid = account.uuid();
				trainerUuid = partnerUuid;
			} else {
				clientUuid = partnerUuid;
				trainerUuid = account.uuid();
			}
			List<CreditBookingSessionStatus> statusList = Arrays.asList(CreditBookingSessionStatus.BURNED,
					CreditBookingSessionStatus.COMPLETED);
			PagedResponse<CreditBookingSessionDTO> result = bookingService.getSessionsHistory(clientUuid, trainerUuid,
					statusList, pageable);
			apiResp.setData(result);
		} catch (Exception e) {
			apiResp.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}

	@PostMapping(value = "/mobile/v1/wallets/credit/booking/sessions/check-in", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> checkinSession(@RequestBody Map<String, Object> request, final Account account) {
		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);

		try {
			if (!UserType.EU.name().equalsIgnoreCase(account.type())) {
				apiResp.setStatus(ErrorCode.FORBIDDEN);
			} else {
				String trainerUuid = (String) request.get("trainer_uuid");
				Integer bookingId = (Integer) request.get("booking_id");
				if (StringUtils.isEmpty(trainerUuid) || bookingId == null) {
					apiResp.setStatus(ErrorCode.REQUEST_INVALID);
				} else {
					CreditSessionBookingResponseDTO bookingResponse = bookingService.handleCheckInSession(bookingId,
							trainerUuid, ClientActions.CHECKIN, account);
					apiResp.setData(bookingResponse);
				}
			}
		} catch (BookingValidationException e) {
			apiResp.setStatus(
					new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResp.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}

	@PostMapping(value = "/mobile/v1/credit/booking/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> bookingSessions(@RequestBody @Valid CreditSessionBookingRequestDTO request,
			@RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String lang,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditSessionBookingResponseDTO> bookingSessions = bookingService.handleBookingSessions(request, lang,
					account);
			apiResponse.setData(bookingSessions);
		} catch (OverlappedTimeBookingException e) {
			apiResponse.setData(e.getData());
			apiResponse.setStatus(
					new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());
			apiResponse.setData(data);
			apiResponse.setStatus(
					new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(
					new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/mobile/v1/credit/booking/sessions/{session_id}/action/{action}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> changeBookingSessionStatus(@PathVariable(name = "session_id") final long bookingId,
			@PathVariable(name = "action") final ClientActions action, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			ChangeBookingSessionStatusDto result = bookingService.handleUpdateSessionStatus(bookingId, action, account);
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

	@GetMapping(value = "/mobile/v1/credit/booking/sessions/recent-partners", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getRecentBookedPartner(@PageableDefault(page = 0, size = 10) Pageable pageable,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditBookingSessionStatus> status = Arrays.asList(CreditBookingSessionStatus.COMPLETED,
					CreditBookingSessionStatus.BURNED);
			PagedResponse<RecentPartnerDTO> recentPartners = bookingService.getRecentBookedPartners(account, status,
					pageable);
			apiResponse.setData(recentPartners);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v1/credit/booking/sessions/matched-trainers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getSuggestedMatchedTrainers(@PageableDefault(page = 0, size = 10) Pageable pageable,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<MatchedTrainersDTO> recentPartners = bookingService
					.getSuggestedMatchedTrainers(account.uuid(), pageable);
			apiResponse.setData(recentPartners);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/internal/v1/credit/booking/sessions/auto-burned", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> autoBurnedForConfirmedSessionAfterEndtime() {
		Boolean result = true;

		try {
			bookingService.handleAutoBurnedSessions();
		} catch (Exception e) {
			result = false;
		}

		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

	@PostMapping(value = "/internal/v1/credit/booking/sessions/auto-deducted", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> autoDeductedForBurnedSession() {
		Boolean result = true;

		try {
			bookingService.handleAutoDeductedForBurnedSessions();
		} catch (Exception e) {
			result = false;
		}

		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/internal/v1/credit/booking/sessions/user/{euUuid}/recent-trainers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTopRecentBookedTrainers(@PathVariable("euUuid") String euUuid,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		List<RecentPartnerDTO> result = new ArrayList<RecentPartnerDTO>();
		try {
			List<CreditBookingSessionStatus> status = Arrays.asList(CreditBookingSessionStatus.BURNED,
					CreditBookingSessionStatus.COMPLETED);
			PagedResponse<RecentPartnerDTO> recentTrainers = bookingService.getRecentBookedTrainers(euUuid, status,
					pageable);
			result = recentTrainers.getEntities();
		} catch (Exception e) {
		}

		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
}
