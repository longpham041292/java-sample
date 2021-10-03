package asia.cmg.f8.session.api;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredCmsRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.*;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.exception.OverlappedTimeBookingException;
import asia.cmg.f8.session.exception.WalletNotEnoughException;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.service.CreditBookingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CreditBookingApi {

	@Autowired
	private CreditBookingService creditBookingService;

	@Autowired
	private BasicUserRepository userRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(CreditBookingApi.class);

	private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";

	/**
	 * Check in course flow (Ticket LEEP-3025)
	 * @author Ha Vu
	 * @param
	 * 	- course_id: long value
	 * @return CreditClassBookingResponse object
	 */
	@PutMapping(value = "/mobile/v1/wallets/credit/courses/{course_id}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkinCourse(@PathVariable("course_id") final long courseId, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.checkinCourseService(courseId, account);

			BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
			CreditClassBookingResponse bookingResponse = new CreditClassBookingResponse(bookingEntity, clientEntity);

			apiResponse.setData(bookingResponse);
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinCourse] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

    /**
     * Check in class booking flow (Ticket LEEP-2758)
     * @author Ha Vu
     * @param
     * 	- class_id: long value
     * @return CreditClassBookingResponse object
     */
    @PutMapping(value = "/mobile/v1/wallets/credit/class-booking/{class_id}/studio/{studio_uuid}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkinClass(@PathVariable("class_id") final long classId, @PathVariable("studio_uuid") final String studioUuid,  final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.checkinClassService(classId, studioUuid, account);

			BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
			CreditClassBookingResponse bookingResponse = new CreditClassBookingResponse(bookingEntity, clientEntity);

			apiResponse.setData(bookingResponse);
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinClass] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    /**
     * In case: Client have not booked yet before, client want to book and check-in directly
     * @param request
     * @param account
     * @return
     */
    @PutMapping(value = "/mobile/v1/wallets/credit/class-booking/checkin-directly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkinClassDirectly(@RequestBody @Valid CreditClassBookingDirectlyRequest request,  final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			boolean canceled = true;
			if(request.getOverlappedBookingId() != 0) {
				canceled = creditBookingService.handleCancelOverlappedBooking(request.getOverlappedBookingId(), account);
			}
			if(canceled == true) {
				CreditBookingEntity bookingEntity = creditBookingService.checkinDirectlyClassService(request, account);
				BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
				CreditClassBookingResponse bookingResponse = new CreditClassBookingResponse(bookingEntity, clientEntity);
				apiResponse.setData(bookingResponse);
			} else {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Cancel overlapped booking failed"));
			}
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());
			apiResponse.setData(data);
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (OverlappedTimeBookingException e) {
			apiResponse.setData(e.getData());
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinDirectlyCLass] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }
    
    @PutMapping(value = "/mobile/v1/wallets/credit/class-booking/category/{category_id}/studio/{studio_uuid}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkinClassCategory(@PathVariable("category_id") final String categoryId, @PathVariable("studio_uuid") final String studioUuid,  final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.checkinClassCategoryService(categoryId, studioUuid, account);

			BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
			CreditClassBookingResponse bookingResponse = new CreditClassBookingResponse(bookingEntity, clientEntity);

			apiResponse.setData(bookingResponse);
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinClass] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    /**
     * Check in eticket booking flow (Ticket LEEP-2655)
     * @author Ha Vu
     * @param
     *  - studio_uuid: string value
     * @return CreditETicketBookingResponse object
     */
    @PutMapping(value = "/mobile/v1/wallets/credit/eticket-booking/{studio_uuid}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkinEticket(@PathVariable("studio_uuid") final String studioUuid, final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.checkinEticketService(studioUuid, account);

			BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
			CreditETicketBookingResponse bookingResponse = new CreditETicketBookingResponse(bookingEntity, clientEntity);

			apiResponse.setData(bookingResponse);
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
			apiResponse.setData(e.getData());
		} catch (Exception e) {
			LOGGER.error("[checkinEticket] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    /**
     * In case: Client have not booked yet before, client want to book and check-in directly
     * @param request
     * @param account
     * @return
     */
    @PutMapping(value = "/mobile/v1/wallets/credit/eticket-booking/checkin-directly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkinEticketDirectly(@RequestBody @Valid CreditETicketBookingDirectlyRequest request, final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.checkinDirectlyEticketService(request, account);

			BasicUserEntity clientEntity = userRepo.findOneByUuid(account.uuid()).get();
			CreditETicketBookingResponse bookingResponse = new CreditETicketBookingResponse(bookingEntity, clientEntity);

			apiResponse.setData(bookingResponse);
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());
			apiResponse.setData(data);
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (OverlappedTimeBookingException e) {
			apiResponse.setData(e.getData());
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinDirectlyEticket] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    /**
     * Upgrade eticket from off-pick to all day
     * @author Ha Vu
     * @param
     *  - studio_uuid: string value
     * @return CreditETicketBookingResponse object
     */
    @PutMapping(value = "/mobile/v1/wallets/credit/eticket-booking/{studio_uuid}/upgrade-eticket", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upgradeAlldayEticket(@PathVariable("studio_uuid") final String studioUuid, final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			CreditBookingEntity bookingEntity = creditBookingService.upgradeEticketService(studioUuid, account);
			CreditETicketBookingResponse bookingResponse = new CreditETicketBookingResponse(bookingEntity);
			apiResponse.setData(bookingResponse);
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());

			apiResponse.setData(data);
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[checkinDirectlyEticket] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

	@GetMapping(value = "/mobile/v1/wallets/credit/booking/calendar/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getBookingSchedule(@RequestParam(value = START_TIME) final Long startTime,
													@RequestParam(value = END_TIME) final Long endTime,
													final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditBookingResponse<?>> scheduledBookings = creditBookingService.getScheduledBookings(account, startTime, endTime);
			apiResponse.setData(scheduledBookings);
		} catch (Exception e) {
			LOGGER.error("[getBookingSchedule] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/mobile/v1/wallets/credit/booking/classes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> bookingClass(@RequestBody CreditClassBookingRequest request, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			boolean canceled = true;
			if(request.getOverlappedBookingId() != 0) {
				canceled = creditBookingService.handleCancelOverlappedBooking(request.getOverlappedBookingId(), account);
			}
			if(canceled == true) {
				CreditBookingEntity bookingEntity = creditBookingService.bookingClassService(request, account);
				CreditClassBookingResponse bookingResponse = new CreditClassBookingResponse(bookingEntity);
				apiResponse.setData(bookingResponse);
			} else {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Cancel overlapped booking failed"));
			}
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());

			apiResponse.setData(data);
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (OverlappedTimeBookingException e) {
			apiResponse.setData(e.getData());
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[bookingClass] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/mobile/v1/wallets/credit/booking/etickets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> bookEticket(@RequestBody @Valid CreditETicketBookingRequest request, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			boolean canceled = true;
			if(request.getOverlappedBookingId() != 0) {
				canceled = creditBookingService.handleCancelOverlappedBooking(request.getOverlappedBookingId(), account);
			}
			if(canceled == true) {
				CreditBookingEntity bookingEntity = creditBookingService.bookingEticketService(request, account);
				CreditETicketBookingResponse bookingResponse = new CreditETicketBookingResponse(bookingEntity);
				apiResponse.setData(bookingResponse);
			} else {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Cancel overlapped booking failed"));
			}
		} catch (WalletNotEnoughException e) {
			Map<String, Object> data = new HashMap<>();
			data.put("lack_of_credit", e.amount());
			data.put("lack_of_money", e.money());

			apiResponse.setData(data);
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (OverlappedTimeBookingException e) {
			apiResponse.setData(e.getData());
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			LOGGER.error("[bookEticket] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}


	@RequiredCmsRole
	@GetMapping(value = "/cms/v1/wallets/credit/booking", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> listBookings(@RequestParam(name = "studio_uuids") List<String> studioUuids, @RequestParam("page") int page,
			@RequestParam("per_page") int perPage, @RequestParam("order_by") String orderCriteria,
			@RequestParam("start_time_start") long startTimeStart, @RequestParam("start_time_end") long startTimeEnd,
			@RequestParam("statuses") List<Integer> statuses, final Account account) {
		ApiRespObject<PageResponse<CMSCreditBookingResponse>> apiResponse = new ApiRespObject<>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			Page<CreditBookingEntity> entitiesPage =
					creditBookingService.findByStudioUuidsAndStatusAndStartTimeBetween(studioUuids, statuses, startTimeStart, startTimeEnd, orderCriteria, page, perPage);
			List<CMSCreditBookingResponse> entities =
					entitiesPage.getContent().stream().map(entity -> new CMSCreditBookingResponse(entity)).collect(Collectors.toList());
			PageResponse<CMSCreditBookingResponse> pageResponse = new PageResponse<>();
			pageResponse.setEntities(entities);
			pageResponse.setCount(entitiesPage.getTotalElements());

			apiResponse.setData(pageResponse);
		} catch (Exception e) {
			LOGGER.error("CMS => listBookings", e);
			// TODO: handle exception
		}

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/mobile/v1/wallets/credit/booking/classes/{booking_id}/action/{action}")
	public ResponseEntity<Object> cancelBookedClass(@PathVariable(name = "booking_id") final long bookingId,
													@PathVariable(name = "action") final ClientActions action,
													final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			if(ClientActions.CANCEL == action) {
				CreditBookingEntity bookingEntity = creditBookingService.handelCancelClassBooking(bookingId, account.uuid());
				CreditClassBookingResponse response = new CreditClassBookingResponse(bookingEntity);
				apiResponse.setData(response);
			} else {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not support action " + action.name()));
			}
		} catch (BookingValidationException e) {
			apiResponse.setStatus(new ErrorCode(e.errorCode().getCode(), e.errorCode().getError(), e.errorCode().getDetail()));
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/mobile/v1/wallets/credit/booking/etickets/{booking_id}/action/{action}")
	public ResponseEntity<Object> cancelBookedEticket(@PathVariable(name = "booking_id") final long bookingId,
													@PathVariable(name = "action") final ClientActions action,
													final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			if(ClientActions.CANCEL == action) {
				CreditBookingEntity bookingEntity = creditBookingService.handelCancelEticketBooking(bookingId, account.uuid());
				CreditETicketBookingResponse response = new CreditETicketBookingResponse(bookingEntity);
				apiResponse.setData(response);
			} else {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not support action " + action.name()));
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/internal/v1/credit/booking/classes/auto-burned", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> autoBurningClassBookingsJob() {
		boolean result = true;
		LOGGER.info("Start auto burning class booking from job schedule");
		try {
			creditBookingService.handleAutoBurningClassBookingsJob();
		} catch (Exception e) {
			LOGGER.error("[autoBurningClassBookingsJob] error detail: {}", e.getMessage());
			result = false;
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping(value = "/internal/v1/credit/booking/etickets/auto-burned", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> autoBurningEticketBookingsJob() {
		boolean result = true;
		LOGGER.info("Start auto burning eticket booking from job schedule");
		try {
			creditBookingService.handleAutoBurningEticketBookingsJob();
		} catch (Exception e) {
			LOGGER.error("[autoBurningEticketBookingsJob] error detail: {}", e.getMessage());
			result = false;
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequiredCmsRole
	@GetMapping(value = "/cms/v1/wallets/credit/booking/statistic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiRespObject<List<Object[]>> listBookings(@RequestParam(name = "studio_uuid") String studioUuid,
			@RequestParam("start_time_start") long startTimeStart, @RequestParam("start_time_end") long startTimeEnd) {
		ApiRespObject<List<Object[]>> apiResponse = new ApiRespObject<>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			List<Object[]> result = creditBookingService.countByStudioUuidAndGroupByStatus(studioUuid, startTimeStart, startTimeEnd);
			apiResponse.setData(result);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return apiResponse;
	}

	/**
	 * @apiNote auto cancel session booking which start time <= now
	 * @author Long
	 * @email longpham@leep.app
	 * @return void
	 */
	@PostMapping(value = "/internal/v1/credit/booking/session/auto-cancel-booking", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> autoCancelSessionBooking() {
		boolean result = true;
		LOGGER.info("Start auto cancel session booking");
		try {
			creditBookingService.handleAutoCancelSessionBookingJob();
		} catch (Exception e) {
			LOGGER.error("[autoCancelSessionBookingg] error detail: {}", e.getMessage());
			result = false;
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
