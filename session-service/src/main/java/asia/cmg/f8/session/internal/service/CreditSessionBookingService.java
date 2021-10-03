package asia.cmg.f8.session.internal.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.common.message.PushingNotificationEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.sms.SmsSender;
import asia.cmg.f8.common.spec.notification.NotificationType;
import asia.cmg.f8.common.spec.session.SessionTrainingStyle;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.session.ScheduleEvent;
import asia.cmg.f8.session.client.FeignCommerceClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BasicTrainerEarningStatisticDto;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.BookingManagementDTO;
import asia.cmg.f8.session.dto.ChangeBookingSessionStatusDto;
import asia.cmg.f8.session.dto.CreditBookingResponse;
import asia.cmg.f8.session.dto.CreditBookingSessionDTO;
import asia.cmg.f8.session.dto.CreditClassBookingResponse;
import asia.cmg.f8.session.dto.CreditPackageDTO;
import asia.cmg.f8.session.dto.LevelDTO;
import asia.cmg.f8.session.dto.MatchedTrainersDTO;
import asia.cmg.f8.session.dto.PTClientsDTO;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.RecentPartnerDTO;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.dto.TimeSlot;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.UserPTiMatchRepository;
import asia.cmg.f8.session.entity.credit.BasicWalletEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditBookingTransactionEntity;
import asia.cmg.f8.session.entity.credit.CreditPackageType;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditTransactionType;
import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.exception.BookingErrorCode;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.exception.OverlappedTimeBookingException;
import asia.cmg.f8.session.exception.WalletNotEnoughException;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.BasicWalletRepository;
import asia.cmg.f8.session.repository.CreditBookingRepository;
import asia.cmg.f8.session.repository.CreditBookingTransactionRepository;
import asia.cmg.f8.session.repository.CreditSessionBookingRepository;
import asia.cmg.f8.session.service.CreditBookingService;
import asia.cmg.f8.session.utils.SessionUtil;
import asia.cmg.f8.session.utils.TimeSlotUtil;
import asia.cmg.f8.session.wrapper.dto.CreditSessionBookingRequestDTO;
import asia.cmg.f8.session.wrapper.dto.CreditSessionBookingResponseDTO;

