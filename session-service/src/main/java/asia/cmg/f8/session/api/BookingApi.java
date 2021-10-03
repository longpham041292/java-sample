package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import java.util.Objects;
import javax.inject.Inject;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.BookingRequest;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.CheckinClubRequest;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.ReviewListResponse;
import asia.cmg.f8.session.dto.TimeSlot;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.internal.service.AbstractSessionBookingService;
import asia.cmg.f8.session.internal.service.DefaultSessionBookingService;
import asia.cmg.f8.session.internal.service.InternalEventManagementService;
import asia.cmg.f8.session.internal.service.OnBehalfSessionBookingService;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.service.ClubService;
import asia.cmg.f8.session.service.DoubleBookingService;
import asia.cmg.f8.session.utils.SessionErrorCode;

/**
 * Created on 12/12/16.
 */

@RestController
@RequestMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
public class BookingApi {

	private static final String TRAINER_ID = "trainerId";
	private static final String USER_ID = "userId";
    private static final String STATUS = "status";
    
	private final AbstractSessionBookingService defaultSessionBookingService;
	private final AbstractSessionBookingService onBehalfSessionBookingService;
	private final DoubleBookingService doubleBookingService;
	private final InternalEventManagementService sessionEventService;
	private final ClubService clubService;
	
	private Gson gson = new Gson();
	private static final Logger logger = LoggerFactory.getLogger(BookingApi.class);

	@Inject
	public BookingApi(final DefaultSessionBookingService defaultSessionBookingService,
			final OnBehalfSessionBookingService oNBehalfSessionBookingService,
			final DoubleBookingService doubleBookingService,
			final InternalEventManagementService sessionEventService,
			final ClubService clubService) {
		this.defaultSessionBookingService = defaultSessionBookingService;
		this.onBehalfSessionBookingService = oNBehalfSessionBookingService;
		this.doubleBookingService = doubleBookingService;
		this.sessionEventService = sessionEventService;
		this.clubService = clubService;
	}

	/**
	 * @Desc: PT books sessions for EU
	 * @param bookingRequest
	 * @param userId
	 * @param account
	 * @return
	 */
	@RequiredPTRole
	@RequestMapping(value = "/sessions/users/{userId}/booking/trainers/me", method = RequestMethod.POST)
	public BookingResponse bookSessionsOnBehalf(@RequestBody @Valid final BookingRequest bookingRequest,
			@PathVariable(value = USER_ID) final String userId, final Account account) {
		
		logger.info("Logging BookingRequest data: {}", gson.toJson(bookingRequest));
		
		ClubDto club = new ClubDto.Builder()
				.uuid(bookingRequest.getClubUuid())
				.name(bookingRequest.getClubName())
				.address(bookingRequest.getClubAddress()).build();
		
		return onBehalfSessionBookingService.doBooking(bookingRequest.getListReservation(), userId, account.uuid(), bookingRequest.getPackageId(),
				account, club);
	}
	
	/**
	 * @Desc: PT books sessions for EU and assign for another PT on behalf
	 * @param bookingRequest
	 * @param userId
	 * @param trainerId
	 * @param account
	 * @return
	 */
	@RequiredPTRole
	@RequestMapping(value = "/sessions/users/{userId}/booking/trainers/{trainerId}", method = RequestMethod.POST)
	public BookingResponse bookAndAssignSessionsOnBehalf(@RequestBody @Valid final BookingRequest bookingRequest,
			@PathVariable(value = USER_ID) final String userId, @PathVariable(value = TRAINER_ID) final String trainerId, final Account account) {
		
		logger.info("Logging BookingRequest data: {}", gson.toJson(bookingRequest));
		
		ClubDto club = new ClubDto.Builder()
				.uuid(bookingRequest.getClubUuid())
				.name(bookingRequest.getClubName())
				.address(bookingRequest.getClubAddress()).build();
		
		return onBehalfSessionBookingService.doBooking(bookingRequest.getListReservation(), userId, trainerId , bookingRequest.getPackageId(),
				account, club);
	}

	/**
	 * EU books sessions for himself
	 * @param bookingRequest
	 * @param trainerId
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/sessions/users/me/booking/trainers/{trainerId}", method = RequestMethod.POST)
	public BookingResponse bookSessions(@RequestBody @Valid final BookingRequest bookingRequest,
			@PathVariable(value = TRAINER_ID) final String trainerId, final Account account) {
		
		logger.info("Logging BookingRequest data: {}", gson.toJson(bookingRequest));
		
		ClubDto club = new ClubDto.Builder()
				.uuid(bookingRequest.getClubUuid())
				.name(bookingRequest.getClubName())
				.address(bookingRequest.getClubAddress()).build();
		
		return defaultSessionBookingService.doBooking(bookingRequest.getListReservation(), account.uuid(), trainerId, bookingRequest.getPackageId(),
				account, club);
	}
	
	/**
	 * Admin books sessions on behalf
	 * @param bookingRequest
	 * @param trainerId
	 * @param userId
	 * @param actions
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/admin/sessions/users/{userId}/booking/trainers/{trainerId}/status/{status}", method = RequestMethod.POST)
	@RequiredAdminRole
	public BookingResponse bookSessionsByAdmin(@RequestBody @Valid final BookingRequest bookingRequest,
			@PathVariable(value = TRAINER_ID) final String trainerId, @PathVariable(value = USER_ID) final String userId, @PathVariable(value = STATUS) @Valid final ClientActions actions, final Account account) {
		
		logger.info("Logging BookingRequest data: {}", gson.toJson(bookingRequest));
		
		ClubDto club = new ClubDto.Builder()
				.uuid(bookingRequest.getClubUuid())
				.name(bookingRequest.getClubName())
				.address(bookingRequest.getClubAddress()).build();
		
		return defaultSessionBookingService.doBookingByAdmin(bookingRequest.getListReservation(), userId, trainerId, actions, account, club);
	}
	
	@RequestMapping(value = "/admin/sessions/users/booking/update", method = RequestMethod.PUT)
	@RequiredAdminRole
	public BookingResponse updateBookSessionsByAdmin(@RequestBody @Valid final TimeSlot timeSlot, final Account account) {
		return defaultSessionBookingService.updateBookingByAdmin(timeSlot, account);
	}
	
	@RequiredPTRole
	@RequestMapping(value = "/sessions/confirmed/users/{userId}/trainers/me", method = RequestMethod.POST)
	public ReviewListResponse getConfirmDoubleBooking(@RequestBody final BookingRequest bookingRequest,
			@PathVariable(value = USER_ID) final String userId, final Account account) {
		
		logger.info("Logging BookingRequest data: {}", gson.toJson(bookingRequest));
		
		return doubleBookingService.getReviewSessionList(bookingRequest.getListReservation(), userId, account.uuid(),
				account);
	}
}
