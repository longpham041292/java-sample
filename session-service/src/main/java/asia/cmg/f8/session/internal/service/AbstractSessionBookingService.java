package asia.cmg.f8.session.internal.service;

import static java.lang.Boolean.TRUE;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import asia.cmg.f8.common.event.session.SessionBook;
import asia.cmg.f8.common.event.session.SessionBookCompleteEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.dto.TimeSlot;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionHistoryEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.exception.BookingConstraintViolationException;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.rule.booking.BookingInput;
import asia.cmg.f8.session.rule.booking.ValidationResult;
import asia.cmg.f8.session.rule.booking.ValidationType;
import asia.cmg.f8.session.service.EventManagementService;
import asia.cmg.f8.session.service.EventService;
import asia.cmg.f8.session.service.OrderManagementService;
import asia.cmg.f8.session.service.SessionBookingService;
import asia.cmg.f8.session.service.SessionHistoryManagementService;
import asia.cmg.f8.session.service.SessionManagementService;
import asia.cmg.f8.session.service.SessionService;
import asia.cmg.f8.session.service.ValidationService;
import asia.cmg.f8.session.utils.TimeSlotUtil;
import asia.cmg.f8.session.wrapper.dto.AvailableSessionWithOrder;

/**
 * Created on 2/16/17.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public abstract class AbstractSessionBookingService implements SessionBookingService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionBookingService.class);
	private final ClubEntity NO_CHECKIN_CLUB = null;

	private final SessionManagementService sessionManagementService;
	private final SessionHistoryManagementService sessionHistoryManagementService;
	private final SessionService sessionService;
	private final EventManagementService eventManagementService;
	private final ValidationService validationService;
	private final EventHandler eventHandler;
	private final OrderManagementService orderManagementService;
	private final EventService eventService;

	protected AbstractSessionBookingService(final SessionManagementService sessionManagementService,
			final SessionHistoryManagementService sessionHistoryManagementService, final SessionService sessionService,
			final EventManagementService eventManagementService, final ValidationService validationService,
			final EventHandler eventHandler, final OrderManagementService orderManagementService,
			final EventService eventService) {
		this.sessionManagementService = sessionManagementService;
		this.sessionHistoryManagementService = sessionHistoryManagementService;
		this.sessionService = sessionService;
		this.eventManagementService = eventManagementService;
		this.validationService = validationService;
		this.eventHandler = eventHandler;
		this.orderManagementService = orderManagementService;
		this.eventService = eventService;
	}

	@Override
	@Transactional
	public BookingResponse doBooking(final Set<TimeSlot> timeSlotList, final String userId, final String trainerId, final String packageId,
									final Account account, final ClubDto club) {

		final Set<ReservationSlot> reservationSlots = parseTimeSlots(timeSlotList);

		// validation time slots.
		final ValidationResult result = doValidation(reservationSlots, userId, trainerId, packageId, account);
		if (result.hasError()) {
			final Optional<Object> data = result.getValidationData();
			if (data.isPresent()) {
				return (BookingResponse) data.get();
			} else {
				// otherwise we throw exception
				throw new BookingConstraintViolationException(result.getType());
			}
		}

		final List<SessionEntity> bookedSessions = handleBooking(reservationSlots, userId, trainerId, packageId, account, club);

		// fire event.
		eventHandler.publish(SessionBookCompleteEvent.newBuilder().setEventId(UUID.randomUUID().toString())
				.setSessions(bookedSessions.stream().map(sessionEntity -> {
					final SessionBook sessionBook = new SessionBook();
					sessionBook.setPackageUuid(sessionEntity.getPackageUuid());
					sessionBook.setSessionDate(ZoneDateTimeUtils.convertToSecondUTC(sessionEntity.getStartTime()));
					sessionBook.setSessionUuid(sessionEntity.getUuid());
					sessionBook.setNewSessionStatus(sessionEntity.getStatus().toString());
					return sessionBook;
				}).collect(Collectors.toList()))
				.setSubmittedAt(System.currentTimeMillis())
				.setUserId(userId)
				.setBookedBy(account.type())
				.setPtUuid(trainerId)
				.setOwnerUuid(account.uuid())
				.build());

		return BookingResponse.builder().withResult(true).build();
	}

	@Transactional
	public BookingResponse doBookingByAdmin(final Set<TimeSlot> timeSlotList, final String userId,
			final String trainerId, final ClientActions actions, final Account account, ClubDto club) {

		final Set<ReservationSlot> reservationSlots = parseTimeSlots(timeSlotList);

		final boolean havePastSlot = reservationSlots.stream()
				.anyMatch(slot -> ChronoUnit.MINUTES.between(LocalDateTime.now(), slot.getStartTime()) <= 0);
		if (havePastSlot && actions == ClientActions.ACCEPT) {
			throw new BookingConstraintViolationException(ValidationType.NOT_VALID_TIME_SLOT);
		}
		if (!havePastSlot && (actions == ClientActions.NOSHOW || actions == ClientActions.CHECKIN)) {
			throw new BookingConstraintViolationException(ValidationType.NOT_VALID_TIME_SLOT);
		}

		// validation time slots.
		final ValidationResult result = doValidation(reservationSlots, userId, trainerId, null, account);
		if (result.hasError()) {
			final Optional<Object> data = result.getValidationData();
			if (data.isPresent()) {
				return (BookingResponse) data.get();
			} else {
				// otherwise we throw exception
				throw new BookingConstraintViolationException(result.getType());
			}
		}

		final List<SessionEntity> bookedSessions = handleBooking(reservationSlots, userId, trainerId, null, account, club);
		bookedSessions.forEach(session -> {
			sessionService.updateSessionStatus(session.getUuid(), actions, account, true, NO_CHECKIN_CLUB);
		});

		// fire event.
		eventHandler.publish(SessionBookCompleteEvent.newBuilder().setEventId(UUID.randomUUID().toString())
				.setSessions(bookedSessions.stream().map(sessionEntity -> {
					final SessionBook sessionBook = new SessionBook();
					sessionBook.setPackageUuid(sessionEntity.getPackageUuid());
					sessionBook.setSessionDate(ZoneDateTimeUtils.convertToSecondUTC(sessionEntity.getStartTime()));
					sessionBook.setSessionUuid(sessionEntity.getUuid());
					sessionBook.setNewSessionStatus(sessionEntity.getStatus().toString());
					return sessionBook;
				}).collect(Collectors.toList())).setSubmittedAt(System.currentTimeMillis()).setUserId(userId)
				.setBookedBy(CommonConstant.ADMIN_USER_TYPE).setPtUuid(trainerId).setOwnerUuid(account.uuid()).build());

		return BookingResponse.builder().withResult(true).build();
	}

	@Transactional
	public BookingResponse updateBookingByAdmin(final TimeSlot timeSlot, final Account account) {

		SessionEntity sessionEntity = sessionService.findOne(timeSlot.getSessionId());

		if (!sessionEntity.getBookedBy().equals(CommonConstant.ADMIN_USER_TYPE)) {
			throw new BookingConstraintViolationException(ValidationType.UNSUPPORTED);
		}

		OrderEntity orderEntity = orderManagementService.findOneBySessionPackageUuid(sessionEntity.getPackageUuid())
				.get();
		LocalDateTime startTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeSlot.getStartTime());
		LocalDateTime endTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeSlot.getEndTime());

		if (startTime.isAfter(orderEntity.getExpiredDate())) {
			throw new BookingConstraintViolationException(ValidationType.NOT_VALID_TIME_SLOT);
		}

		sessionEntity.setStartTime(startTime);
		sessionEntity.setEndTime(endTime);
		sessionService.updateSession(sessionEntity);
		eventService.updateSessionEventTime(sessionEntity.getUserEventId(), sessionEntity.getStartTime(),
				sessionEntity.getEndTime());
		eventService.updateSessionEventTime(sessionEntity.getPtEventId(), sessionEntity.getStartTime(),
				sessionEntity.getEndTime());
		// fire event.
		eventHandler.publish(SessionBookCompleteEvent.newBuilder().setEventId(UUID.randomUUID().toString())
				.setSessions(Arrays.asList(sessionEntity).stream().map(session -> {
					final SessionBook sessionBook = new SessionBook();
					sessionBook.setPackageUuid(session.getPackageUuid());
					sessionBook.setSessionDate(ZoneDateTimeUtils.convertToSecondUTC(session.getStartTime()));
					sessionBook.setSessionUuid(session.getUuid());
					sessionBook.setNewSessionStatus(session.getStatus().toString());
					return sessionBook;
				}).collect(Collectors.toList())).setSubmittedAt(System.currentTimeMillis())
				.setUserId(sessionEntity.getUserUuid()).setBookedBy(CommonConstant.ADMIN_USER_TYPE)
				.setPtUuid(sessionEntity.getPtUuid()).build());

		return BookingResponse.builder().withResult(true).build();
	}

	protected List<SessionEntity> handleBooking(final Set<ReservationSlot> reservationSlots, final String userId,
			final String trainerId, final String packageId, final Account account, ClubDto club) {
		
		final Set<ReservationSlot> finalReservationList = handleBookingSpecifiedSessions(reservationSlots, account);
		
		final long numberOfBookingSession = reservationSlots.stream().filter(slot -> Objects.isNull(slot.getConfirmed())
				|| (Objects.isNull(slot.getSessionId()) && !Objects.isNull(slot.getConfirmed()) && slot.getConfirmed()))
				.count();

		LOGGER.info("Number of remanding sessions = {} for booking.", numberOfBookingSession);

		if (numberOfBookingSession == 0) {
			return Collections.emptyList();
		}

		// Get all available sessions with OPEN status.
		List<AvailableSessionWithOrder> availableSessions = new ArrayList<AvailableSessionWithOrder>();
        
        if(StringUtils.isEmpty(packageId)){
        	availableSessions = getSessionManagementService()
                    .getAvailableSessions(userId, trainerId);        	
        }
        else {
        	availableSessions = getSessionManagementService()
                    .getAvailableSessionsByPackageId(userId, packageId);
        }

		if (availableSessions.isEmpty() || availableSessions.size() < numberOfBookingSession) {
			throw new BookingConstraintViolationException(ValidationType.NOT_ENOUGH_AVAILABLE_SESSION);
		}

		final List<SessionEntity> affectedSessions = new ArrayList<>();

		final List<ReservationSlot> remainingReservationList = finalReservationList.stream()
				.sorted((slot1, slot2) -> slot1.getStartTime().compareTo(slot2.getStartTime()))
				.collect(Collectors.toList());

		for (final ReservationSlot slot : remainingReservationList) {
			for (final AvailableSessionWithOrder availableSession : availableSessions) {
				if (isTimeFitSession(slot.getStartTime().atZone(ZoneOffset.systemDefault()).toEpochSecond(),
						availableSession)) {
					final SessionEntity session = sessionService.findOne(availableSession.getSessionUuid());
					if (session == null) {
						throw new IllegalArgumentException(
								String.format("Session id not found %s", availableSession.getSessionUuid()));
					}
					handleBookingSession(userId, trainerId, account, affectedSessions, slot, session, club);
					availableSession.setReserved(true);
					break;
				}
			}
		}

		return affectedSessions;
	}

	private boolean isTimeFitSession(final long timeInSec, final AvailableSessionWithOrder availableSession) {
		if (!availableSession.isReserved()) {
			return availableSession.getExpireDays() == null
					|| timeInSec * 1000 < availableSession.getExpireDays().getTime();
		}
		return false;
	}

	protected Set<ReservationSlot> parseTimeSlots(final Set<TimeSlot> timeSlotList) {
		if (Objects.isNull(timeSlotList) || timeSlotList.isEmpty()) {
			throw new IllegalArgumentException("Date slots is empty");
		}
		return TimeSlotUtil.parseTimeSlot(timeSlotList);
	}

	protected ValidationResult doValidation(final Iterable<? extends ReservationSlot> reservationList,
			final String userId, final String trainerId, final String packageUuid, final Account account) {

		final BookingInput bookingInput = BookingInput.builder().withReservationSlotList(reservationList)
				.withUserId(userId).withTrainerId(trainerId).withPackageUuid(packageUuid).withAccount(account)
				.withValidationService(validationService).build();
		return validationService.bookingValidation(bookingInput);
	}

	protected void handleBookingSession(final String userId, final String trainerId, final Account account,
			final List<SessionEntity> affectedSessions, final ReservationSlot slot, final SessionEntity session, final ClubDto club) {

		final SessionStatus oldStatus = session.getStatus();
		final SessionStatus newStatus = session.getStatus().book(session, account);

		String bookedBy = CommonConstant.ADMIN_USER_TYPE;
		if (!account.isAdmin()) {
			bookedBy = account.type();
		}

		LOGGER.info(
				"Handling booking of session \"{}\" from old status {} to new status {} by lLogging user {} and userType {}",
				session.getUuid(), oldStatus, newStatus, account.uuid(), bookedBy);

		// Create event for end user and trainer
		final EventEntity userEvent = eventManagementService.create(userId, slot.getStartTime(), slot.getEndTime(),
				session.getUuid(), newStatus, bookedBy, club);

		final EventEntity trainerEvent = eventManagementService.create(trainerId, slot.getStartTime(),
				slot.getEndTime(), session.getUuid(), newStatus, bookedBy, club);

		session.setStartTime(slot.getStartTime());
		session.setEndTime(slot.getEndTime());
		session.setLastStatus(oldStatus);
		session.setLastStatusModifiedDate(session.getStatusModifiedDate());
		session.setStatus(newStatus);
		session.setStatusModifiedDate(LocalDateTime.now());
		session.setUserEventId(userEvent.getUuid());
		session.setPtEventId(trainerEvent.getUuid());
		session.setPtUuid(trainerId);
		session.setBookedBy(bookedBy);
		session.setBookingClubUuid(club.getUuid());
		session.setBookingClubName(club.getName());
		session.setBookingClubAddress(club.getAddress());

		affectedSessions.add(session);

		final SessionHistoryEntity sessionHistoryEntity = createSessionHistory(account, session, oldStatus, newStatus);

		sessionManagementService.save(session);
		sessionHistoryManagementService.save(sessionHistoryEntity);
	}

	private SessionHistoryEntity createSessionHistory(final Account account, final SessionEntity session,
			final SessionStatus oldStatus, final SessionStatus newStatus) {
		final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
		sessionHistoryEntity.setSessionUuid(session.getUuid());
		sessionHistoryEntity.setUserUuid(session.getUserUuid());
		sessionHistoryEntity.setNewPackageUuid(session.getPackageUuid());
		sessionHistoryEntity.setOldPackageUuid(session.getPackageUuid());
		sessionHistoryEntity.setOldPtUuid(session.getPtUuid());
		sessionHistoryEntity.setNewPtUuid(session.getPtUuid());
		sessionHistoryEntity.setOldStatus(oldStatus);
		sessionHistoryEntity.setNewStatus(newStatus);
		sessionHistoryEntity
				.setAction(SessionAction.mapSessionActionByClientAction(session, ClientActions.RESERVE, account));
		return sessionHistoryEntity;
	}

	protected Set<ReservationSlot> handleBookingSpecifiedSessions(final Set<ReservationSlot> reservationList,
			final Account account) {

		final boolean isConfirmedRequest = isConfirmedRequest(reservationList);

		LOGGER.info("Start handleBookingSpecifiedSessions with isConfirmedRequest = {}", isConfirmedRequest);

		if (isConfirmedRequest) {

			// Update session status after review double booking list.
			final List<ReservationSlot> eligibleSlots = reservationList.stream()
					.filter(slot -> slot.getSessionId() != null).collect(Collectors.toList());
			for (final ReservationSlot reservationSlot : eligibleSlots) {

				ClientActions clientActions = ClientActions.DECLINE;
				final Boolean confirmed = reservationSlot.getConfirmed();
				if (TRUE.equals(confirmed)) {
					clientActions = ClientActions.ACCEPT;
				}

				final String sessionId = reservationSlot.getSessionId();

				LOGGER.info("Updating status of session {}, client action {} , is admin {}", sessionId, clientActions,
						false);

				sessionService.updateSessionStatus(sessionId, clientActions, account, false, NO_CHECKIN_CLUB);
			}

			// Return a list of new booking
			return reservationList.stream().filter(slot -> Objects.isNull(slot.getSessionId())
					&& !Objects.isNull(slot.getConfirmed()) && slot.getConfirmed()).collect(Collectors.toSet());
		}
		return reservationList;
	}

	/**
	 * A convenience method to check whether the reservation list is confirm
	 * request of PT.
	 *
	 * @param reservationList
	 *            the reservation list
	 * @return true if reservation list is confirm request.
	 */
	protected boolean isConfirmedRequest(final Set<ReservationSlot> reservationList) {
		return reservationList.stream().findFirst().get().getConfirmed() != null;
	}

	public SessionManagementService getSessionManagementService() {
		return sessionManagementService;
	}
}