@Service
public class CreditSessionBookingService implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(CreditSessionBookingService.class);
	private static final String SESSION_COMPLETED = "session_completed";
	private static final String TOTAL_LEEP_COIN = "total_leep_coin";
	private static final String NOTIFICATION_DATETIME_FORMAT = "HH:mm, dd/MM/yyyy";

	@Autowired
	private CreditSessionBookingRepository sessionBookingRepo;

	@Autowired
	private BasicUserRepository userRepo;

	@Autowired
	private BasicWalletRepository walletRepo;

	@Autowired
	private FeignCommerceClient commerceClient;

	@Autowired
	private SessionProperties sessionProperties;

	@Autowired
	private UserPTiMatchRepository userPTIMatchRepo;

	@Autowired
	private CreditBookingRepository bookingRepo;

	@Autowired
	private CreditBookingTransactionRepository bookingTransactionRepo;

	@Autowired
	private CreditBookingService creditBookingService;

	private MessageSource messageSource;

	private final EventHandler eventHandler;

	@Inject
	public CreditSessionBookingService(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public PagedResponse<CreditBookingSessionDTO> getSessionsHistory(String clientUuid, String trainerUuid,
			List<CreditBookingSessionStatus> statusList, Pageable pageable) {
		PagedResponse<CreditBookingSessionDTO> result = new PagedResponse<CreditBookingSessionDTO>();

		try {
			List<Integer> status = statusList.stream().map(item -> {
				return item.ordinal();
			}).collect(Collectors.toList());
			Page<CreditSessionBookingEntity> pageableEntities = sessionBookingRepo.getSessionsHistory(clientUuid,
					trainerUuid, status, pageable);
			if (pageableEntities != null) {
				List<CreditSessionBookingEntity> entities = pageableEntities.getContent();
				List<CreditBookingSessionDTO> sessionDTOs = entities.stream().map(session -> {
					return new CreditBookingSessionDTO(session);
				}).collect(Collectors.toList());
				result.setCount((int) pageableEntities.getTotalElements());
				result.setEntities(sessionDTOs);
			}
		} catch (Exception e) {
		}

		return result;
	}

	@Transactional
	public void handleAutoBurnedSessions() throws Exception {
		try {
			Account cronJob = null;
			CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.BURNED;
			List<CreditBookingEntity> confirmedSessions = bookingRepo
					.getConfirmedSessionsOverEndTime(CreditBookingSessionStatus.CONFIRMED.ordinal());
			for (CreditBookingEntity creditSessionBookingEntity : confirmedSessions) {
				CreditBookingSessionStatus oldStatus = creditSessionBookingEntity.getStatus();
				this.updateSessionStatus(creditSessionBookingEntity, oldStatus, newStatus, ClientActions.NOSHOW,
						cronJob);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void handleAutoDeductedForBurnedSessions() throws Exception {
		try {
			CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.DEDUCTED;
			List<CreditBookingSessionStatus> bookingStatus = Arrays.asList(CreditBookingSessionStatus.BURNED);
			List<BookingServiceType> bookingType = Arrays.asList(BookingServiceType.SESSION);
			List<CreditBookingEntity> burnedBookings = sessionBookingRepo
					.getBurnedSessionsToDeduct(bookingType, bookingStatus);

			burnedBookings.stream().forEach(bookingEntity -> {
				List<String> params = new ArrayList<String>();
				CreditSessionBookingEntity sessionEntity = bookingEntity.getSessions().get(0);
				CreditBookingSessionStatus oldStatus = bookingEntity.getStatus();
				BasicUserEntity clientInfo = userRepo.findOneByUuid(sessionEntity.getUserUuid()).get();

				if (clientInfo != null) {
					params.add(clientInfo.getFullName());
				}

				// Process plus 48% credit for PT, 52% credit for LEEP
				String desc = CreditTransactionType.PAY_BURNED_SESSION + "_" + bookingEntity.getId();
				Long creditAmount = Math.round((bookingEntity.getCreditAmount() * (1 - sessionEntity.getServiceBurnedFee()))
						/ sessionProperties.getVat() * (1 - sessionProperties.getPit()));
				long resultOfPT = commerceClient.plusCreditWallet(sessionEntity.getPtUuid(), bookingEntity.getId(),
						creditAmount.intValue(), CreditTransactionType.PAY_BURNED_SESSION.name(), desc, params);
				LOG.info("[handleAutoDeductedForBurnedSessions] Plus {} credits to PT {} with wallet trans id {}",
						creditAmount, sessionEntity.getPtUuid(), resultOfPT);

				creditAmount = Math.round((bookingEntity.getCreditAmount() * sessionEntity.getServiceBurnedFee()));
				long resultOfLEEP = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(),
						bookingEntity.getId(), creditAmount.intValue(), CreditTransactionType.PAY_BURNED_SESSION.name(),
						desc, params);
				LOG.info("[handleAutoDeductedForBurnedSessions] Plus {} credits to LEEP {} with wallet trans id {}",
						creditAmount, sessionProperties.getLeepAccountUuid(), resultOfLEEP);

				// Saving CreditSessionBookingTransactionEntity
				if (resultOfPT != 0) {
					bookingEntity.setStatus(CreditBookingSessionStatus.COMPLETED);
					sessionEntity.setStatus(CreditBookingSessionStatus.COMPLETED);
					bookingRepo.save(bookingEntity);
					CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
					transaction.setAction(SessionAction.AUTO_DEDUCTED);
					transaction.setCreditBooking(bookingEntity);
					transaction.setNewStatus(newStatus);
					transaction.setOldStatus(oldStatus);
					bookingTransactionRepo.save(transaction);
				}
			});
		} catch (Exception e) {
			LOG.info("[handleAutoDeductedForBurnedSessions]: ", e.getMessage());
			throw e;
		}
	}

	public PagedResponse<MatchedTrainersDTO> getSuggestedMatchedTrainers(String euUuid, Pageable pageable) {
		PagedResponse<MatchedTrainersDTO> result = new PagedResponse<MatchedTrainersDTO>();
		try {
			List<MatchedTrainersDTO> matchedTrainers = new ArrayList<MatchedTrainersDTO>();
			Page<Object[]> pagedResult = userPTIMatchRepo.searchMatchedTrainer(euUuid, pageable);
			for (Object[] record : pagedResult) {
				MatchedTrainersDTO matchedTrainer = new MatchedTrainersDTO((String) record[0], (String) record[1],
						(String) record[2], (String) record[3], (Double) record[4], (Double) record[5],
						(Double) record[6], (Double) record[7], (String) record[8], (Integer) record[9]);
				matchedTrainers.add(matchedTrainer);
			}
			result.setCount((int) pagedResult.getTotalElements());
			result.setEntities(matchedTrainers);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public PagedResponse<RecentPartnerDTO> getRecentBookedTrainers(String euUuid,
			List<CreditBookingSessionStatus> status, Pageable pageable) {
		PagedResponse<RecentPartnerDTO> result = new PagedResponse<RecentPartnerDTO>();

		try {
			List<RecentPartnerDTO> partnersDTO = new ArrayList<RecentPartnerDTO>();
			List<Integer> ordinalStatus = status.stream().map(item -> {
				return item.ordinal();
			}).collect(Collectors.toList());

			Page<Object[]> queryResult = sessionBookingRepo.getRecentBookedTrainersByUserUuid(euUuid, ordinalStatus,
					pageable);
			for (Object[] record : queryResult) {
				partnersDTO.add(new RecentPartnerDTO(String.valueOf(record[0]), String.valueOf(record[1]),
						String.valueOf(record[2]), String.valueOf(record[3]), String.valueOf(record[4]),
						(Integer) record[5], (Date) record[6]));
			}
			result.setCount((int) queryResult.getTotalElements());
			result.setEntities(partnersDTO);

		} catch (Exception e) {
		}

		return result;
	}

	public PagedResponse<RecentPartnerDTO> getRecentBookedPartners(Account account,
			List<CreditBookingSessionStatus> status, Pageable pageable) {
		PagedResponse<RecentPartnerDTO> result = new PagedResponse<RecentPartnerDTO>();

		try {
			List<RecentPartnerDTO> partnersDTO = new ArrayList<RecentPartnerDTO>();
			List<Integer> ordinalStatus = status.stream().map(item -> {
				return item.ordinal();
			}).collect(Collectors.toList());

			if (account.isEu()) {
				Page<Object[]> queryResult = sessionBookingRepo.getRecentBookedTrainersByUserUuid(account.uuid(),
						ordinalStatus, pageable);
				for (Object[] record : queryResult) {
					partnersDTO.add(new RecentPartnerDTO(String.valueOf(record[0]), String.valueOf(record[1]),
							String.valueOf(record[2]), String.valueOf(record[3]), String.valueOf(record[4]),
							(Integer) record[5], (Date) record[6]));
				}
				result.setCount((int) queryResult.getTotalElements());
				result.setEntities(partnersDTO);
			} else if (account.isPt()) {
				Page<Object[]> queryResult = sessionBookingRepo.getRecentBookedClientsByTrainerUuid(account.uuid(),
						ordinalStatus, pageable);
				for (Object[] record : queryResult) {
					partnersDTO.add(new RecentPartnerDTO(String.valueOf(record[0]), String.valueOf(record[1]),
							String.valueOf(record[2]), String.valueOf(record[3]), null, 0, (Date) record[4]));
				}
				result.setCount((int) queryResult.getTotalElements());
				result.setEntities(partnersDTO);
			}
		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * Only allowing range time to check-in 15' before start time and 15' after end
	 * time of session
	 *
	 * @param creditBookingId
	 * @param trainerUuid
	 * @param action
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public CreditSessionBookingResponseDTO handleCheckInSession(long creditBookingId, String trainerUuid,
			ClientActions action, Account account) throws Exception {
		try {
			Optional<CreditSessionBookingEntity> sessionEntityOpt = sessionBookingRepo
					.findFirstByUserUuidAndPtUuidAndCreditBookingId(account.uuid(), trainerUuid, creditBookingId);

			if (sessionEntityOpt.isPresent()) {
				CreditSessionBookingEntity sessionEntity = sessionEntityOpt.get();
				CreditBookingEntity bookingEntity = sessionEntity.getCreditBooking();
				CreditBookingSessionStatus oldStatus = sessionEntity.getStatus();

				CreditBookingSessionStatus newStatus = transitSessionState(sessionEntity, action, account);

				if (newStatus != oldStatus) {
					bookingEntity = this.updateSessionStatus(bookingEntity, oldStatus, newStatus, action, account);
					return new CreditSessionBookingResponseDTO(bookingEntity, null, null);
				} else {
					throw new BookingValidationException(BookingErrorCode.SESSION_INVALID);
				}
			} else {
				throw new BookingValidationException(BookingErrorCode.SESSION_INVALID);
			}
		} catch (BookingValidationException e) {
			LOG.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw e;
		}
	}

	@Transactional
	public ChangeBookingSessionStatusDto handleUpdateSessionStatus(long bookingId, ClientActions action,
			Account account) throws Exception {
		try {
			CreditBookingEntity bookingEntity = bookingRepo.findOne(bookingId);
			if (bookingEntity == null) {
				throw new IllegalArgumentException("Training session does not exist");
			}
			CreditBookingSessionStatus oldStatus = bookingEntity.getStatus();
			CreditSessionBookingEntity sessionEntity = bookingEntity.getSessions().get(0);

			if (!account.isAdmin() && !isInvolvedInSession(sessionEntity, account)) {
				throw new BookingValidationException(BookingErrorCode.SESSION_INVALID
						.withDetail("Can not change status because current user is not involved in this session"));
			}

			CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.CANCELLED;
			newStatus = transitSessionState(sessionEntity, action, account);

			if (newStatus != oldStatus) {
				this.updateSessionStatus(bookingEntity, oldStatus, newStatus, action, account);
			}

			ChangeBookingSessionStatusDto dto = new ChangeBookingSessionStatusDto();
			dto.setStatus(newStatus);
			dto.setId(bookingId);

			pushNotificationWhenSessionChangeStatus(account, sessionEntity);

			return dto;
		} catch (Exception e) {
			throw e;
		}
	}

	void pushNotificationWhenSessionChangeStatus(Account account, CreditSessionBookingEntity bookingEntity) {
		try {
			boolean isPTAction = account.uuid().equals(bookingEntity.getPtUuid());
			String receiverUuid = isPTAction ? bookingEntity.getUserUuid() : bookingEntity.getPtUuid();

			List<CharSequence> messageData = new ArrayList<>();
			messageData.add(userRepo.findOneByUuid(account.uuid()).get().getFullName());
			messageData.add(bookingEntity.getStartTime().format(DateTimeFormatter.ofPattern(NOTIFICATION_DATETIME_FORMAT)));

			String messageKey = null;
			NotificationType actionName = null;
			if(bookingEntity.getStatus().equals(CreditBookingSessionStatus.REJECTED)) {
				messageKey = isPTAction ? "message.session.pt.rejected" : "message.session.client.rejected";
				actionName = isPTAction ? NotificationType.SESSION_PT_REJECTED : NotificationType.SESSION_CLIENT_REJECTED;
			} else if(bookingEntity.getStatus().equals(CreditBookingSessionStatus.CONFIRMED)) {
				messageKey = isPTAction ? "message.session.pt.confirmed" : "message.session.client.confirmed";
				actionName = isPTAction ? NotificationType.SESSION_PT_CONFIRMED : NotificationType.SESSION_CLIENT_CONFIRMED;
			} else if(bookingEntity.getStatus().equals(CreditBookingSessionStatus.CANCELLED)) {
				messageKey = isPTAction ? "message.session.pt.cancelled" : "message.session.client.cancelled";
				actionName = isPTAction ? NotificationType.SESSION_PT_CANCELLED : NotificationType.SESSION_CLIENT_CANCELLED;
			}

			PushingNotificationEvent event = PushingNotificationEvent.newBuilder()
					.setReceiverUuid(receiverUuid)
					.setMessageKey(messageKey)
					.setMessageData(messageData)
					.setName(actionName.toString())
					.setCustomData(new ArrayList<>())
					.build();
			eventHandler.publish(event);
		} catch(Exception ex) {
			LOG.error("pushNotificationWhenSessionChangeStatus: {}", ex);
		}
	}

	/**
	 * Validate action only support CANCEL and COMPLETE then call
	 * updateSessionStatusByAdmin() function
	 *
	 * @param sessionId
	 * @param action
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public ChangeBookingSessionStatusDto handleAdminUpdateSessionStatus(long sessionId, String action, String uuid)
			throws Exception {
		try {
			CreditBookingEntity bookingEntity = bookingRepo.findOne(sessionId);
			if (bookingEntity == null) {
				throw new IllegalArgumentException("Training session does not exist");
			}
			CreditSessionBookingEntity sessionEntity = bookingEntity.getSessions().get(0);
			if (sessionEntity.getStatus() != CreditBookingSessionStatus.BURNED
					&& sessionEntity.getStatus() != CreditBookingSessionStatus.CONFIRMED) {
				throw new Exception("Change status failed. Only support change status from BURNED and CONFIRMED");
			}
			CreditBookingSessionStatus oldStatus = sessionEntity.getStatus();
			CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.CANCELLED;
			switch (action) {
			case "CANCEL":
				newStatus = CreditBookingSessionStatus.CANCELLED;
				if (newStatus != oldStatus) {
					this.handleAdminCancelledSession(bookingEntity, oldStatus, newStatus, action, uuid);
				}
				break;
			case "COMPLETE":
				newStatus = CreditBookingSessionStatus.COMPLETED;
				if (newStatus != oldStatus) {
					this.handleAdminCompletedSession(bookingEntity, oldStatus, newStatus, action, uuid);
				}
				break;
			default:
				throw new Exception("Can not change status. The status is not supported to change");
			}

			ChangeBookingSessionStatusDto dto = new ChangeBookingSessionStatusDto();
			dto.setStatus(newStatus);
			dto.setId(sessionId);

			return dto;
		} catch (Exception e) {
			throw e;
		}
	}

	public CreditBookingSessionStatus transitSessionState(final CreditSessionBookingEntity sessionEntity,
			final ClientActions actions, final Account account) {
		final CreditBookingSessionStatus currentStatus = sessionEntity.getStatus();
		CreditBookingSessionStatus newStatus = currentStatus;

		switch (actions) {
		case ACCEPT:
			newStatus = currentStatus.confirm(sessionEntity, account);
			break;
		case DECLINE:
			newStatus = currentStatus.reject(sessionEntity, account);
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

	private boolean isInvolvedInSession(final CreditSessionBookingEntity sessionEntity, final Account account) {
		final boolean isEndUser = StringUtils.equalsIgnoreCase(account.type(), UserType.EU.toString());

		return (!isEndUser && StringUtils.equals(sessionEntity.getPtUuid(), account.uuid()))
				|| (isEndUser && StringUtils.equals(sessionEntity.getUserUuid(), account.uuid()));
	}

	public List<CreditSessionBookingResponseDTO> handleBookingSessions(CreditSessionBookingRequestDTO request,
			String lang, final Account account) throws Exception {
		List<CreditSessionBookingResponseDTO> bookingEntities = new ArrayList<CreditSessionBookingResponseDTO>();

		try {
			Set<TimeSlot> timeSlots = request.getTimeSlots();
			Set<ReservationSlot> reservationSlots = TimeSlotUtil.parseTimeSlot(timeSlots);

			// Validate the time slots not in the past
			for (ReservationSlot reservationSlot : reservationSlots) {
				if (reservationSlot.getStartTime().isBefore(LocalDateTime.now())) {
					LOG.info("!!! The start time is not valid !!!");
					throw new BookingValidationException(BookingErrorCode.NOT_VALID_TIME_SLOT);
				}
			}

			// Validate level and booking credit of Trainer
			List<Object[]> queryResults = userRepo.getBookingCreditOfApprovedTrainer(request.getPtUuid());
			if (queryResults == null || queryResults.isEmpty()) {
				LOG.info("PT is not approved or invalid level");
				throw new Exception("PT is not approved or invalid level");
			}
			Object[] queryResult = queryResults.get(0);
			LevelDTO ptLevelDTO = new LevelDTO(String.valueOf(queryResult[0]), (Integer) queryResult[1],
					(Double) queryResult[2], (Double) queryResult[3]);

			// Sorting slots by start time ascend
			List<ReservationSlot> sortedReservationList = reservationSlots.stream()
					.sorted((slot1, slot2) -> slot1.getStartTime().compareTo(slot2.getStartTime()))
					.collect(Collectors.toList());

			// Validate the booking session slots
			CreditBookingEntity overlappedEntity = validateTimeSlotsOfBookingByEU(request.getUserUuid(),
					sortedReservationList);
			if (overlappedEntity != null) {
				LOG.info("!!! Duplicated booking timeslots !!!");

				switch (overlappedEntity.getBookingType()) {
				case CLASS:
					CreditBookingResponse<CreditClassBookingResponse> classBookingResponse = new CreditBookingResponse<CreditClassBookingResponse>();
					CreditClassBookingResponse classBooking = new CreditClassBookingResponse(overlappedEntity);
					classBookingResponse.setServiceType(BookingServiceType.CLASS);
					classBookingResponse.setBookingData(classBooking);

					throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, classBookingResponse);
				case SESSION:
					BasicUserInfo userInfo = null;
					BasicUserInfo trainerInfo = null;
					CreditSessionBookingEntity session = overlappedEntity.getSessions().get(0);
					BasicUserEntity userEntity = userRepo.findOneByUuid(session.getUserUuid()).get();
					if (userEntity != null) {
						userInfo = BasicUserInfo.convertFromEntity(userEntity);
					}
					BasicUserEntity trainerEntity = userRepo.findOneByUuid(session.getPtUuid()).get();
					if (trainerEntity != null) {
						trainerInfo = BasicUserInfo.convertFromEntity(trainerEntity);
					}
					CreditSessionBookingResponseDTO sessionBookingDTO = new CreditSessionBookingResponseDTO(
							overlappedEntity, userInfo, trainerInfo);
					CreditBookingResponse<CreditSessionBookingResponseDTO> sessionBookingResponse = new CreditBookingResponse<CreditSessionBookingResponseDTO>();
					sessionBookingResponse.setServiceType(BookingServiceType.SESSION);
					sessionBookingResponse.setBookingData(sessionBookingDTO);

					throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, sessionBookingResponse);
				default:
					throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, null);
				}
			}

			// Validate wallet balance of client
			BasicWalletEntity wallet = walletRepo.findByOwnerUuid(request.getUserUuid());
			int totalBookingCreditAmount = sortedReservationList.size() * ptLevelDTO.getPtBookingCredit();
			if (wallet == null) {
				LOG.info("Wallet is not enough credits");
				int lackOfAmount = totalBookingCreditAmount;
				CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
				throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount,
						creditPackageDTO.getTotalPrice() * lackOfAmount);
			}

			if (!wallet.getActive() || wallet.getAvailableCredit() < totalBookingCreditAmount) {
				LOG.info("Wallet is not enough credits");
				int lackOfAmount = totalBookingCreditAmount - wallet.getAvailableCredit();
				CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
				throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount,
						creditPackageDTO.getTotalPrice() * lackOfAmount);
			}

			BasicUserInfo userInfo = null;
			BasicUserInfo trainerInfo = null;
			BasicUserEntity userEntity = userRepo.findOneByUuid(request.getUserUuid()).get();
			if (userEntity != null) {
				userInfo = BasicUserInfo.convertFromEntity(userEntity);
			}
			BasicUserEntity trainerEntity = userRepo.findOneByUuid(request.getPtUuid()).get();
			if (trainerEntity != null) {
				trainerInfo = BasicUserInfo.convertFromEntity(trainerEntity);
			}

			for (ReservationSlot reservationSlot : sortedReservationList) {
				CreditSessionBookingEntity sessionBookingEntity = new CreditSessionBookingEntity();
				sessionBookingEntity.setBookedBy(account.uuid());
				sessionBookingEntity.setBookingStudioAddress(request.getBookingStudioAddress());
				sessionBookingEntity.setBookingStudioName(request.getBookingStudioName());
				sessionBookingEntity.setBookingStudioUuid(request.getBookingStudioUuid());
				sessionBookingEntity.setCreditAmount(ptLevelDTO.getPtBookingCredit());
				sessionBookingEntity.setEndTime(reservationSlot.getEndTime());
				sessionBookingEntity.setPtLevel(ptLevelDTO.getCode());
				sessionBookingEntity.setPtUuid(request.getPtUuid());
				sessionBookingEntity.setStartTime(reservationSlot.getStartTime());
				sessionBookingEntity.setStatus(CreditBookingSessionStatus.BOOKED);
				sessionBookingEntity.setUserUuid(request.getUserUuid());
				sessionBookingEntity.setUuid(UUID.randomUUID().toString());
				sessionBookingEntity.setTrainingStyle(request.getTrainingStyle() == null ? SessionTrainingStyle.OFFLINE
						: SessionTrainingStyle.values()[request.getTrainingStyle()]);
				sessionBookingEntity.setServiceFee(1 - ptLevelDTO.getPtCommission());
				sessionBookingEntity.setServiceBurnedFee(1 - ptLevelDTO.getPtBurnedCommission());

				CreditBookingEntity bookingEntity = new CreditBookingEntity();
				bookingEntity.setBookedBy(account.uuid());
				bookingEntity.setBookingType(BookingServiceType.SESSION);
				bookingEntity.setSessions(Arrays.asList(sessionBookingEntity));
				bookingEntity.setClientUuid(request.getUserUuid());
				bookingEntity.setCreditAmount(ptLevelDTO.getPtBookingCredit());
				bookingEntity.setStatus(CreditBookingSessionStatus.BOOKED);
				bookingEntity.setStudioName(request.getBookingStudioName());
				bookingEntity.setStudioUuid(request.getBookingStudioUuid());
				bookingEntity.setBookingDay(reservationSlot.getStartTime().toLocalDate());
				bookingEntity.setStartTime(reservationSlot.getStartTime());
				bookingEntity.setEndTime(reservationSlot.getEndTime());

				bookingEntity = this.createCreditSessionBooking(bookingEntity);
				if (bookingEntity != null) {
					bookingEntities.add(new CreditSessionBookingResponseDTO(bookingEntity, userInfo, trainerInfo));
				}

				Boolean checkFirstBooking = creditBookingService.checkFirstBooking(account.uuid(), BookingServiceType.SESSION);
				if (checkFirstBooking) {
					creditBookingService.sendEmailToFinance(bookingEntity);
				}
			}
			remindNewSessionReservationNotification(sortedReservationList, request, account);
			if (!StringUtils.isEmpty(trainerInfo.getPhone()) && trainerInfo.getVerifyPhone() != null && trainerInfo.getVerifyPhone()) {
				sendSmsToPT(trainerInfo, userInfo, lang, sortedReservationList.get(0).getStartTime());
			}

		} catch (Exception e) {
			throw e;
		}

		return bookingEntities;
	}
	
	private void remindNewSessionReservationNotification(List<ReservationSlot> reservationList, 
			CreditSessionBookingRequestDTO request, Account account) {
		try {
			boolean isPTAction = account.uuid().equals(request.getPtUuid());
			String receiverUuid = isPTAction ? request.getUserUuid() : request.getPtUuid();
			BasicUserEntity actor = userRepo.findOneByUuid(account.uuid()).get();
			
			for (ReservationSlot reservationSlot : reservationList) {
				List<CharSequence> messageData = new ArrayList<>();
				messageData.add(actor.getFullName());
				messageData.add(reservationSlot.getStartTime().format(DateTimeFormatter.ofPattern(NOTIFICATION_DATETIME_FORMAT)));

				String messageKey = isPTAction ? "message.session.schedule.remind.client.confirm" : "message.session.schedule.remind.pt.confirm";
				NotificationType actionName = isPTAction ? NotificationType.SESSION_CLIENT_CONFIRM : NotificationType.SESSION_PT_CONFIRM;

				PushingNotificationEvent event = PushingNotificationEvent.newBuilder()
						.setReceiverUuid(receiverUuid)
						.setMessageKey(messageKey)
						.setMessageData(messageData)
						.setName(actionName.toString())
						.setCustomData(new ArrayList<>())
						.build();
				eventHandler.publish(event);
			}
		} catch(Exception ex) {
			LOG.error("remindNewSessionReservationNotification: {}", ex);
		}
	}

	private void sendSmsToPT(BasicUserInfo trainerInfo, BasicUserInfo userInfo, String language,
			LocalDateTime startTime) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String phone = trainerInfo.getPhone().replace("+", "");
			if (phone.charAt(0) == Character.valueOf('0')) {
				phone = phone.replaceFirst("0", "");
			}
			String userPhone = StringUtils.isEmpty(userInfo.getPhone()) ? "" : userInfo.getPhone();
			Object[] param = new Object[] { userInfo.getName(), startTime.format(formatter), userPhone };
			String message = language.equalsIgnoreCase("vi")
					? messageSource.getMessage("session.sms.session.booking", param, new Locale("vi", "VN"))
					: messageSource.getMessage("session.sms.session.booking", param, Locale.ENGLISH);
			SmsSender sms = new SmsSender();
			sms.sendSmsMessage(phone, message);

		} catch (Exception e) {
			LOG.error("Failed to send sms to {}", trainerInfo.getPhone());
		}
	}

	/**
	 * Validate time slot of sessions of EU 1. Time slot in the past 2. Time slot is
	 * duplicated with the current one with not in status of
	 * CANCELLED/TRAINER_CANCELLED/EU_CANCELLED/BURNED/COMPLETED
	 *
	 * @param userUuid
	 * @param reservationSlots
	 * @return Overlapped CreditBookingEntity before
	 */
	private CreditBookingEntity validateTimeSlotsOfBookingByEU(final String userUuid,
			List<ReservationSlot> reservationSlots) {
		try {
			for (ReservationSlot reservationSlot : reservationSlots) {
				List<BookingServiceType> bookingTypes = Arrays.asList(BookingServiceType.CLASS,
						BookingServiceType.SESSION);
				List<CreditBookingSessionStatus> statusList = Arrays.asList(CreditBookingSessionStatus.BOOKED,
						CreditBookingSessionStatus.CONFIRMED);
				List<CreditBookingEntity> entities = sessionBookingRepo.findOverlappedBookedByClientAndTime(userUuid,
						reservationSlot.getStartTime(), reservationSlot.getEndTime(), statusList, bookingTypes);
				if (!entities.isEmpty()) {
					return entities.get(0);
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	@Transactional
	private CreditBookingEntity createCreditSessionBooking(CreditBookingEntity entity) throws Exception {
		try {
			// Saving CreditSessionBookingEntity
			CreditBookingEntity booking = bookingRepo.save(entity);

			// SavingCreditSessionBookingTransactionEntity
			CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
			transaction.setAction(SessionAction.BOOKED);
			transaction.setCreditBooking(entity);
			transaction.setNewStatus(CreditBookingSessionStatus.BOOKED);
			transaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
			bookingTransactionRepo.save(transaction);

			return booking;
		} catch (Exception e) {
			LOG.info("[createCreditSessionBooking] Error on create new booking entity {}: {}", entity.toString(),
					e.getMessage());
			throw e;
		}
	}

	@Transactional
	private CreditBookingEntity updateSessionStatus(CreditBookingEntity bookingEntity,
			CreditBookingSessionStatus oldStatus, CreditBookingSessionStatus newStatus, ClientActions action,
			Account account) throws Exception {
		try {
			long walletTransId = 0;
			CreditSessionBookingEntity entity = bookingEntity.getSessions().get(0);
			List<String> params = new ArrayList<String>();

			switch (newStatus) {
			case REJECTED:
				break;

			case CONFIRMED:
                BasicUserEntity trainerInfo = userRepo.findOneByUuid(entity.getPtUuid()).get();
                if (trainerInfo != null) {
                    params.add(trainerInfo.getFullName());
                }

                // Subtract credit to EU
                String desc = CreditTransactionType.BOOKING_SESSION.name() + "_" + entity.getId();
                walletTransId = commerceClient.subtractCreditWallet(entity.getUserUuid(), bookingEntity.getId(),
                        entity.getCreditAmount(), CreditTransactionType.BOOKING_SESSION.name(), desc, params);
                LOG.info("[updateSessionStatus] Subtract {} credits to EU {} with wallet trans id {}",
                        entity.getCreditAmount(), entity.getUserUuid(), walletTransId);

                if (walletTransId == 0) {
                    throw new BookingValidationException(BookingErrorCode.NOT_ENOUGH_CREDIT);
                }
				
				try {
					ScheduleEvent event = ScheduleEvent.newBuilder()
							.setNotiType(NotificationType.SESSION_CONFIRMED_EU.toString())
							.setStartAt(SessionUtil.convertDateTimeToEpochMiliSecond(bookingEntity.getStartTime()))
							.setEventId(UUID.randomUUID().toString()).setUserUuid(bookingEntity.getClientUuid())
							.build();
					eventHandler.publish(event);
				} catch (Exception e) {
					LOG.error("publishPTNotification: {}", e);
				}
				break;

			case CANCELLED:
				// From action NOSHOW of Admin
				if (ClientActions.NOSHOW == action && CreditBookingSessionStatus.CONFIRMED == oldStatus) {
					// Process refund 100% credit for EU
					desc = CreditTransactionType.REFUND_CANCEL_SESSION.name() + "_" + bookingEntity.getId();
					walletTransId = commerceClient.plusCreditWallet(entity.getUserUuid(), bookingEntity.getId(),
							entity.getCreditAmount(), CreditTransactionType.REFUND_CANCEL_SESSION.name(), desc, params);

					// If refund successfully then record more refunded transaction
					if (walletTransId != 0) {
						CreditBookingTransactionEntity refundedTransaction = new CreditBookingTransactionEntity();
						refundedTransaction
								.setAction(SessionAction.mapSessionActionByClientAction(entity, action, account));
						refundedTransaction.setCreditBooking(bookingEntity);
						refundedTransaction.setNewStatus(CreditBookingSessionStatus.REFUNDED);
						refundedTransaction.setOldStatus(oldStatus);
						bookingTransactionRepo.save(refundedTransaction);
					} else {
						throw new Exception("Refund credit to client failed");
					}

					// Penalty amount of credit for PT
					
                } else if (ClientActions.CANCEL == action && CreditBookingSessionStatus.BOOKED == oldStatus) {
                    // Nothing to do
                }

				break;

			case TRAINER_CANCELLED:
			case EU_CANCELLED:
				// Process refund 100% credit for EU
				desc = CreditTransactionType.REFUND_CANCEL_SESSION.name() + "_" + bookingEntity.getId();
				walletTransId = commerceClient.plusCreditWallet(entity.getUserUuid(), bookingEntity.getId(),
						entity.getCreditAmount(), CreditTransactionType.REFUND_CANCEL_SESSION.name(), desc, params);
				LOG.info("[updateSessionStatus] Plus {} credits to EU {} with wallet trans id {}",
						entity.getCreditAmount(), entity.getUserUuid(), walletTransId);

				if (walletTransId == 0) {
					throw new Exception("Refund credit to client failed");
				}

				break;

			case COMPLETED:
				// Process plus 60% credit for PT, 40% credit for LEEP
				desc = CreditTransactionType.PAY_COMPLETED_SESSION + "_" + bookingEntity.getId();
				BasicUserEntity clientInfo = userRepo.findOneByUuid(entity.getUserUuid()).get();
				if (clientInfo != null) {
					params.add(clientInfo.getFullName());
				}

				Long trainerPaidCredit = Math.round((entity.getCreditAmount() * (1 - entity.getServiceFee()))
						/ sessionProperties.getVat() * (1 - sessionProperties.getPit()));
				walletTransId = commerceClient.plusCreditWallet(entity.getPtUuid(), bookingEntity.getId(),
						trainerPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_SESSION.name(), desc, params);
				LOG.info("[updateSessionStatus] Plus {} credits to PT {} with wallet trans id {}", trainerPaidCredit,
						entity.getPtUuid(), walletTransId);

				Long leepPaidCredit = Math.round((entity.getCreditAmount() * entity.getServiceFee()));
				long trainerPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(),
						bookingEntity.getId(), leepPaidCredit.intValue(),
						CreditTransactionType.PAY_COMPLETED_SESSION.name(), desc, params);
				LOG.info("[updateSessionStatus] Plus {} credits to LEEP {} with wallet trans id {}", leepPaidCredit,
						sessionProperties.getLeepAccountUuid(), trainerPaidCreditResult);

				// If plus successfully then record more deducted transaction
				if (walletTransId != 0) {
					CreditBookingTransactionEntity refundedTransaction = new CreditBookingTransactionEntity();
					refundedTransaction
							.setAction(SessionAction.mapSessionActionByClientAction(entity, action, account));
					refundedTransaction.setCreditBooking(bookingEntity);
					refundedTransaction.setNewStatus(CreditBookingSessionStatus.DEDUCTED);
					refundedTransaction.setOldStatus(oldStatus);
					bookingTransactionRepo.save(refundedTransaction);
				} else {
					throw new Exception("Plus credit to PT failed");
				}

				break;

			case BURNED:
				break;

			default:
				break;
			}

			// Saving CreditSessionBookingEntity
			bookingEntity.setStatus(newStatus);
			entity.setStatus(newStatus);
			bookingEntity = bookingRepo.save(bookingEntity);

			// Saving CreditSessionBookingTransactionEntity
			CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
			transaction.setAction(SessionAction.mapSessionActionByClientAction(entity, action, account));
			transaction.setCreditBooking(bookingEntity);
			transaction.setNewStatus(newStatus);
			transaction.setOldStatus(oldStatus);
			bookingTransactionRepo.save(transaction);

			return bookingEntity;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Admin update status to cancelled when session is no show => action refund
	 * 100% credit to EU
	 *
	 * @return
	 */
	@Transactional
	private CreditBookingEntity handleAdminCancelledSession(CreditBookingEntity bookingEntity,
			CreditBookingSessionStatus oldStatus, CreditBookingSessionStatus newStatus, String action, String uuid)
			throws Exception {

		try {
			long walletTransId = 0;
			// Update booking entity
			CreditSessionBookingEntity entity = bookingEntity.getSessions().get(0);
			bookingEntity.setStatus(newStatus);

			entity.setStatus(newStatus);
			// Add record Booking transaction
			CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
			transaction.setAction(SessionAction.ADMIN_CANCELLED);
			transaction.setCreditBooking(bookingEntity);
			transaction.setNewStatus(newStatus);
			transaction.setOldStatus(oldStatus);
			bookingTransactionRepo.save(transaction);
			// Refund 100% to Client
			List<String> params = new ArrayList<String>();
			String desc = "";
			desc = CreditTransactionType.REFUND_CANCEL_SESSION.name() + "_" + bookingEntity.getId();
			walletTransId = commerceClient.plusCreditWallet(entity.getUserUuid(), bookingEntity.getId(),
					entity.getCreditAmount(), CreditTransactionType.REFUND_CANCEL_SESSION.name(), desc, params);
			if (walletTransId == 0)
				throw new Exception("Refund credit to client failed");
			bookingEntity = bookingRepo.save(bookingEntity);
			entity = sessionBookingRepo.save(entity);
			return bookingEntity;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Update status to completed when session client forget check-in, plus 100%
	 * credit with commission
	 *
	 * @param bookingEntity
	 * @param oldStatus
	 * @param newStatus
	 * @param action
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	@Transactional
	private CreditBookingEntity handleAdminCompletedSession(CreditBookingEntity bookingEntity,
			CreditBookingSessionStatus oldStatus, CreditBookingSessionStatus newStatus, String action, String uuid)
			throws Exception {
		try {
			long walletTransId = 0;
			CreditSessionBookingEntity entity = bookingEntity.getSessions().get(0);
			bookingEntity.setStatus(newStatus);
			entity.setStatus(newStatus);

			// Update booking transaction
			CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
			transaction.setAction(SessionAction.ADMIN_COMPLETED);
			transaction.setNewStatus(newStatus);
			transaction.setOldStatus(oldStatus);
			transaction.setCreditBooking(bookingEntity);
			bookingTransactionRepo.save(transaction);

			// plus wallet for PT
			String desc = CreditTransactionType.PAY_COMPLETED_SESSION + "_" + bookingEntity.getId();
			List<String> params = new ArrayList<String>();
			BasicUserEntity clientInfo = userRepo.findOneByUuid(entity.getUserUuid()).get();
			if (clientInfo != null) {
				params.add(clientInfo.getFullName());
			}

			final Double LEEP_SERVICE_FEE = entity.getServiceFee();
			Long trainerPaidCredit = Math.round(entity.getCreditAmount() * (1 - LEEP_SERVICE_FEE)
					/ sessionProperties.getVat() * (1 - sessionProperties.getPit()));
			walletTransId = commerceClient.plusCreditWallet(entity.getPtUuid(), bookingEntity.getId(),
					trainerPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_SESSION.name(), desc, params);
			LOG.info("[updateSessionStatus] Plus {} credits to PT {} with wallet trans id {}", trainerPaidCredit,
					entity.getPtUuid(), walletTransId);

			// plus wallet for Leep
			Long leepPaidCredit = Math.round(entity.getCreditAmount() * LEEP_SERVICE_FEE);
			long leepWalletId = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(),
					bookingEntity.getId(), leepPaidCredit.intValue(),
					CreditTransactionType.PAY_COMPLETED_SESSION.name(), desc, params);
			LOG.info("[updateSessionStatus] Plus {} credits to LEEP {} with wallet trans id {}", leepPaidCredit,
					sessionProperties.getLeepAccountUuid(), leepWalletId);

			if (walletTransId == 0 || leepWalletId == 0) {
				throw new Exception("Plus credit to PT and Leep failed");
			}
			bookingEntity = bookingRepo.save(bookingEntity);
			entity = sessionBookingRepo.save(entity);
			return bookingEntity;
		} catch (Exception e) {
			throw e;
		}
	}

	public PageResponse<PTClientsDTO> getPTClients(final String uuid, String filter, int page, int perPage) {
		List<PTClientsDTO> result = new ArrayList<PTClientsDTO>();
		Pageable pageable = new PageRequest(page, perPage);
		Page<Object[]> entitiesPage = filter == null ? sessionBookingRepo.getPTClients(uuid, pageable)
				: sessionBookingRepo.getPTClientsWithFilter(uuid, filter, pageable);
		List<Object[]> ptClients = entitiesPage.getContent();
		for (Object[] row : ptClients) {
			PTClientsDTO ptClientsDTO = new PTClientsDTO();
			ptClientsDTO.setAvatar(row[0] == null ? "" : row[0].toString());
			ptClientsDTO.setFullname(row[1] == null ? "" : row[1].toString());
			ptClientsDTO.setUsername(row[2] == null ? "" : row[2].toString());
			ptClientsDTO.setPhone(row[3] == null ? "" : row[3].toString());
			ptClientsDTO.setEmail(row[4] == null ? "" : row[4].toString());
			Map<String, BigInteger> totalSessionAndtotalLeepCoin = getTotalSessionCompletedAndTotalLeepCoin(uuid,
					row[5].toString());
			ptClientsDTO.setTotalCompletedSession(totalSessionAndtotalLeepCoin.get(SESSION_COMPLETED));
			ptClientsDTO.setTotalCredit(totalSessionAndtotalLeepCoin.get(TOTAL_LEEP_COIN));
			result.add(ptClientsDTO);
		}
		PageResponse<PTClientsDTO> pageResponse = new PageResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount(entitiesPage.getTotalElements());
		return pageResponse;
	}

	private Map<String, BigInteger> getTotalSessionCompletedAndTotalLeepCoin(String ptUuid, String userUuid) {
		List<Object[]> rowData = sessionBookingRepo.getTotalSessionCompletedAndTotalLeepCoin(ptUuid, userUuid);
		Object[] rows = rowData.get(0);
		Map<String, BigInteger> result = new HashMap<String, BigInteger>();
		result.put(SESSION_COMPLETED, rows[0] == null ? BigInteger.valueOf(0) : (BigInteger) rows[0]);
		result.put(TOTAL_LEEP_COIN, rows[1] == null ? BigInteger.valueOf(0) : ((BigDecimal) rows[1]).toBigInteger());
		return result;
	}

	public List<BasicTrainerEarningStatisticDto> getAllCreditSesionsBookingOfTrainer(LocalDateTime start,
			LocalDateTime end, String ptUuid) {
		List<CreditBookingSessionStatus> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED,
				CreditBookingSessionStatus.BURNED);
		final List<CreditSessionBookingEntity> result = sessionBookingRepo
				.findByStartTimeBetweenAndPtUuidAndStatusInOrderByStartTime(start, end, ptUuid, completedCode);
		final List<BasicTrainerEarningStatisticDto> stats = new ArrayList<BasicTrainerEarningStatisticDto>();

		result.stream().forEach(entry -> {
			final Long time = entry.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond();
			BasicTrainerEarningStatisticDto dto = new BasicTrainerEarningStatisticDto(time, entry.getCreditAmount());
			stats.add(dto);
		});
		return stats;
	}

	public HashMap<String, BigInteger> countStatusBookingByRange(LocalDateTime start, LocalDateTime end) {
		List<Integer> statusCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal(), CreditBookingSessionStatus.CANCELLED.ordinal());
		List<Object[]> dataCouting = sessionBookingRepo.countStatusBookingByRange(start, end, statusCode);

		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();
		map.put(CreditBookingSessionStatus.COMPLETED.name().toLowerCase(), BigInteger.valueOf(0));
		map.put(CreditBookingSessionStatus.BURNED.name().toLowerCase(), BigInteger.valueOf(0));
		map.put(CreditBookingSessionStatus.CANCELLED.name().toLowerCase(), BigInteger.valueOf(0));
		for (Object[] row : dataCouting) {
			final BigInteger count = (BigInteger) row[1];

			final Integer status = (Integer) row[0];
			if (status == CreditBookingSessionStatus.COMPLETED.ordinal()) {
				map.replace(CreditBookingSessionStatus.COMPLETED.name().toLowerCase(), count);
			} else if (status == CreditBookingSessionStatus.BURNED.ordinal()) {
				map.replace(CreditBookingSessionStatus.BURNED.name().toLowerCase(), count);
			} else if (status == CreditBookingSessionStatus.CANCELLED.ordinal()) {
				map.replace(CreditBookingSessionStatus.CANCELLED.name().toLowerCase(), count);
			}
		}

		return map;
	}

	public HashMap<String, Integer> getTrainerStatisticOverview(LocalDateTime start, LocalDateTime end, String ptUuid) {
		List<Integer> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal());
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("new_clients", 0);
		map.put("completed", 0);
		map.put("earning", 0);
		Integer newClient = sessionBookingRepo.getStatsNewClients(start, end, ptUuid);
		map.put("new_clients", newClient);
		List<Object[]> rowsData = sessionBookingRepo.getStatsTotalCompletedSession(start, end, ptUuid, completedCode);
		Object[] row = rowsData.get(0);
		if (row[0] != null) {
			Integer completed = ((BigInteger) row[0]).intValue();
			map.replace("completed", completed);
		}
		if (row[1] != null) {
			Integer earning = ((BigDecimal) row[1]).intValue();
			map.replace("earning", earning);
		}

		return map;
	}

	public HashMap<String, Long> countCancelledBookingByRange(LocalDateTime start, LocalDateTime end,
			Integer serviceType) {
		List<Integer> cancelledCode = Arrays.asList(CreditBookingSessionStatus.TRAINER_CANCELLED.ordinal(),
				CreditBookingSessionStatus.EU_CANCELLED.ordinal(), CreditBookingSessionStatus.CANCELLED.ordinal());
		List<Object[]> dataRaws = sessionBookingRepo.countCancelledSessionsBookingByRange(start, end, cancelledCode,
				serviceType);
		HashMap<String, Long> map = new HashMap<String, Long>();
		if (!dataRaws.isEmpty()) {
			for (Object[] row : dataRaws) {
				final long count = ((BigInteger) row[0]).longValue();
				final long totalCoint = ((BigDecimal) row[1]).longValue();
				map.put("count", count);
				map.put("total_coins", totalCoint);
			}
		} else {
			map.put("count", Long.valueOf(0));
			map.put("total_coins", Long.valueOf(0));
		}
		return map;
	}

	public List<BookingManagementDTO> sessionBookingManagement(String keyword, LocalDateTime start, LocalDateTime end,
			List<Integer> status, Pageable pageable) {
		List<CreditBookingSessionStatus> statusEnum = new ArrayList<CreditBookingSessionStatus>();
		status.forEach(el -> {
			statusEnum.add(CreditBookingSessionStatus.values()[el]);
		});
		return sessionBookingRepo.sessionBookingManagement(keyword, start, end, statusEnum, pageable);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		messageSource = new ResourceBundleMessageSource();
		((AbstractResourceBasedMessageSource) messageSource).setBasename("i18n/messages");
		((AbstractResourceBasedMessageSource) messageSource).setDefaultEncoding("utf-8");
	}
}
