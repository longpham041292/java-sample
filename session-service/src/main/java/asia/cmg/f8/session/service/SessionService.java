package asia.cmg.f8.session.service;

import static asia.cmg.f8.session.entity.SessionStatus.OPEN;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.ChangeSessionStatusEvent;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.ContractUser;
import asia.cmg.f8.session.dto.CountDown;
import asia.cmg.f8.session.dto.PopupSchedule;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.dto.SessionPackage;
import asia.cmg.f8.session.dto.UserConnectionDto;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.PopupScheduleCase;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionHistoryEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.exception.BookingErrorCode;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.rule.booking.BookingInput;

/**
 * Created on 11/22/16.
 */
@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class SessionService implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);

	private final ClubEntity NO_CHECKIN_CLUB = null;

	private final SessionRepository sessionRepository;
	private final SessionManagementService sessionManagementService;
	private final SessionPackageManagementService sessionPackageManagementService;
	private final SessionHistoryManagementService sessionHistoryManagementService;
	private final EventManagementService eventManagementService;
	private final OrderManagementService orderManagementService;
	private final EventHandler eventHandler;
	private final ValidationService validationService;
	private final SessionProperties sessionProperties;
	private final ContractingService contractingService;
	private final UserService userService;

	@Autowired
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public SessionService(final SessionRepository sessionRepository,
			final SessionManagementService sessionManagementService,
			final SessionPackageManagementService sessionPackageManagementService,
			final SessionHistoryManagementService sessionHistoryManagementService,
			final OrderManagementService orderManagementService, final SessionProperties sessionProperties,
			final EventManagementService eventManagementService, final EventHandler eventHandler,
			final ValidationService validationService, final UserService userService,
			final ContractingService contractingService) {
		this.sessionRepository = sessionRepository;
		this.sessionManagementService = sessionManagementService;
		this.sessionPackageManagementService = sessionPackageManagementService;
		this.sessionHistoryManagementService = sessionHistoryManagementService;
		this.eventManagementService = eventManagementService;
		this.orderManagementService = orderManagementService;
		this.eventHandler = eventHandler;
		this.validationService = validationService;
		this.sessionProperties = sessionProperties;
		this.contractingService = contractingService;
		this.userService = userService;
	}

	@Override
	@SuppressWarnings("PMD")
	public void afterPropertiesSet() throws Exception {
		// initialize message source
		// messageSource = new ResourceBundleMessageSource();
		// messageSource.setBasename("i18n/messages");
		// messageSource.setDefaultEncoding("utf-8");
	}

	public SessionStatus transitSessionState(final SessionEntity sessionEntity, final ClientActions actions,
			final Account account) {
		final SessionStatus currentStatus = sessionEntity.getStatus();
		SessionStatus newStatus = currentStatus;

		switch (actions) {
		case RESERVE:
			newStatus = currentStatus.book(sessionEntity, account);
			break;
		case ACCEPT:
			newStatus = currentStatus.accept(sessionEntity, account);
			break;
		case DECLINE:
			newStatus = currentStatus.decline(sessionEntity, account);
			break;
		case CANCEL:
			newStatus = currentStatus.cancel(sessionEntity, account);
			break;
		case CHECKIN:
			newStatus = currentStatus.checkIn(sessionEntity, account);
			break;
		case NOSHOW:
			newStatus = currentStatus.noShow(sessionEntity, account);
			break;
		default:
			break;
		}
		return newStatus;
	}

	/**
	 * Update session status
	 *
	 * @param sessionId
	 *            the session id
	 * @param clientAction
	 *            the client action
	 * @param account
	 *            the account
	 * @param isAdmin
	 *            is called by admin
	 * @param checkDoubleBook
	 *            whether to check double booking
	 * @return
	 */
	@Transactional
	public BookingResponse updateSessionStatus(final String sessionId, final ClientActions clientAction,
											final Account account, final boolean checkDoubleBook, ClubEntity checkinClub) {
		validateSessionInput(sessionId, clientAction);

		final Optional<SessionEntity> sessionEntityOptional = sessionRepository.findOneByUuid(sessionId);

		if (!sessionEntityOptional.isPresent()) {
			throw new IllegalArgumentException("Session does not exist");
		}

		final SessionEntity session = sessionEntityOptional.get();
		final SessionStatus oldStatus = session.getStatus();

		if (!account.isAdmin() && !isInvolvedInSession(session, account)) {
			throw new BookingValidationException(BookingErrorCode.SESSION_INVALID
					.withDetail("Can not change status because current user is not involved in this session"));
		}

		SessionStatus newStatus = SessionStatus.CANCELLED;
		newStatus = transitSessionState(session, clientAction, account);

		if (newStatus == oldStatus) {
			if (OPEN.equals(newStatus)) {
				eventManagementService.batchDeleteEventsWithUuid(session.getUuid(), SessionStatus.PENDING,
						Arrays.asList(session.getUserEventId(), session.getPtEventId()));
				return BookingResponse.builder().withResult(true).build();
			}
			return BookingResponse.builder().withResult(false).build();
		}

		// Check double booking when PT accept pending session.
		if (checkDoubleBook) {
			final BookingResponse bookingResponse = doubleBookingPTAccept(session, oldStatus, newStatus, account);
			if (!bookingResponse.getResult()) {
				return bookingResponse;
			}
		}

		session.setLastStatus(oldStatus);
		session.setLastStatusModifiedDate(session.getStatusModifiedDate());
		session.setStatus(newStatus);
		session.setStatusModifiedDate(LocalDateTime.now());
		if (account.isAdmin()) {
			session.setBookedBy(CommonConstant.ADMIN_USER_TYPE);
		}
		if(!Objects.isNull(checkinClub)) {
			session.setCheckinClubUuid(checkinClub.getId());
			session.setCheckinClubName(checkinClub.getName());
			session.setCheckinClubAddress(checkinClub.getAddress());
			session.setCheckinDate(LocalDateTime.now());
		}

		// Update session entity
		final SessionEntity savedSession = sessionRepository.save(session);

		// Process after update session status success.
		processSessionEvent(clientAction, account, session, oldStatus, newStatus, savedSession, checkinClub);

		return BookingResponse.builder().withResult(!Objects.isNull(savedSession)).build();
	}

	/**
	 * Internal Update session status
	 *
	 * @param sessionId
	 *            the session id
	 * @param clientAction
	 *            the client action
	 * @return
	 */
	@Transactional
	public BookingResponse cancelSession(final String sessionId) {

		final Optional<SessionEntity> sessionEntityOptional = sessionRepository.findOneByUuid(sessionId);

		if (!sessionEntityOptional.isPresent()) {
			throw new IllegalArgumentException("Session does not exist");
		}

		final SessionEntity session = sessionEntityOptional.get();
		final SessionStatus oldStatus = session.getStatus();
		final SessionStatus newStatus = SessionStatus.CANCELLED;

		session.setLastStatus(oldStatus);
		session.setStatus(newStatus);
		session.setLastStatusModifiedDate(session.getStatusModifiedDate());
		session.setStatusModifiedDate(LocalDateTime.now());

		final SessionEntity savedSession = sessionRepository.save(session);

		updateNumberOfBurnedSession(savedSession);

		updateSessionEvent(savedSession, NO_CHECKIN_CLUB);

		trackSessionHistory(savedSession, oldStatus, ClientActions.CANCEL, null);
		LOGGER.info("Finished cancel session {} for user {} with trainer {} from {}", savedSession.getUuid(),
				savedSession.getUserUuid(), savedSession.getPtUuid(), oldStatus.toString());
		return BookingResponse.builder().withResult(!Objects.isNull(savedSession)).build();
	}

	private void processSessionEvent(final ClientActions clientAction, final Account account,
			final SessionEntity session, final SessionStatus oldStatus, final SessionStatus newStatus,
			final SessionEntity savedSession, final ClubEntity checkinClub) {

		if (!Objects.isNull(savedSession)) {

			updateNumberOfBurnedSession(savedSession);

			updateSessionEvent(savedSession, checkinClub);

			trackSessionHistory(savedSession, oldStatus, clientAction, account);

			if (shouldFireChangeStatusEvent(account, clientAction, session, oldStatus)) {

				String changedBy = account.isAdmin() ? CommonConstant.ADMIN_USER_TYPE : account.type();

				fireChangeSessionStatusEvent(session, oldStatus, newStatus, changedBy);
			}

			LOGGER.info("Finished to change status of session {} for user {} with trainer {} from {} to {}.",
					savedSession.getUuid(), savedSession.getUserUuid(), savedSession.getPtUuid(), oldStatus.toString(),
					newStatus.toString());
		}
	}

	private void validateSessionInput(final String sessionId, final ClientActions clientAction) {
		if (StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException("Session ID is required for this action.");
		}

		if (Objects.isNull(clientAction)) {
			throw new IllegalArgumentException("Session status is required for this action.");
		}
	}

	private void updateSessionEvent(final SessionEntity sessionEntity, ClubEntity checkinClub) {

		final SessionStatus currentStatus = sessionEntity.getStatus();
		if (OPEN.equals(currentStatus)) {
			/**
			 * Remove session event if new status of session is open (declined
			 * by EU).
			 */
			eventManagementService
					.deleteEvents(Arrays.asList(sessionEntity.getUserEventId(), sessionEntity.getPtEventId()));
		} else {
			if(Objects.isNull(checkinClub)) {
				eventManagementService.updateEventStatus(sessionEntity);
			} else {
				eventManagementService.updateEventWithCheckinClub(sessionEntity, checkinClub);
			}
		}
	}

	private boolean shouldFireChangeStatusEvent(final Account account, final ClientActions clientAction,
			final SessionEntity session, final SessionStatus oldStatus) {

		if (account.isAdmin() && (ClientActions.ACCEPT.compareTo(clientAction) == 0
				|| ClientActions.CHECKIN.compareTo(clientAction) == 0
				|| ClientActions.NOSHOW.compareTo(clientAction) == 0)) {
			return false;
		}

		if (ClientActions.CANCEL.compareTo(clientAction) == 0) {

			if (SessionStatus.CONFIRMED == oldStatus) {
				return true;
			}

			final String type = account.type();
			return !(type != null && type.equals(session.getBookedBy()));
		}

		return true;
	}

	private void fireChangeSessionStatusEvent(final SessionEntity session, final SessionStatus oldSession,
			final SessionStatus newStatus, final String changedBy) {
		LOGGER.info("Firing change session status from {} to {}  of session {}", oldSession.toString(),
				newStatus.toString(), session.getUuid());

		final ChangeSessionStatusEvent changeSessionStatusEvent = ChangeSessionStatusEvent.newBuilder()
				.setEventId(UUID.randomUUID().toString()).setNewStatus(newStatus.toString())
				.setOldStatus(oldSession.toString()).setSessionId(session.getUuid()).setTrainerId(session.getPtUuid())
				.setEndUserId(session.getUserUuid()).setPackageUuid(session.getPackageUuid())
				.setSessionDate(ZoneDateTimeUtils.convertToSecondUTC(session.getStartTime()))
				.setSubmittedAt(System.currentTimeMillis()).setBookedBy(session.getBookedBy()).setChangedBy(changedBy)
				.build();

		eventHandler.publish(changeSessionStatusEvent);
	}

	private boolean isInvolvedInSession(final SessionEntity sessionEntity, final Account account) {
		final boolean isEndUser = StringUtils.equalsIgnoreCase(account.type(), UserType.EU.toString());

		return (!isEndUser && StringUtils.equals(sessionEntity.getPtUuid(), account.uuid()))
				|| (isEndUser && StringUtils.equals(sessionEntity.getUserUuid(), account.uuid()));
	}

	private void updateNumberOfBurnedSession(final SessionEntity sessionEntity) {

		// Increase number of burned session
		if (SessionStatus.getRevenueSessionStatus().contains(sessionEntity.getStatus())) {
			final Optional<SessionPackageEntity> opSessionPackageEntity = sessionPackageManagementService
					.getSessionPackageBySessionUUID(sessionEntity.getUuid());
			if (!opSessionPackageEntity.isPresent()) {
				throw new IllegalArgumentException("Can not find a Session Package based on current session");
			}

			final SessionPackageEntity sessionPackageEntity = opSessionPackageEntity.get();

			final Optional<OrderEntity> opOrderEntity = orderManagementService
					.findOneByUuid(sessionPackageEntity.getOrderUuid());
			if (!opOrderEntity.isPresent()) {
				throw new IllegalArgumentException("Can not find a order based on current session");
			}
			final OrderEntity orderEntity = opOrderEntity.get();

			// should not count incase change from COMPLETED to NO-SHOW
			if (!sessionEntity.getLastStatus().equals(SessionStatus.COMPLETED)) {

				sessionPackageEntity.setNumOfBurned(sessionPackageEntity.getNumOfBurned() + 1);
				sessionPackageManagementService.saveSessionPackage(sessionPackageEntity);

				orderEntity.setNumOfBurned(orderEntity.getNumOfBurned() + 1);
			}

			if (Objects.isNull(orderEntity.getExpiredDate())) {
				final LocalDateTime expiredDate = sessionEntity.getEndTime().plusDays(orderEntity.getNumberOfLimitDay())
						.withHour(23).withMinute(59).withSecond(59);
				orderEntity.setExpiredDate(expiredDate);
			}

			orderManagementService.saveOrderEntity(orderEntity);

			LOGGER.info("Decrease number of burned session for session package {} and order {}",
					sessionPackageEntity.getUuid(), orderEntity.getUuid());
		}
	}

	private void trackSessionHistory(final SessionEntity session, final SessionStatus oldStatus,
			final ClientActions clientAction, final Account account) {
		final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
		sessionHistoryEntity.setSessionUuid(session.getUuid());
		sessionHistoryEntity.setUserUuid(session.getUserUuid());
		sessionHistoryEntity.setNewPackageUuid(session.getPackageUuid());
		sessionHistoryEntity.setOldPackageUuid(session.getPackageUuid());
		sessionHistoryEntity.setOldPtUuid(session.getPtUuid());
		sessionHistoryEntity.setNewPtUuid(session.getPtUuid());
		sessionHistoryEntity.setOldStatus(oldStatus);
		sessionHistoryEntity.setNewStatus(session.getStatus());
		sessionHistoryEntity.setAction(SessionAction.mapSessionActionByClientAction(session, clientAction, account));

		sessionHistoryManagementService.save(sessionHistoryEntity);
	}

	private BookingResponse doubleBookingPTAccept(final SessionEntity sessionEntity, final SessionStatus currentStatus,
			final SessionStatus newStatus, final Account account) {
		// Check double booking when PT accept pending session.
		if (currentStatus == SessionStatus.PENDING && newStatus == SessionStatus.CONFIRMED) {
			final ReservationSlot reservationSlot = new ReservationSlot();
			reservationSlot.setStartTime(sessionEntity.getStartTime());
			reservationSlot.setEndTime(sessionEntity.getEndTime());
			reservationSlot.setSessionId(sessionEntity.getUuid());

			final Set<ReservationSlot> reservationList = Collections.singleton(reservationSlot);
			final BookingInput bookingInput = BookingInput.builder().withReservationSlotList(reservationList)
					.withUserId(sessionEntity.getUserUuid()).withTrainerId(sessionEntity.getPtUuid())
					.withAccount(account).withValidationService(validationService).build();

			return validationService.checkDoubleBooking(bookingInput);
		}

		return BookingResponse.builder().withResult(true).build();
	}

	@Transactional(readOnly = true)
	public CountDown getCountDownSession(final String userId) {
		final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus().stream()
				.map(Enum::name).collect(Collectors.toList());
		final List<Object[]> sessionOp = sessionRepository.getCountDownSession(userId, validPackageStatus,
				SessionStatus.CONFIRMED.name());
		if (!sessionOp.isEmpty()) {
			final Object[] session = sessionOp.get(0);

			final String sessionId = (String) session[0];
			final Timestamp startTimestamp = (Timestamp) session[3];
			final Long startTime = (startTimestamp != null) ? startTimestamp.toInstant().getEpochSecond() : null;

			final Timestamp endTimestamp = (Timestamp) session[4];
			final Long endTime = (endTimestamp != null) ? endTimestamp.toInstant().getEpochSecond() : null;

			final BigDecimal sessionBurned = (BigDecimal) session[1];
			final BigDecimal sessionTotal = (BigDecimal) session[2];

			return CountDown.builder().withUserUUID(userId).withSessionUUID(sessionId)
					.withSessionBurned(sessionBurned.intValue()).withSessionTotal(sessionTotal.intValue())
					.withStartTime(startTime).withEndTime(endTime).build();
		}

		return CountDown.builder().build();
	}

	@Transactional(readOnly = true)
	public SessionEntity findOne(final String sessionUuid) {
		final Optional<SessionEntity> sessionOpt = sessionRepository.findOneByUuid(sessionUuid);
		return sessionOpt.isPresent() ? sessionOpt.get() : null;
	}

	public PopupSchedule getScheduleMessageByUser(final String userUuid) {

		final Long popupSchedulePeriod = Long.valueOf(sessionProperties.getPopupSchedulePeriod());
		final LocalDate now = LocalDate.now();
		final long currentTime = ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now());
		final long endTime = ZoneDateTimeUtils
				.convertToSecondUTC(LocalDateTime.of(now, LocalTime.of(0, 0)).plusDays(popupSchedulePeriod));
		final List<Object[]> pendingSessions = sessionRepository.getSessionEntitiesScheduleByUser(userUuid, currentTime,
				endTime, SessionStatus.PENDING.name());
		final List<Object[]> activeSessions = sessionRepository.getSessionEntitiesScheduleByUser(userUuid, currentTime,
				endTime, SessionStatus.CONFIRMED.name());

		final List<ContractUser> contractUsers = contractingService.searchContractingPt(userUuid, false, "");

		if (!activeSessions.isEmpty()) {
			Long startDate = null;
			final Timestamp timestamp = (Timestamp) activeSessions.get(0)[2];
			final LocalDateTime sessionStartTime = LocalDateTime.ofInstant(timestamp.toInstant(),
					ZoneOffset.systemDefault());
			startDate = ZoneDateTimeUtils.convertToSecondUTC(sessionStartTime);

			final String ptUuid = (String)activeSessions.get(0)[3];
			final Optional<BasicUserEntity> ptUser = userService.findOneByUuid(ptUuid);
			final Integer numberSession = (Integer)activeSessions.get(0)[4];
			final Integer numberBurned = (Integer)activeSessions.get(0)[5];

			if (ptUser.isPresent()) {
				final BasicUserEntity user = ptUser.get();
				return PopupSchedule.builder().build()
						.withPopupScheduleCase(PopupScheduleCase.HAS_SESSION_HAS_ACTIVE_OR_INACTIVE_PACKAGES)
						.withStartDate(startDate).withName(user.getFullName() )
						.withPtUserName(user.getUserName()).withPtUuid(user.getUuid())
						.withTotalSessions(numberSession)
						.withBurnedSessions(numberBurned)
						.withPicture(user.getAvatar()).withTrainerList(contractUsers);
			}

			return PopupSchedule.builder().build();
		}

		if (!pendingSessions.isEmpty()) {
			return PopupSchedule.builder().build().withPopupScheduleCase(PopupScheduleCase.HAS_PENDING_SESSION);
		}

		if (contractUsers.isEmpty()) {
			return PopupSchedule.builder().build().withPopupScheduleCase(PopupScheduleCase.NO_SESSION_NO_PACKAGES);
		} else {
			final Optional<ContractUser> activeContractUser = contractUsers.stream()
					.filter(contractUser -> contractUser.getExpiredDate() != null).findFirst();

			if (!activeContractUser.isPresent()) {
				return PopupSchedule.builder().build()
						.withPopupScheduleCase(PopupScheduleCase.NO_SESSION_HAS_INACTIVE_PACKAGES)
						.withTrainerList(contractUsers);
			}

			return PopupSchedule.builder().build()
					.withPopupScheduleCase(PopupScheduleCase.NO_SESSION_HAS_ACTIVE_PACKAGES)
					.withName(activeContractUser.get().getName()).withPtUserName(activeContractUser.get().getUsername())
					.withPtUuid(activeContractUser.get().getUserUuid())
					.withTotalSessions(activeContractUser.get().getSessionNumber())
					.withBurnedSessions(activeContractUser.get().getSessionBurned())
					.withPicture(activeContractUser.get().getPicture())
					.withExpiredDate(activeContractUser.get().getExpiredDate()).withTrainerList(contractUsers);
		}
	}

	public SessionPackage transferSessions(final String packageUuid, final String newTrainer) {

		final Optional<SessionPackageEntity> opSessionPackage = sessionPackageManagementService
				.getTransferableSessionPackageByUuid(packageUuid);
		if (!opSessionPackage.isPresent()) {
			throw new IllegalArgumentException("Can not find the session package.");
		}

		final SessionPackageEntity sessionPackage = opSessionPackage.get();

		final Optional<SessionPackageEntity> opTransferredSessionPackage = sessionPackageManagementService
				.transferPackage(sessionPackage, newTrainer);
		if (!opTransferredSessionPackage.isPresent()) {
			throw new IllegalArgumentException("Can not transfer this package to trainer");
		}

		final SessionPackageEntity newSessionPackage = opTransferredSessionPackage.get();

		return SessionPackage.convertFromEntity(newSessionPackage);
	}

	public Optional<CurrentTransferPackageInfo> getTransferSessionPackageInfo(final String oldPackageUuid,
			final String newPackageUuid) {
		return sessionPackageManagementService.getTransferSessionPackageInfo(oldPackageUuid, newPackageUuid);
	}

	public boolean autoExpiredSession() {
		final List<SessionEntity> sessionEntities = sessionManagementService.autoExpiredSession();
		return !sessionEntities.isEmpty();
	}

	public Optional<SessionPackageEntity> getPackageByUuid(final String packageUUID) {

		final Optional<SessionPackageEntity> sessionPackageResp = sessionPackageManagementService
				.getSessionPackageByUuid(packageUUID);
		if (!sessionPackageResp.isPresent()) {
			LOGGER.error("Could not found session order with package uuid {}", packageUUID);
			return Optional.empty();
		}

		return Optional.of(sessionPackageResp.get());
	}

	public List<UserConnectionDto> getAllUserFromSessionByUserUuid(String uuid) {
		List<Object[]> result = sessionRepository.getAllUserFromSessionByUserUuid(uuid);
		List<UserConnectionDto> autoFollowResetEntities = new ArrayList<UserConnectionDto>();
		for (Object[] item : result) {
			autoFollowResetEntities.add(UserConnectionDto.convertFromSession(item));
		}
		return autoFollowResetEntities;
	}

	public List<UserConnectionDto> getAllUserFromSession() {
		List<Object[]> result = sessionRepository.getAllUserFromSession();
		List<UserConnectionDto> autoFollowResetEntities = new ArrayList<UserConnectionDto>();
		for (Object[] item : result) {
			autoFollowResetEntities.add(UserConnectionDto.convertFromSession(item));
		}
		return autoFollowResetEntities;
	}

	public void updateSession(SessionEntity sessionEntity) {
		sessionRepository.save(sessionEntity);
	}

	@Transactional
	public void autoBurnConfirmSession() {
		final List<SessionEntity> sessions = sessionRepository.getAllPastSessionByStatus(SessionStatus.CONFIRMED);
		sessions.forEach(session -> {
			try {
				session.setLastStatus(session.getStatus());
				session.setLastStatusModifiedDate(session.getStatusModifiedDate());
				session.setStatus(SessionStatus.COMPLETED);
				session.setStatusModifiedDate(LocalDateTime.now());
				final SessionEntity savedSession = sessionRepository.save(session);
				updateNumberOfBurnedSession(savedSession);
				updateSessionEvent(savedSession, NO_CHECKIN_CLUB);
				trackSessionHistory(savedSession, SessionStatus.CONFIRMED, ClientActions.CHECKIN, null);
			} catch (final Exception ex) {
				LOGGER.error("Can not auto burn session {}", session.getUuid());
			}
		});
	}

	@Transactional(readOnly = true)
	public int countAllHistorySession(final String eu_uuid) {
		int count = 0;
		try {
			count = sessionRepository.countAllHistorySession(eu_uuid);
		} catch (Exception e) {
			LOGGER.error("[countAllHistorySession] Error with detail: ", e.getMessage());
		}

		return count;
	}

	public void bookingCancel() {
	}
}
