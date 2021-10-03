package asia.cmg.f8.session.service;


import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import asia.cmg.f8.common.email.EmailSender;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.ScheduleEvent;
import asia.cmg.f8.session.client.FeignCommerceClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.BookingHistoryDTO;
import asia.cmg.f8.session.dto.BookingManagementDTO;
import asia.cmg.f8.session.dto.CMSSessionValidatingResponse;
import asia.cmg.f8.session.dto.ChangeBookingSessionStatusDto;
import asia.cmg.f8.session.dto.CreditBookingResponse;
import asia.cmg.f8.session.dto.CreditClassBookingDirectlyRequest;
import asia.cmg.f8.session.dto.CreditClassBookingRequest;
import asia.cmg.f8.session.dto.CreditClassBookingResponse;
import asia.cmg.f8.session.dto.CreditETicketBookingDirectlyRequest;
import asia.cmg.f8.session.dto.CreditETicketBookingRequest;
import asia.cmg.f8.session.dto.CreditETicketBookingResponse;
import asia.cmg.f8.session.dto.CreditPackageDTO;
import asia.cmg.f8.session.dto.OpeningHour;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.SpendingStatisticDTO;
import asia.cmg.f8.session.dto.cms.CMSClassBookingDTO;
import asia.cmg.f8.session.dto.cms.CMSClassCategoryBookingDTO;
import asia.cmg.f8.session.dto.cms.CMSETicketDTO;
import asia.cmg.f8.session.dto.cms.ETicketOpenTimeDTO;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.credit.BasicWalletEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditBookingTransactionEntity;
import asia.cmg.f8.session.entity.credit.CreditClassBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditETicketBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditPackageType;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditTransactionType;
import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.exception.BookingErrorCode;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.exception.OverlappedTimeBookingException;
import asia.cmg.f8.session.exception.WalletNotEnoughException;
import asia.cmg.f8.session.internal.service.CreditSessionBookingService;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.BasicWalletRepository;
import asia.cmg.f8.session.repository.CreditBookingRepository;
import asia.cmg.f8.session.repository.CreditBookingTransactionRepository;
import asia.cmg.f8.session.repository.CreditClassBookingRepository;
import asia.cmg.f8.session.utils.BookingEmail;
import asia.cmg.f8.session.utils.SessionUtil;
import asia.cmg.f8.session.wrapper.dto.CreditSessionBookingResponseDTO;


@Service
public class CreditBookingService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditBookingService.class);

	@Autowired
	private CreditClassBookingRepository creditBookingClassRepository;

	@Autowired
	private CreditBookingRepository creditBookingRepo;

	@Autowired
	private CmsService cmsService;

	@Autowired
	private BasicUserRepository userRepo;

	@Autowired
	private BasicWalletRepository walletRepo;

	@Autowired
	private CreditBookingTransactionRepository bookingTransactionRepo;

	@Autowired
	private FeignCommerceClient commerceClient;

	@Autowired
	private CreditSessionBookingService sessionBookingService;

	@Autowired
	private CreditBookingTransactionRepository creditBookingTransactionRepository;

	@Autowired
	private EventHandler eventHandler;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private BookingEmail bookingEmail;

	public final List<Integer> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
															CreditBookingSessionStatus.BURNED.ordinal());
	public final List<Integer> bookedCode = Arrays.asList(CreditBookingSessionStatus.BOOKED.ordinal());
	public final List<Integer> cancelledCode = Arrays.asList(CreditBookingSessionStatus.CANCELLED.ordinal(),
															CreditBookingSessionStatus.EU_CANCELLED.ordinal(),
															CreditBookingSessionStatus.TRAINER_CANCELLED.ordinal());
	public static final String STATUS_FIELD = "status";
	public static final String PUBLISHED = "published";
	private static final String BOOKING_EMAIL = "booking_email";
	private static final String CUSTOMER_NAME = "customer_name";
	private static final String USERNAME = "username";
	private static final String PHONE = "phone";
	private static final String BOOKING_TIME = "booking_time";
	private static final String SESSION_TIME = "session_time";
	private static final String SERVICE_TYPE = "service_type";
	private static final String CLUB_NAME = "club_name";
	private static final String LOGO_URL = "logoUrl";
	private static final String APP_STORE = "appStore";
	private static final String GOOGLE_PLAY = "googlePlay";
	private static final String DECOR = "decor";
	private static final String DIVIDER = "divider";
	private static final String CONTACT = "contact";
	
	@Autowired
	private SessionProperties sessionProperties;

	public String generateConfirmationCode(CreditBookingEntity bookingEntity) {
		String DATE_PATTERN = "yyMMdd-HHmmss";
		DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		LocalDateTime now = LocalDateTime.now();
		long bookingId = bookingEntity.getId();
		return bookingId + "-" + now.format(DATE_FORMATTER);
	}

	@Transactional
	public CreditBookingEntity checkinCourseService(long courseId, Account account) throws Exception {
		List<CreditClassBookingEntity> classBookingEntities = creditBookingClassRepository.getClassBookingsByCourseId(
				courseId,
				account.uuid(),
				Collections.singletonList(CreditBookingSessionStatus.BOOKED),
				BookingServiceType.CLASS
		);

		// TODO: Check booking
		if (CollectionUtils.isEmpty(classBookingEntities)) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		CreditBookingEntity bookingEntity = classBookingEntities.get(0).getCreditBooking();

		checkWindowTime(bookingEntity);

		CreditBookingSessionStatus oldStatus = bookingEntity.getStatus();
		CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.COMPLETED;

		bookingEntity.setStatus(newStatus);
		creditBookingRepo.save(bookingEntity);

		CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
		transaction.setAction(SessionAction.CHECK_IN);
		transaction.setCreditBooking(bookingEntity);
		transaction.setNewStatus(newStatus);
		transaction.setOldStatus(oldStatus);
		bookingTransactionRepo.save(transaction);

		deductCreditsOnCheckinCompleted(BookingServiceType.CLASS, bookingEntity);

		return bookingEntity;
	}

	@Transactional(rollbackFor = Exception.class)
	public CreditBookingEntity upgradeEticketService(String studioUuid, Account account) throws Exception {
		LocalDate bookingDay = LocalDate.now();
		List<CreditBookingEntity> bookingEntities = creditBookingRepo.getCreditBookingByStudioUuid(
															studioUuid,
															account.uuid(),
															Collections.singletonList(CreditBookingSessionStatus.BOOKED),
															BookingServiceType.ETICKET,
															bookingDay
													);

		// TODO: Check booking
		if (CollectionUtils.isEmpty(bookingEntities)) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		CreditBookingEntity bookingEntity = bookingEntities.get(0);
		CreditETicketBookingEntity eTicketBookingEntity = bookingEntity.getEtickets().get(0);

		// TODO: Check wallet valid
		BasicWalletEntity wallet = walletRepo.findByOwnerUuid(account.uuid());
		if (wallet == null || !wallet.getActive()) {
			LOG.info("Wallet is not active");
			throw new BookingValidationException(BookingErrorCode.WALLET_NOT_ACTIVE);
		}

		// TODO: Call CMS to handle logic
		CMSETicketDTO cmseTicketDTO = cmsService.getETicketAllDayDetail(studioUuid);
		if (cmseTicketDTO != null) {
			// TODO: update booking info
			int creditAmountOld = bookingEntity.getCreditAmount();
			int creditAmountNew = cmseTicketDTO.getData().getCreditAmount();

			// Validate wallet balance of client
			int creditPayAmount = creditAmountNew - creditAmountOld;
			creditPayAmount = creditPayAmount < 0 ? 0 : creditPayAmount;

			if (wallet.getAvailableCredit() < creditPayAmount) {
				LOG.info("Wallet is not enough credits");
				int lackOfAmount = creditPayAmount - wallet.getAvailableCredit();
				CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
				throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount, creditPackageDTO.getTotalPrice() * lackOfAmount);
			}

			// Subtract credit to check-in
			List<String> params = new ArrayList<>();
			Optional<BasicUserEntity> clientInfo = userRepo.findOneByUuid(account.uuid());
			clientInfo.ifPresent(basicUserEntity -> params.add(basicUserEntity.getFullName()));
			String desc = CreditTransactionType.UPGRADE_ETICKET.name() + "_" + bookingEntity.getId();
			long walletTransId = commerceClient.subtractCreditWallet(account.uuid(), bookingEntity.getId(), creditPayAmount, CreditTransactionType.UPGRADE_ETICKET.name(), desc, params);
			LOG.info("[upgradeEticket] Subtract {} credits to client {} with wallet trans id {}", creditPayAmount, account.uuid(), walletTransId);

			if (walletTransId == 0) {
				throw new Exception("Subtracting credit of client failed");
			}

			// Update status of booking & Update e-ticket to All-day type
			bookingEntity.setStatus(CreditBookingSessionStatus.COMPLETED);
			bookingEntity.setConfirmationCode(generateConfirmationCode(bookingEntity));
			eTicketBookingEntity.setAllDay(cmseTicketDTO.getData().isAllDay());
			eTicketBookingEntity.setNoShowFee(cmseTicketDTO.getData().getPayableNoShowFee() / 100);
			eTicketBookingEntity.setServiceFee(cmseTicketDTO.getData().getServiceFee() / 100);
			eTicketBookingEntity.setServiceId(cmseTicketDTO.getData().getId());
			eTicketBookingEntity.setServiceName(cmseTicketDTO.getData().getName());
			bookingEntity.setCreditAmount(creditAmountNew);
			CreditBookingEntity entity = creditBookingRepo.save(bookingEntity);

			// Recording new CreditBookingTransactionEntity with action ETICKET_UPGRADED
			CreditBookingTransactionEntity transactionUpgrade = new CreditBookingTransactionEntity();
			transactionUpgrade.setAction(SessionAction.ETICKET_UPGRADED);
			transactionUpgrade.setCreditBooking(entity);
			transactionUpgrade.setNewStatus(CreditBookingSessionStatus.BOOKED);
			transactionUpgrade.setOldStatus(CreditBookingSessionStatus.BOOKED);
			bookingTransactionRepo.save(transactionUpgrade);

			// Recording new CreditBookingTransactionEntity with action CHECK_IN
			CreditBookingTransactionEntity transactionCheckin = new CreditBookingTransactionEntity();
			transactionCheckin.setAction(SessionAction.CHECK_IN);
			transactionCheckin.setCreditBooking(entity);
			transactionCheckin.setNewStatus(CreditBookingSessionStatus.COMPLETED);
			transactionCheckin.setOldStatus(CreditBookingSessionStatus.BOOKED);
			bookingTransactionRepo.save(transactionCheckin);

			return entity;
		} else {
			throw new BookingValidationException(BookingErrorCode.CMS_NOT_RESPONSE);
		}
	}

	@Transactional
	public CreditBookingEntity checkinDirectlyEticketService(CreditETicketBookingDirectlyRequest request, Account account) throws Exception {
		String status = checkingClubStatus(request.getStudioUuid());
		if (!PUBLISHED.equalsIgnoreCase(status)) {
			throw new BookingValidationException(BookingErrorCode.NOT_PUBLISHED);
		}
		CreditBookingEntity bookingEntity;
		LocalDate bookingDay = LocalDate.now();
		List<CreditBookingEntity> bookingEntities = creditBookingRepo.getCreditBookingByStudioUuid(
				request.getStudioUuid(),
				account.uuid(),
				Collections.singletonList(CreditBookingSessionStatus.BOOKED),
				BookingServiceType.ETICKET,
				bookingDay
		);

		if (!CollectionUtils.isEmpty(bookingEntities)) {
			// TODO: Upgrade eticket flow
			bookingEntity = upgradeEticketService(request.getStudioUuid(), account);
		} else {
			// TODO: Booking eTicket for user if user no have
			CreditETicketBookingRequest eTicketBookingRequest = new CreditETicketBookingRequest();
			eTicketBookingRequest.setBookingDay(SessionUtil.getCurrentDateInMillis());
			eTicketBookingRequest.setClientUuid(account.uuid());
			eTicketBookingRequest.setEticketId(request.getEticketId());
			eTicketBookingRequest.setStudioUuid(request.getStudioUuid());
			bookingEticketService(eTicketBookingRequest, account);

			// TODO: Checkin success flow
			bookingEntity = checkinEticketService(request.getStudioUuid(), account);
		}

		return bookingEntity;
	}

	@Transactional
	public CreditBookingEntity checkinDirectlyClassService(CreditClassBookingDirectlyRequest request, Account account) throws Exception {
		// TODO: New booking class
		CreditClassBookingRequest bookingRequest = new CreditClassBookingRequest();
		bookingRequest.setClassId(request.getClassId());
		bookingRequest.setClientUuid(account.uuid());
		bookingRequest.setStudioUuid(request.getStudioUuid());
		bookingRequest.setBookingDay(SessionUtil.getCurrentDateInMillis());

		CreditBookingEntity bookingEntity = bookingClassService(bookingRequest, account);

		// TODO: Checkin success flow
		long serviceId = bookingEntity.getClasses().get(0).getServiceId();
		CreditBookingEntity bookingEntityReponse = checkinClassService(serviceId, bookingEntity.getStudioUuid(), account);

		return bookingEntityReponse;
	}

	@Transactional
	public CreditBookingEntity checkinEticketService(String studioUuid, Account account) throws Exception {
		String status = checkingClubStatus(studioUuid);
		if (!PUBLISHED.equalsIgnoreCase(status)) {
			throw new BookingValidationException(BookingErrorCode.NOT_PUBLISHED);
		}
		LocalDate bookingDay = LocalDate.now();
		List<CreditBookingEntity> bookingEntities = creditBookingRepo.getCreditBookingByStudioUuid(
																		studioUuid,
																		account.uuid(),
																		Collections.singletonList(CreditBookingSessionStatus.BOOKED),
																		BookingServiceType.ETICKET,
																		bookingDay
																);

		// TODO: Check booking
		if (CollectionUtils.isEmpty(bookingEntities)) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		CreditBookingEntity bookingEntity = bookingEntities.get(0);
		CreditETicketBookingEntity eTicketBookingEntity = bookingEntity.getEtickets().get(0);

		// TODO: Check window time
		boolean windowTimeValid = eTicketBookingEntity.isAllDay();

		if (!windowTimeValid && StringUtils.isNotEmpty(eTicketBookingEntity.getOpeningHours())) {
			long now = System.currentTimeMillis();
			OpeningHour openingHour = (OpeningHour)SessionUtil.toObjectList(eTicketBookingEntity.getOpeningHours(), OpeningHour.class);
			List<ETicketOpenTimeDTO> openingTimeSlots = openingHour.getOpeningHours();
			for (ETicketOpenTimeDTO openingTimeSlot : openingTimeSlots) {
				long startTime = openingTimeSlot.getFrom();
				long endTime = openingTimeSlot.getTo();
				if(now >= startTime && now <= endTime) {
					windowTimeValid = true;
					break;
				}
			}
		}

		if (!windowTimeValid) {
			CMSETicketDTO cmseTicketAllday = cmsService.getETicketAllDayDetail(studioUuid);
			if(cmseTicketAllday == null) {
				throw new BookingValidationException(BookingErrorCode.CMS_NOT_RESPONSE);
			}
			int payExtendedCredit = cmseTicketAllday.getData().getCreditAmount() - bookingEntity.getCreditAmount() <= 0 ? 0 : cmseTicketAllday.getData().getCreditAmount() - bookingEntity.getCreditAmount();

			CreditBookingResponse<CreditETicketBookingResponse> eticketBookingResponse = new CreditBookingResponse<CreditETicketBookingResponse>();
			CreditETicketBookingResponse eticketBooking = new CreditETicketBookingResponse(bookingEntity);
			eticketBookingResponse.setServiceType(BookingServiceType.ETICKET);
			eticketBookingResponse.setBookingData(eticketBooking);
			eticketBookingResponse.setPayExtendedCredit(payExtendedCredit);

			throw new BookingValidationException(BookingErrorCode.INVALID_CHECKIN_TIME, eticketBookingResponse);
		}

		CreditBookingSessionStatus oldStatus = bookingEntity.getStatus();
		CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.COMPLETED;

		bookingEntity.setStatus(newStatus);
		bookingEntity.setConfirmationCode(generateConfirmationCode(bookingEntity));
		creditBookingRepo.save(bookingEntity);

		CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
		transaction.setAction(SessionAction.CHECK_IN);
		transaction.setCreditBooking(bookingEntity);
		transaction.setNewStatus(newStatus);
		transaction.setOldStatus(oldStatus);
		bookingTransactionRepo.save(transaction);

		deductCreditsOnCheckinCompleted(BookingServiceType.ETICKET, bookingEntity);

		return bookingEntity;
	}

	@Transactional
	public CreditBookingEntity checkinClassService(long serviceId, String studioUuid, Account account) throws Exception {
		String status = checkingClassStatus(studioUuid, serviceId);
		if (!PUBLISHED.equalsIgnoreCase(status)) {
			throw new BookingValidationException(BookingErrorCode.NOT_PUBLISHED);
		}
		List<CreditClassBookingEntity> classBookingEntities = creditBookingClassRepository.getClassBookingsByServiceIdAndStudioUuid(
																		serviceId,
																		studioUuid,
																		account.uuid(),
																		Collections.singletonList(CreditBookingSessionStatus.BOOKED),
																		BookingServiceType.CLASS
																);

		// TODO: Check booking
		if (CollectionUtils.isEmpty(classBookingEntities)) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		CreditBookingEntity bookingEntity = classBookingEntities.get(0).getCreditBooking();

		checkWindowTime(bookingEntity);

		CreditBookingSessionStatus oldStatus = bookingEntity.getStatus();
		CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.COMPLETED;

		bookingEntity.setStatus(newStatus);
		bookingEntity.setConfirmationCode(generateConfirmationCode(bookingEntity));
		creditBookingRepo.save(bookingEntity);

		CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
		transaction.setAction(SessionAction.CHECK_IN);
		transaction.setCreditBooking(bookingEntity);
		transaction.setNewStatus(newStatus);
		transaction.setOldStatus(oldStatus);
		bookingTransactionRepo.save(transaction);

		deductCreditsOnCheckinCompleted(BookingServiceType.CLASS, bookingEntity);

		return bookingEntity;
	}

	private void deductCreditsOnCheckinCompleted(BookingServiceType bookingServiceType, CreditBookingEntity bookingEntity) throws Exception {
		List<String> params = new ArrayList<>();
		String desc = "";
		switch (bookingServiceType) {
			case CLASS:
				desc = CreditTransactionType.PAY_COMPLETED_CLASS + "_" + bookingEntity.getId();
				CreditClassBookingEntity classBookingEntity = bookingEntity.getClasses().get(0);

				Long studioClassPaidCredit = Math.round(bookingEntity.getCreditAmount() * (1 - classBookingEntity.getServiceFee()) / sessionProperties.getVat() * (1 - sessionProperties.getPit()));
				long studioClassPaidCreditResult = commerceClient.plusCreditWallet(bookingEntity.getStudioUuid(), bookingEntity.getId(), studioClassPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_CLASS.name(), desc, params);
				LOG.info("[bookingClassDeduct] Plus {} credits to Studio {} with wallet trans id {}", studioClassPaidCredit, bookingEntity.getStudioUuid(), studioClassPaidCreditResult);

				Long leepClassPaidCredit = Math.round(bookingEntity.getCreditAmount() * classBookingEntity.getServiceFee());
				long leepClassPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), leepClassPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_CLASS.name(), desc, params);
				LOG.info("[bookingClassDeduct] Plus {} credits to LEEP {} with wallet trans id {}", leepClassPaidCredit, sessionProperties.getLeepAccountUuid(), leepClassPaidCreditResult);

				// If plus successfully then record more deducted transaction
				if(studioClassPaidCreditResult == 0) {
					throw new Exception("Plus credit to studio failed");
				}

				break;
			case ETICKET:
				desc = CreditTransactionType.PAY_COMPLETED_ETICKET + "_" + bookingEntity.getId();
				CreditETicketBookingEntity eTicketBookingEntity = bookingEntity.getEtickets().get(0);

				Long studioEticketPaidCredit = Math.round(bookingEntity.getCreditAmount() * (1 - eTicketBookingEntity.getServiceFee()) / sessionProperties.getVat() * (1 - sessionProperties.getPit()));
				long studioEticketPaidCreditResult = commerceClient.plusCreditWallet(bookingEntity.getStudioUuid(), bookingEntity.getId(), studioEticketPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_ETICKET.name(), desc, params);
				LOG.info("[bookingEticketDeduct] Plus {} credits to Studio {} with wallet trans id {}", studioEticketPaidCredit, bookingEntity.getStudioUuid(), studioEticketPaidCreditResult);

				Long leepEticketPaidCredit = Math.round(bookingEntity.getCreditAmount() * eTicketBookingEntity.getServiceFee());
				long leepEticketPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), leepEticketPaidCredit.intValue(), CreditTransactionType.PAY_COMPLETED_ETICKET.name(), desc, params);
				LOG.info("[bookingEticketDeduct] Plus {} credits to LEEP {} with wallet trans id {}", leepEticketPaidCredit, sessionProperties.getLeepAccountUuid(), leepEticketPaidCreditResult);

				// If plus successfully then record more deducted transaction
				if(studioEticketPaidCreditResult == 0) {
					throw new Exception("Plus credit to studio failed");
				}

				break;
			default:
				throw new Exception("BookingServiceType not handle");
		}

		CreditBookingTransactionEntity bookingTransaction = new CreditBookingTransactionEntity();
		bookingTransaction.setAction(SessionAction.CHECK_IN);
		bookingTransaction.setCreditBooking(bookingEntity);
		bookingTransaction.setNewStatus(CreditBookingSessionStatus.DEDUCTED);
		bookingTransaction.setOldStatus(bookingEntity.getStatus());
		bookingTransactionRepo.save(bookingTransaction);
	}

	@Transactional
	public void handleAutoBurningEticketBookingsJob() throws Exception {
		List<CreditBookingEntity> entities = creditBookingRepo.getCreditBookingByStatusOverEndTime(CreditBookingSessionStatus.BOOKED.ordinal(), BookingServiceType.ETICKET.ordinal());

		for (CreditBookingEntity bookingEntity : entities) {
			try {
				CreditETicketBookingEntity eTicketBookingEntity = bookingEntity.getEtickets().get(0);
				List<String> params = new ArrayList<>();
				String desc = CreditTransactionType.PAY_BURNED_ETICKET + "_" + bookingEntity.getId();

				if (eTicketBookingEntity.getNoShowFee() != null) {
					Long studioPaidCredit = Math.round(bookingEntity.getCreditAmount() * eTicketBookingEntity.getNoShowFee() / sessionProperties.getVat() * (1 - sessionProperties.getPit()));
					long studioPaidCreditResult = commerceClient.plusCreditWallet(bookingEntity.getStudioUuid(), bookingEntity.getId(), studioPaidCredit.intValue(), CreditTransactionType.PAY_BURNED_ETICKET.name(), desc, params);
					LOG.info("Plus {} credits to Studio {} with wallet trans id {}", studioPaidCredit, bookingEntity.getStudioUuid(), studioPaidCreditResult);

					Long leepPaidCredit = Math.round(bookingEntity.getCreditAmount() * (1 - eTicketBookingEntity.getNoShowFee()));
					long leepPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), leepPaidCredit.intValue(), CreditTransactionType.PAY_BURNED_ETICKET.name(), desc, params);
					LOG.info("Plus {} credits to LEEP {} with wallet trans id {}", leepPaidCredit, sessionProperties.getLeepAccountUuid(), leepPaidCreditResult);

				} else {
					// Process plus 100% credit for LEEP
					long leepPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), bookingEntity.getCreditAmount(), CreditTransactionType.PAY_BURNED_ETICKET.name(), desc, params);
					LOG.info("Plus {} credits to LEEP {} with wallet trans id {}", bookingEntity.getCreditAmount(), sessionProperties.getLeepAccountUuid(), leepPaidCreditResult);
				}
					
				bookingEntity.setStatus(CreditBookingSessionStatus.BURNED);
				creditBookingRepo.save(bookingEntity);

				CreditBookingTransactionEntity creditTransaction = new CreditBookingTransactionEntity();
				creditTransaction.setAction(SessionAction.AUTO_BURNED);
				creditTransaction.setCreditBooking(bookingEntity);
				creditTransaction.setNewStatus(CreditBookingSessionStatus.BURNED);
				creditTransaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
				bookingTransactionRepo.save(creditTransaction);

				CreditBookingTransactionEntity bookingTransaction = new CreditBookingTransactionEntity();
				bookingTransaction.setAction(SessionAction.AUTO_DEDUCTED);
				bookingTransaction.setCreditBooking(bookingEntity);
				bookingTransaction.setNewStatus(CreditBookingSessionStatus.DEDUCTED);
				bookingTransaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
				bookingTransactionRepo.save(bookingTransaction);
			} catch (Exception e) {
				LOG.error("Handle auto burning e-ticket failed for booking id: {}", bookingEntity.getId());
			}
		}
	}

	@Transactional
	public void handleAutoBurningClassBookingsJob() throws Exception {
		List<CreditBookingEntity> entities = creditBookingRepo.getCreditBookingByStatusOverEndTime(CreditBookingSessionStatus.BOOKED.ordinal(), BookingServiceType.CLASS.ordinal());

		for (CreditBookingEntity bookingEntity : entities) {
			try {
				CreditClassBookingEntity classBookingEntity = bookingEntity.getClasses().get(0);
				List<String> params = new ArrayList<>();
				String desc = CreditTransactionType.PAY_BURNED_CLASS + "_" + bookingEntity.getId();

				if (classBookingEntity.getNoShowFee() > 0) {
					Long studioPaidCredit = Math.round(bookingEntity.getCreditAmount() * classBookingEntity.getNoShowFee() / sessionProperties.getVat() * (1 - sessionProperties.getPit()));
					long studioPaidCreditResult = commerceClient.plusCreditWallet(bookingEntity.getStudioUuid(), bookingEntity.getId(), studioPaidCredit.intValue(), CreditTransactionType.PAY_BURNED_CLASS.name(), desc, params);
					LOG.info("Plus {} credits to Studio {} with wallet trans id {}", studioPaidCredit, bookingEntity.getStudioUuid(), studioPaidCreditResult);

					Long leepPaidCredit = Math.round(bookingEntity.getCreditAmount() * (1 - classBookingEntity.getNoShowFee()));
					long leepPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), leepPaidCredit.intValue(), CreditTransactionType.PAY_BURNED_CLASS.name(), desc, params);
					LOG.info("Plus {} credits to LEEP {} with wallet trans id {}", leepPaidCredit, sessionProperties.getLeepAccountUuid(), leepPaidCreditResult);
				} else {
					// Process plus 100% credit for LEEP
					long leepPaidCreditResult = commerceClient.plusCreditWallet(sessionProperties.getLeepAccountUuid(), bookingEntity.getId(), bookingEntity.getCreditAmount(), CreditTransactionType.PAY_BURNED_CLASS.name(), desc, params);
					LOG.info("[bookingCancelAfter3h] Plus {} credits to LEEP {} with wallet trans id {}", bookingEntity.getCreditAmount(), sessionProperties.getLeepAccountUuid(), leepPaidCreditResult);
				}

				bookingEntity.setStatus(CreditBookingSessionStatus.BURNED);
				creditBookingRepo.save(bookingEntity);

				CreditBookingTransactionEntity creditTransaction = new CreditBookingTransactionEntity();
				creditTransaction.setAction(SessionAction.AUTO_BURNED);
				creditTransaction.setCreditBooking(bookingEntity);
				creditTransaction.setNewStatus(CreditBookingSessionStatus.BURNED);
				creditTransaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
				bookingTransactionRepo.save(creditTransaction);

				CreditBookingTransactionEntity bookingTransaction = new CreditBookingTransactionEntity();
				bookingTransaction.setAction(SessionAction.AUTO_DEDUCTED);
				bookingTransaction.setCreditBooking(bookingEntity);
				bookingTransaction.setNewStatus(CreditBookingSessionStatus.DEDUCTED);
				bookingTransaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
				bookingTransactionRepo.save(bookingTransaction);
			} catch (Exception e) {
				LOG.error("Handle auto burning class failed for booking id: {}", bookingEntity.getId());
			}
		}
	}

	public boolean handleCancelSessionBooking(long bookingId, Account account) throws Exception{
		List<Integer> status = Arrays.asList(CreditBookingSessionStatus.BOOKED.ordinal(),
												CreditBookingSessionStatus.CONFIRMED.ordinal());

		try {
			CreditBookingEntity bookedEntity = creditBookingRepo.getClientBookingByIdAndTypeAndStatusIn(bookingId, account.uuid(), BookingServiceType.SESSION.ordinal(), status);
			if(bookedEntity == null) {
				LOG.info("Handle cancel session booking id {} not existed", bookingId);
				return false;
			}
			CreditBookingSessionStatus oldStatus = bookedEntity.getStatus();
			ChangeBookingSessionStatusDto bookingSessionDto = sessionBookingService.handleUpdateSessionStatus(bookedEntity.getId(), ClientActions.CANCEL, account);
			if (bookingSessionDto.getStatus() == oldStatus) {
				throw new BookingValidationException(BookingErrorCode.DISALLOW_CANCEL_BOOKING);
			}
			return true;
		} catch(BookingValidationException ex) {
			throw ex;
		} catch (Exception e) {
			LOG.info("Handle cancel session booking id {} with failed {}", bookingId, e.getMessage());
			return false;
		}
	}

	@Transactional
	public CreditBookingEntity handelCancelEticketBooking(long bookingId, String clientUuid) throws Exception {
		List<Integer> status = Arrays.asList(CreditBookingSessionStatus.BOOKED.ordinal());
		CreditBookingEntity bookedEntity = creditBookingRepo.getClientBookingByIdAndTypeAndStatusIn(bookingId, clientUuid, BookingServiceType.ETICKET.ordinal(), status);

		if(bookedEntity == null) {
			LOG.info("Handle cancel e-ticket booking id {} not existed", bookingId);
			throw new Exception("Booking id not found");
		}

//		Eticket can be deleted any time on day.
//		LocalDateTime now = LocalDateTime.now();
//		if (bookedEntity.getEndTime().isBefore(now)) {
//			LOG.info("Handle cancel e-ticket booking id {} not allowed", bookingId);
//			throw new BookingValidationException(BookingErrorCode.DISALLOW_CANCEL_BOOKING);
//		}

		// Refund wallet
		List<String> params = new ArrayList<>();
		String desc = CreditTransactionType.REFUND_CANCEL_ETICKET.name() + "_" + bookedEntity.getId();
		long walletTransId = commerceClient.plusCreditWallet(bookedEntity.getClientUuid(), bookedEntity.getId(), bookedEntity.getCreditAmount(),
				CreditTransactionType.REFUND_CANCEL_ETICKET.name(),
				desc, params);

		if (walletTransId == 0) {
			throw new Exception("Refund credit to client failed");
		}

		CreditBookingSessionStatus oldStatus = bookedEntity.getStatus();
		CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.CANCELLED;

		bookedEntity.setStatus(newStatus);
		creditBookingRepo.save(bookedEntity);

		CreditBookingTransactionEntity bookingTransaction = new CreditBookingTransactionEntity();
		bookingTransaction.setAction(SessionAction.CANCEL);
		bookingTransaction.setCreditBooking(bookedEntity);
		bookingTransaction.setNewStatus(newStatus);
		bookingTransaction.setOldStatus(oldStatus);
		bookingTransactionRepo.save(bookingTransaction);

		return bookedEntity;
	}

	public boolean handleCancelOverlappedBooking(long bookingId, Account client) throws Exception {
		try {
			CreditBookingEntity bookingEntity = creditBookingRepo.getBookingByIdAndClient(bookingId, client.uuid());

			if(bookingEntity == null) {
				LOG.info("Overlapped booking id not found: {}", bookingId);
				return false;
			}

			switch (bookingEntity.getBookingType()) {
				case SESSION:
					return this.handleCancelSessionBooking(bookingId, client);
				case ETICKET:
					this.handelCancelEticketBooking(bookingId, client.uuid());
					break;
				case CLASS:
					this.handelCancelClassBooking(bookingId, client.uuid());
					break;
				default:
					break;
			}
		} catch (BookingValidationException ex) {
			throw ex;
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Transactional
	public CreditBookingEntity handelCancelClassBooking(long bookingId, String clientUuid) throws Exception {
		long requireHours = 3;
		List<Integer> status = Arrays.asList(CreditBookingSessionStatus.BOOKED.ordinal());
		CreditBookingEntity bookedEntity = creditBookingRepo.getClientBookingByIdAndTypeAndStatusIn(bookingId, clientUuid, BookingServiceType.CLASS.ordinal(), status);

		if(bookedEntity == null) {
			LOG.info("Handle cancel class booking id {} not existed", bookingId);
			throw new Exception("Booking id not found");
		}

		if (bookedEntity.getStartTime().isBefore(LocalDateTime.now().plusHours(requireHours))) {
			LOG.info("Handle cancel class booking id {} not allowed", bookingId);
			throw new BookingValidationException(BookingErrorCode.DISALLOW_CANCEL_BOOKING);
		}

		// Process refund with cancel before 3h
		List<String> params = new ArrayList<>();
		String desc = CreditTransactionType.REFUND_CANCEL_CLASS.name() + "_" + bookedEntity.getId();
		long walletTransId = commerceClient.plusCreditWallet(bookedEntity.getClientUuid(), bookedEntity.getId(), bookedEntity.getCreditAmount(),
				CreditTransactionType.REFUND_CANCEL_CLASS.name(),
				desc, params);

		// If refund successfully then record CANCELED transaction
		if(walletTransId != 0) {
			CreditBookingSessionStatus oldStatus = bookedEntity.getStatus();
			CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.CANCELLED;

			// Update Booking status
			bookedEntity.setStatus(newStatus);
			creditBookingRepo.save(bookedEntity);

			// Record the CANCELED booking transaction
			CreditBookingTransactionEntity canceledTransaction = new CreditBookingTransactionEntity();
			canceledTransaction.setAction(SessionAction.CANCEL);
			canceledTransaction.setCreditBooking(bookedEntity);
			canceledTransaction.setNewStatus(newStatus);
			canceledTransaction.setOldStatus(oldStatus);
			bookingTransactionRepo.save(canceledTransaction);
		} else {
			throw new Exception("Refund credit to client failed");
		}

		return bookedEntity;
	}

	public List<CreditBookingResponse<?>> getScheduledBookings(final Account account, final long startTime, final long endTime) {
		List<CreditBookingResponse<?>> result = new ArrayList<CreditBookingResponse<?>>();
		try {
			LocalDate fromDate = SessionUtil.convertToLocalDate(startTime);
			LocalDate toDate = SessionUtil.convertToLocalDate(endTime);

			List<CreditBookingEntity> bookingEntities = new ArrayList<CreditBookingEntity>();
			if (account.isPt()) {
				bookingEntities = creditBookingRepo.getPTScheduledBookings(fromDate, toDate, account.uuid());
			} else {
				bookingEntities = creditBookingRepo.getEUScheduledBookings(fromDate, toDate, account.uuid());
			}

			for (CreditBookingEntity creditBookingEntity : bookingEntities) {
				switch (creditBookingEntity.getBookingType()) {
				case CLASS:
					CreditBookingResponse<CreditClassBookingResponse> classBookingResponse = new CreditBookingResponse<CreditClassBookingResponse>();
					CreditClassBookingResponse classBooking = new CreditClassBookingResponse(creditBookingEntity);
					classBookingResponse.setServiceType(BookingServiceType.CLASS);
					classBookingResponse.setBookingData(classBooking);

					result.add(classBookingResponse);
					break;
				case ETICKET:
					CreditBookingResponse<CreditETicketBookingResponse> eticketBookingResponse = new CreditBookingResponse<CreditETicketBookingResponse>();
					CreditETicketBookingResponse eticketBooking = new CreditETicketBookingResponse(creditBookingEntity);
					eticketBookingResponse.setServiceType(BookingServiceType.ETICKET);
					eticketBookingResponse.setBookingData(eticketBooking);

					result.add(eticketBookingResponse);
					break;
				case SESSION:
					BasicUserInfo userInfo = null;
					BasicUserInfo trainerInfo = null;
					CreditSessionBookingEntity session = creditBookingEntity.getSessions().get(0);
					BasicUserEntity userEntity = userRepo.findOneByUuid(session.getUserUuid()).get();
					if (userEntity != null) {
						userInfo = BasicUserInfo.convertFromEntity(userEntity);
					}
					BasicUserEntity trainerEntity = userRepo.findOneByUuid(session.getPtUuid()).get();
					if (trainerEntity != null) {
						trainerInfo = BasicUserInfo.convertFromEntity(trainerEntity);
					}
					CreditSessionBookingResponseDTO sessionBookingDTO = new CreditSessionBookingResponseDTO(
							creditBookingEntity, userInfo, trainerInfo);
					CreditBookingResponse<CreditSessionBookingResponseDTO> sessionBookingResponse = new CreditBookingResponse<CreditSessionBookingResponseDTO>();
					sessionBookingResponse.setServiceType(BookingServiceType.SESSION);
					sessionBookingResponse.setBookingData(sessionBookingDTO);

					result.add(sessionBookingResponse);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public CreditBookingEntity bookingEticketService(CreditETicketBookingRequest bookingRequest, Account account)
			throws Exception {
		try {
			CMSETicketDTO cmsEticketDetail = cmsService.getETicketDetail(bookingRequest.getEticketId(), bookingRequest.getBookingDay());
			if (cmsEticketDetail != null) {
				CreditBookingEntity bookingEntity = new CreditBookingEntity();
				CreditETicketBookingEntity eTicketBookingEntity = new CreditETicketBookingEntity();

				// Validating e-ticket booking conditions
				this.validateEticketBooking(bookingRequest, cmsEticketDetail);

				// Setting start_time, end_time, opening_hours for e-ticket booking
				this.setEticketUsingTime(bookingEntity, eTicketBookingEntity, cmsEticketDetail, bookingRequest.getBookingDay());

				// Setting data for CreditETicketBookingEntity
				eTicketBookingEntity.setAllDay(cmsEticketDetail.getData().isAllDay());
				eTicketBookingEntity.setNoShowFee(cmsEticketDetail.getData().getPayableNoShowFee() / 100);
				eTicketBookingEntity.setServiceFee(cmsEticketDetail.getData().getServiceFee() / 100);
				eTicketBookingEntity.setServiceId(cmsEticketDetail.getData().getId());
				eTicketBookingEntity.setServiceName(cmsEticketDetail.getData().getName());

				// Setting data for CreditBookingEntity
				bookingEntity.setBookedBy(account.uuid());
				bookingEntity.setBookingDay(SessionUtil.convertToLocalDate(bookingRequest.getBookingDay()));
				bookingEntity.setBookingType(BookingServiceType.ETICKET);
				bookingEntity.setEtickets(Arrays.asList(eTicketBookingEntity));
				bookingEntity.setClientUuid(bookingRequest.getClientUuid());
				bookingEntity.setCreditAmount(cmsEticketDetail.getData().getCreditAmount());
				bookingEntity.setStatus(CreditBookingSessionStatus.BOOKED);
				bookingEntity.setStudioAddress(cmsEticketDetail.getData().getStudio().getAddress());
				bookingEntity.setStudioName(cmsEticketDetail.getData().getStudio().getName());
				bookingEntity.setStudioUuid(cmsEticketDetail.getData().getStudio().getId());
				bookingEntity.setStudioPicture(cmsEticketDetail.getData().getStudio().getLogo_link());
				bookingEntity.setStudioCover(cmsEticketDetail.getData().getStudio().getImage_link());
				creditBookingRepo.save(bookingEntity);

				// Subtract credit
	            List<String> params = Collections.singletonList(cmsEticketDetail.getData().getStudio().getName());

	            String desc = CreditTransactionType.BOOKING_ETICKET.name() + "_" + bookingEntity.getId();
	            long walletTransId = commerceClient.subtractCreditWallet(bookingEntity.getClientUuid(), bookingEntity.getId(), bookingEntity.getCreditAmount(), CreditTransactionType.BOOKING_ETICKET.name(), desc, params);
	            LOG.info("[bookingEticket] Subtract {} credits to client {} with wallet trans id {}", bookingEntity.getCreditAmount(), bookingEntity.getClientUuid(), walletTransId);

	            if(walletTransId == 0) {
	            	throw new Exception("Subtracting credit of client failed");
	            }

                // Saving CreditSessionBookingTransactionEntity
                CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
                transaction.setAction(SessionAction.BOOKED);
                transaction.setCreditBooking(bookingEntity);
                transaction.setNewStatus(CreditBookingSessionStatus.BOOKED);
                transaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
                bookingTransactionRepo.save(transaction);
                Boolean checkFirstBooking = this.checkFirstBooking(account.uuid(), BookingServiceType.ETICKET);
                if (checkFirstBooking) {
                	this.sendEmailToFinance(bookingEntity);
                }   
				return bookingEntity;
			} else {
				throw new Exception("Could not get CMS Class Booking detail");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional(rollbackFor = {Exception.class})
	public CreditBookingEntity bookingClassService(CreditClassBookingRequest bookingRequest, Account account) throws Exception {
		try {
			CMSClassBookingDTO cmsClassDetail = cmsService.getClassDetail(bookingRequest.getStudioUuid(), bookingRequest.getClassId());
			if (cmsClassDetail != null) {

				// Validate class booking conditions
				this.validateClassBooking(bookingRequest, cmsClassDetail);

				CreditClassBookingEntity classBookingEntity = new CreditClassBookingEntity();
				classBookingEntity.setNoShowFee(cmsClassDetail.getData().getCourse().getNoShowFeePercent() / 100);
				classBookingEntity.setServiceFee(cmsClassDetail.getData().getCourse().getServiceFeePercent() / 100);
				classBookingEntity.setServiceId(cmsClassDetail.getData().getId());
				classBookingEntity.setServiceName(cmsClassDetail.getData().getName());
				classBookingEntity.setCourseId(cmsClassDetail.getData().getCourse().getId());

				CreditBookingEntity bookingEntity = new CreditBookingEntity();
				bookingEntity.setStartTime(SessionUtil.convertToLocalDateTime(cmsClassDetail.getData().getStartTime()));
				bookingEntity.setEndTime(SessionUtil.convertToLocalDateTime(cmsClassDetail.getData().getEndTime()));
				bookingEntity.setBookedBy(account.uuid());
				bookingEntity.setBookingDay(SessionUtil.convertToLocalDate(bookingRequest.getBookingDay()));
				bookingEntity.setBookingType(BookingServiceType.CLASS);
				bookingEntity.setClasses(Arrays.asList(classBookingEntity));
				bookingEntity.setClientUuid(bookingRequest.getClientUuid());
				bookingEntity.setCreditAmount(cmsClassDetail.getData().getCreditAmount());
				bookingEntity.setStatus(CreditBookingSessionStatus.BOOKED);
				bookingEntity.setStudioAddress(cmsClassDetail.getData().getStudio().getAddress());
				bookingEntity.setStudioName(cmsClassDetail.getData().getStudio().getName());
				bookingEntity.setStudioUuid(cmsClassDetail.getData().getStudio().getId());
				bookingEntity.setStudioPicture(cmsClassDetail.getData().getStudio().getLogo_link());
				bookingEntity.setStudioCover(cmsClassDetail.getData().getStudio().getImage_link());

				creditBookingRepo.save(bookingEntity);

				// Subtracting client's credits
				List<String> params = Collections.singletonList(cmsClassDetail.getData().getName());
				String desc = CreditTransactionType.BOOKING_CLASS.name() + "_" + bookingEntity.getId();

				long walletTransId = commerceClient.subtractCreditWallet(bookingEntity.getClientUuid(), bookingEntity.getId(), bookingEntity.getCreditAmount(), CreditTransactionType.BOOKING_CLASS.name(), desc, params);
				if(walletTransId == 0) {
					throw new Exception("Subtracting credit of client failed");
				}
				LOG.info("[bookingClass] Subtract {} credits to client {} with wallet trans id {}", bookingEntity.getCreditAmount(), bookingEntity.getClientUuid(), walletTransId);

				// Saving CreditSessionBookingTransactionEntity
				CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
				transaction.setAction(SessionAction.BOOKED);
				transaction.setCreditBooking(bookingEntity);
				transaction.setNewStatus(CreditBookingSessionStatus.BOOKED);
				transaction.setOldStatus(CreditBookingSessionStatus.BOOKED);
				bookingTransactionRepo.save(transaction);
				Boolean checkFisrtBooking = this.checkFirstBooking(account.uuid(), BookingServiceType.CLASS);
				if (checkFisrtBooking) {
					this.sendEmailToFinance(bookingEntity);
				}	
				return bookingEntity;
			} else {
				throw new Exception("Could not get CMS Class Booking detail");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void validateEticketBooking(CreditETicketBookingRequest bookingRequest, CMSETicketDTO cmsEticketDetail) {
		LocalDate today = SessionUtil.convertToLocalDate(System.currentTimeMillis());
		LocalDate bookingDay = SessionUtil.convertToLocalDate(bookingRequest.getBookingDay());

		// Validate bookingDay not in the past
		if (bookingDay.isBefore(today)) {
			LOG.info("Booking day is not valid: {}", bookingDay);
			throw new BookingValidationException(BookingErrorCode.NOT_VALID_TIME_SLOT);
		}

		// Validate off-pick time not in the past
		if(!cmsEticketDetail.getData().isAllDay()) {
			List<ETicketOpenTimeDTO> timeSlots = cmsEticketDetail.getData().getOpenTimes();
			if(!timeSlots.isEmpty()) {
				long lastEndtime = 0;
				for (ETicketOpenTimeDTO timeSlot : timeSlots) {
					if(timeSlot.getTo() > lastEndtime) {
						lastEndtime = timeSlot.getTo();
					}
				}
				if(lastEndtime <= System.currentTimeMillis()) {
					LOG.info("The last time slot is not valid: {}", lastEndtime);
					throw new BookingValidationException(BookingErrorCode.NOT_VALID_TIME_SLOT);
				}
			}
		}

		// Validate wallet balance of client
        BasicWalletEntity wallet = walletRepo.findByOwnerUuid(bookingRequest.getClientUuid());
        int creditAmount = cmsEticketDetail.getData().getCreditAmount();
        if (wallet == null) {
        	LOG.info("Client {} does not have the wallet", bookingRequest.getClientUuid());
        	CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
            int lackOfAmount = creditAmount;
            throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount, creditPackageDTO.getTotalPrice() * lackOfAmount);
        }

        if (wallet.getAvailableCredit() < creditAmount) {
            LOG.info("Wallet is not enough credits: {} credits", wallet.getAvailableCredit());
            int lackOfAmount = creditAmount - wallet.getAvailableCredit();
            CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
            throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount, creditPackageDTO.getTotalPrice() * lackOfAmount);
        }

        // Validate On the day, user only holds ONE club access
        List<CreditBookingEntity> creditBookingEntities = creditBookingRepo
        						.getCreditBookingByBookingDay(bookingRequest.getClientUuid(), CreditBookingSessionStatus.BOOKED.ordinal(), bookingDay, BookingServiceType.ETICKET.ordinal());
        if (!creditBookingEntities.isEmpty()) {
        	LOG.info("Client already has e-ticket booking before with booking id: {}", creditBookingEntities.get(0).getId());

        	CreditBookingResponse<CreditETicketBookingResponse> eticketBookingResponse = new CreditBookingResponse<CreditETicketBookingResponse>();
			CreditETicketBookingResponse eticketBooking = new CreditETicketBookingResponse(creditBookingEntities.get(0));
			eticketBookingResponse.setServiceType(BookingServiceType.ETICKET);
			eticketBookingResponse.setBookingData(eticketBooking);
            throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, eticketBookingResponse);
        }
	}

	private void validateClassBooking(CreditClassBookingRequest bookingRequest, CMSClassBookingDTO cmsClassDetail) {
		LocalDate today = SessionUtil.convertToLocalDate(System.currentTimeMillis());
		LocalDate bookingDay = SessionUtil.convertToLocalDate(bookingRequest.getBookingDay());

		// Validate bookingDay
		if (bookingDay.isBefore(today)) {
			LOG.info("Booking day is not valid: {}", bookingDay);
			throw new BookingValidationException(BookingErrorCode.NOT_VALID_TIME_SLOT);
		}

		// Validate the start time of class not in the past
		Long startTime = cmsClassDetail.getData().getStartTime();
		if(startTime < System.currentTimeMillis()) {
			LOG.info("The start time of class is not valid: {}", startTime);
			throw new BookingValidationException(BookingErrorCode.NOT_VALID_TIME_SLOT);
		}

		// Validate the booking overlap time
		CreditBookingEntity overlappedEntity = validateBookingOverlapTime(bookingRequest.getClientUuid(), cmsClassDetail.getData().getStartTime(), cmsClassDetail.getData().getEndTime());
		if (overlappedEntity != null) {
			LOG.info("Overlapped time with booking id before: {}", overlappedEntity.getId());

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
				CreditSessionBookingResponseDTO sessionBookingDTO = new CreditSessionBookingResponseDTO(overlappedEntity, userInfo, trainerInfo);
				CreditBookingResponse<CreditSessionBookingResponseDTO> sessionBookingResponse = new CreditBookingResponse<CreditSessionBookingResponseDTO>();
				sessionBookingResponse.setServiceType(BookingServiceType.SESSION);
				sessionBookingResponse.setBookingData(sessionBookingDTO);

				throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, sessionBookingResponse);
			default:
				throw new OverlappedTimeBookingException(BookingErrorCode.OVERLAP_BOOKING, null);
			}
		}

		// Validate wallet balance of client
		BasicWalletEntity wallet = walletRepo.findByOwnerUuid(bookingRequest.getClientUuid());
		int creditAmount = cmsClassDetail.getData().getCreditAmount();
		if (wallet == null) {
			LOG.info("Client {} does not have the wallet", bookingRequest.getClientUuid());
			CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
			int lackOfAmount = creditAmount;
			throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount, creditPackageDTO.getTotalPrice() * lackOfAmount);
		}

		if (wallet.getAvailableCredit() < creditAmount) {
			LOG.info("Wallet is not enough credits: {} credits", wallet.getAvailableCredit());
			int lackOfAmount = creditAmount - wallet.getAvailableCredit();
			CreditPackageDTO creditPackageDTO = commerceClient.getCreditPackageByType(CreditPackageType.UNIT);
			throw new WalletNotEnoughException(BookingErrorCode.NOT_ENOUGH_CREDIT, lackOfAmount, creditPackageDTO.getTotalPrice() * lackOfAmount);
		}

		// Validate slot
		int totalSlotClassBooked = countTotalSlotBooked(cmsClassDetail.getData().getId(), BookingServiceType.CLASS);
		if (totalSlotClassBooked >= cmsClassDetail.getData().getTotalSlots()) {
			throw new BookingValidationException(BookingErrorCode.NOT_ENOUGH_SLOT);
		}
	}

	private int countTotalSlotBooked(long serviceId, BookingServiceType bookingServiceType) {
		List<CreditBookingSessionStatus> statuses = Arrays.asList(CreditBookingSessionStatus.BOOKED);
		List<BookingServiceType> serviceTypes = Collections.singletonList(bookingServiceType);
		return creditBookingClassRepository.countTotalSlotByStatusAndType(serviceId, statuses, serviceTypes);
	}

	/**
	 * Checking overlap booking time slot of SESSION/CLASS type
	 * @param clientUuid
	 * @param startTime
	 * @param endTime
	 * @return Overlapped CreditBookingEntity before
	 */
	private CreditBookingEntity validateBookingOverlapTime(String clientUuid, long startTime, long endTime) {
		try {
			LocalDateTime startDay = SessionUtil.convertToLocalDateTime(startTime);
			LocalDateTime endDay = SessionUtil.convertToLocalDateTime(endTime);

			List<CreditBookingSessionStatus> statuses = Arrays.asList(CreditBookingSessionStatus.BOOKED, CreditBookingSessionStatus.CONFIRMED);
			List<BookingServiceType> serviceTypes = Arrays.asList(BookingServiceType.CLASS, BookingServiceType.SESSION);
			List<CreditBookingEntity> entities = creditBookingRepo.findOverlappedBookedByClientAndTime(clientUuid, startDay, endDay, statuses, serviceTypes);

			if (!entities.isEmpty()) {
				return entities.get(0);
			}
		} catch (Exception e) {
			LOG.error("[validateOverlapTimeOfBookingByClient] Exception detail: {}", e.getMessage());
		}

		return null;
	}

	public CMSSessionValidatingResponse countBookingAndcheckReservedForServices(Account account, List<Long> sessionsIds) {
		CMSSessionValidatingResponse result = new CMSSessionValidatingResponse();
		// available credit
		BasicWalletEntity wallet = walletRepo.findByOwnerUuid(account.uuid());
		result.setAvailableCredit(wallet.getAvailableCredit());

		// sessions
		HashMap<Long, long[]> sessions = new HashMap<>();
		for (long l : sessionsIds) {
			// first value is reserved, second value is counting
			long[] defaultValues = {0,0};
			sessions.put(l, defaultValues);
		}

		List<Object[]> countingBooking = creditBookingClassRepository.countBookingForServices(sessionsIds);
		if (countingBooking != null) {
			// in row, first value is service_id, second value is counting
			countingBooking.forEach(row -> {
				long sessionId = ((BigInteger) row[0]).longValue();
				if (sessions.containsKey(sessionId)) {
					sessions.get(sessionId)[1] = ((BigInteger) row[1]).intValue();
				}
			});

			//	check reserved
			List<Object[]> checkingReserved = creditBookingClassRepository.checkReservedServices(account.uuid(), sessionsIds);
			if(checkingReserved != null) {
				checkingReserved.forEach(row -> {
					long sessionId = ((BigInteger) row[0]).longValue();
					if(sessions.containsKey(sessionId)) {
						sessions.get(sessionId)[0] = ((BigInteger) row[1]).longValue();
					}
				});
			}
		}
		result.setSessions(sessions);

		return result;
	}

	public PageResponse<BookingHistoryDTO> getBookingsHistory(final String uuid, final long fromDate, final long toDate,
			String partnerName, int page, int perPage) {
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDate),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDate),
				TimeZone.getDefault().toZoneId());
		List<CreditBookingSessionStatus> statuses = new ArrayList<CreditBookingSessionStatus>();
		statuses.add(CreditBookingSessionStatus.values()[3]);
		statuses.add(CreditBookingSessionStatus.values()[7]);
		Pageable pageable = new PageRequest(page, perPage);
		boolean isPTaccount = checkPTAccount(uuid);
		List<BookingServiceType> bookingType = new ArrayList<BookingServiceType>();
		if (isPTaccount) {
			bookingType.add(BookingServiceType.SESSION);
		} else {
			bookingType.add(BookingServiceType.SESSION);
			bookingType.add(BookingServiceType.CLASS);
			bookingType.add(BookingServiceType.ETICKET);
		}
		Page<BookingHistoryDTO> entitiesPage = partnerName == null
				? creditBookingRepo.getServiceHistoy(uuid, from, to, statuses, bookingType, pageable)
				: creditBookingRepo.getServiceHistoyByName(uuid, from, to, statuses, bookingType, partnerName, pageable);
		List<BookingHistoryDTO> result = entitiesPage.getContent();
		PageResponse<BookingHistoryDTO> pageResponse = new PageResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount(entitiesPage.getTotalElements());
		return pageResponse;
	}

	private boolean checkPTAccount(final String uuid) {
		Optional<BasicUserEntity> basicUserEntity = userRepo.findOneByUuid(uuid);
		BasicUserEntity userEntity = basicUserEntity.get();
		return userEntity.getUserType().equalsIgnoreCase("pt");
	}

	public HashMap<String, BigInteger> getStatisticNumberClubAndEticket(LocalDateTime start, LocalDateTime end,
			int serviceType) {
		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();

		if (serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			map.put("not_used",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.bookedCode));
			map.put("used",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.completedCode));
			map.put("cancelled",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.cancelledCode));
		}
		return map;
	}

	public List<SpendingStatisticDTO> getSpendingHistory(LocalDateTime start, LocalDateTime end, String uuid) {
		List<SpendingStatisticDTO> dto = creditBookingRepo.getSpendingStatistic(start, end, uuid);
		return dto;
	}

	public HashMap<String, BigInteger> getStatisticNumberClubAndEticket(LocalDateTime start, LocalDateTime end,
			Integer serviceType, String uuid) {
		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();

		if (serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			map.put("not_used", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.bookedCode, uuid));
			map.put("used", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.completedCode, uuid));
			map.put("cancelled", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.cancelledCode, uuid));
		}
		return map;
	}

	public Page<CreditBookingEntity> findByStudioUuidsAndStatusAndStartTimeBetween(List<String> studioUuids, List<Integer> statuses, long startTimeStartMilliseconds, long startTimeEndMilliseconds, String orderCriteria, int page, int perPage) {
		LocalDateTime startTimeStart = SessionUtil.convertToLocalDateTime(startTimeStartMilliseconds);
		LocalDateTime startTimeEnd = SessionUtil.convertToLocalDateTime(startTimeEndMilliseconds);
		List<CreditBookingSessionStatus> statusesEnum = statuses.stream().map(value -> CreditBookingSessionStatus.values()[value]).collect(Collectors.toList());

		if(orderCriteria == null) orderCriteria = "startTime.asc";
		Pageable pageable = buildPageable(page, perPage, orderCriteria);
		Page<CreditBookingEntity> entitiesPage = creditBookingRepo.findByStudioUuidsAndStatusAndStartTimeBetween(studioUuids, statusesEnum, startTimeStart, startTimeEnd, pageable);
		// Map client with booking
		List<String> clientUuids = entitiesPage.getContent().stream().map(entity -> entity.getClientUuid()).collect(Collectors.toList());
		List<BasicUserEntity> clients = userRepo.findByUuidIn(clientUuids);
		Map<String, BasicUserEntity> clientMap = clients
				.stream().collect(Collectors.toMap(BasicUserEntity::getUuid, client -> client));
		entitiesPage.getContent().forEach(booking -> {
			booking.setClient(clientMap.get(booking.getClientUuid()));
		});
		// Get checkin transactions
		if (statuses.contains(CreditBookingSessionStatus.COMPLETED.toValue())) {
			List<CreditBookingTransactionEntity> transactionEntities =
					creditBookingTransactionRepository.findAllByNewStatusAndCreditBookingIn(
							CreditBookingSessionStatus.COMPLETED, entitiesPage.getContent());
			Map<Long, CreditBookingEntity> bookingEntityMap = entitiesPage.getContent().stream()
					.collect(Collectors.toMap(CreditBookingEntity::getId, booking -> booking));
			transactionEntities.forEach(transaction -> {
				bookingEntityMap.get(transaction.getCreditBooking().getId()).setCheckinTransaction(transaction);
			});
		}

		return entitiesPage;
	}

	public List<Object[]> countByStudioUuidAndGroupByStatus(String studioUuid, long startTimeStartMilliseconds, long startTimeEndMilliseconds) {
		LocalDateTime startTimeStart = SessionUtil.convertToLocalDateTime(startTimeStartMilliseconds);
		LocalDateTime startTimeEnd = SessionUtil.convertToLocalDateTime(startTimeEndMilliseconds);
		return creditBookingRepo.countByStudioUuidAndGroupByStatus(studioUuid, startTimeStart, startTimeEnd);
	}

	private Pageable buildPageable(int page, int perPage, String orderCriteria) {
		String[] orderCriterias = orderCriteria.split("\\.");
		Direction direction = orderCriterias[1].equals("asc") ? Direction.ASC : Direction.DESC;

		return new PageRequest(page, perPage, new Sort(direction, orderCriterias[0]));
	}
	/**
	 * Get scheduled by uuid
	 * @author phong
	 * @param uuid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<CreditBookingResponse<?>> getScheduledBookingsByUuuid(String uuid, Long startTime, Long endTime) {
		List<CreditBookingResponse<?>> result = new ArrayList<CreditBookingResponse<?>>();
		try {
			//LocalDate fromDate = SessionUtil.convertToLocalDate(startTime);
			LocalDate fromDate  = Instant.ofEpochSecond(startTime ).atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = Instant.ofEpochSecond(endTime ).atZone(ZoneId.systemDefault()).toLocalDate();

			List<CreditBookingEntity> bookingEntities = new ArrayList<CreditBookingEntity>();
			BasicUserEntity user = userRepo.findOneByUuid(uuid).get();
			if (user.getUserType().equalsIgnoreCase("PT")) {
				bookingEntities = creditBookingRepo.getPTScheduledBookings(fromDate, toDate, uuid);
			} else {
				bookingEntities = creditBookingRepo.getEUScheduledBookings(fromDate, toDate, uuid);
			}

			for (CreditBookingEntity creditBookingEntity : bookingEntities) {
				switch (creditBookingEntity.getBookingType()) {
				case CLASS:
					CreditBookingResponse<CreditClassBookingResponse> classBookingResponse = new CreditBookingResponse<CreditClassBookingResponse>();
					CreditClassBookingResponse classBooking = new CreditClassBookingResponse(creditBookingEntity);
					classBookingResponse.setServiceType(BookingServiceType.CLASS);
					classBookingResponse.setBookingData(classBooking);

					result.add(classBookingResponse);
					break;
				case ETICKET:
					CreditBookingResponse<CreditETicketBookingResponse> eticketBookingResponse = new CreditBookingResponse<CreditETicketBookingResponse>();
					CreditETicketBookingResponse eticketBooking = new CreditETicketBookingResponse(creditBookingEntity);
					eticketBookingResponse.setServiceType(BookingServiceType.ETICKET);
					eticketBookingResponse.setBookingData(eticketBooking);

					result.add(eticketBookingResponse);
					break;
				case SESSION:
					BasicUserInfo userInfo = null;
					BasicUserInfo trainerInfo = null;
					CreditSessionBookingEntity session = creditBookingEntity.getSessions().get(0);
					BasicUserEntity userEntity = userRepo.findOneByUuid(session.getUserUuid()).get();
					if (userEntity != null) {
						userInfo = BasicUserInfo.convertFromEntity(userEntity);
					}
					BasicUserEntity trainerEntity = userRepo.findOneByUuid(session.getPtUuid()).get();
					if (trainerEntity != null) {
						trainerInfo = BasicUserInfo.convertFromEntity(trainerEntity);
					}
					CreditSessionBookingResponseDTO sessionBookingDTO = new CreditSessionBookingResponseDTO(
							creditBookingEntity, userInfo, trainerInfo);
					CreditBookingResponse<CreditSessionBookingResponseDTO> sessionBookingResponse = new CreditBookingResponse<CreditSessionBookingResponseDTO>();
					sessionBookingResponse.setServiceType(BookingServiceType.SESSION);
					sessionBookingResponse.setBookingData(sessionBookingDTO);

					result.add(sessionBookingResponse);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return result;
	}

	/**
	 * Get total club has booking in the range time
	 * @author phong
	 * @param start
	 * @param end
	 * @return
	 */
	public Integer getTotalClubHasBooking(LocalDateTime start, LocalDateTime end) {

		return creditBookingRepo.getTotalClubHasBooking(start, end);
	}

	@Transactional
	public void handleAutoCancelSessionBookingJob() {
		List<CreditBookingTransactionEntity> creditBookingTransaction = new ArrayList<CreditBookingTransactionEntity>();
		List<CreditBookingEntity> creditBookingEntities = creditBookingRepo.getListOverdueStartTime();
		creditBookingEntities.forEach(creditBooking -> {
			CreditBookingTransactionEntity creditBookingTransactionEntity = new CreditBookingTransactionEntity();
			creditBookingTransactionEntity.setOldStatus(creditBooking.getStatus());
			creditBooking.setStatus(CreditBookingSessionStatus.CANCELLED);

			creditBookingTransactionEntity.setAction(SessionAction.AUTO_CANCELED);
			creditBookingTransactionEntity.setCreditBooking(creditBooking);
			creditBookingTransactionEntity.setNewStatus(creditBooking.getStatus());
			creditBookingTransaction.add(creditBookingTransactionEntity);

			List<CreditSessionBookingEntity> creditSessionBookingList = creditBooking.getSessions();
			creditSessionBookingList.forEach(creditSessionBooking -> {
				creditSessionBooking.setStatus(CreditBookingSessionStatus.CANCELLED);
			});
		});
		creditBookingRepo.save(creditBookingEntities);
		bookingTransactionRepo.save(creditBookingTransaction);
	}

	private void checkWindowTime(CreditBookingEntity bookingEntity) {
		// TODO: Check window time
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTimeBefore = bookingEntity.getStartTime().minusMinutes(30);
		LocalDateTime startTimeAfter = bookingEntity.getStartTime().plusMinutes(15);
		if (now.isBefore(startTimeBefore) || now.isAfter(startTimeAfter)) {
			throw new BookingValidationException(BookingErrorCode.INVALID_CHECKIN_TIME);
		}
	}

	private CreditBookingTransactionEntity changeStatusOfCreditBookingEntity(CreditBookingEntity bookingEntity, CreditBookingSessionStatus oldStatus, CreditBookingSessionStatus newStatus, SessionAction action) {
		bookingEntity.setStatus(newStatus);

		CreditBookingTransactionEntity bookingTransaction = new CreditBookingTransactionEntity();
		bookingTransaction.setAction(action);
		bookingTransaction.setCreditBooking(bookingEntity);
		bookingTransaction.setNewStatus(newStatus);
		bookingTransaction.setOldStatus(oldStatus);

		return bookingTransaction;
	}

	/**
	 * Set start time and end time of eticket booking by the first slot of opening time of CMS E-ticket info
	 * @param eticketBookingEntity
	 * @param cmsEticketInfo
	 */
	private void setEticketUsingTime(CreditBookingEntity bookingEntity, CreditETicketBookingEntity eTicketBookingEntity, CMSETicketDTO cmsEticketDetail, long bookingDayInMillis) {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = LocalDateTime.now();

		// Setting start_time, end_time, opening_hours for e-ticket booking
		if(cmsEticketDetail.getData().isAllDay() == true) {
			startTime = SessionUtil.convertToLocalDateTime(bookingDayInMillis);
			endTime = startTime.plusDays(1);
			bookingEntity.setStartTime(startTime);
			bookingEntity.setEndTime(endTime);

			ETicketOpenTimeDTO openingTime = new ETicketOpenTimeDTO(SessionUtil.convertDateTimeToEpochMiliSecond(startTime), SessionUtil.convertDateTimeToEpochMiliSecond(endTime));
			List<ETicketOpenTimeDTO> openingTimes = Arrays.asList(openingTime);
			OpeningHour openingHours = new OpeningHour();
			openingHours.setOpeningHours(openingTimes);
			eTicketBookingEntity.setOpeningHours(SessionUtil.toJsonString(openingHours));			// {"openingHours":[{"from":1606755600000,"to":1606842000000}]}
		} else {
			if(cmsEticketDetail.getData().getOpenTimes().size() > 0) {
				ETicketOpenTimeDTO openingTime = cmsEticketDetail.getData().getOpenTimes().get(0);
				startTime = SessionUtil.convertToLocalDateTime(openingTime.getFrom());
				endTime = SessionUtil.convertToLocalDateTime(openingTime.getTo());
				bookingEntity.setStartTime(startTime);
				bookingEntity.setEndTime(endTime);

				OpeningHour openingHours = new OpeningHour();
				openingHours.setOpeningHours(cmsEticketDetail.getData().getOpenTimes());
				eTicketBookingEntity.setOpeningHours(SessionUtil.toJsonString(openingHours));
			}
		}
	}
	
	@Transactional
	public CreditBookingEntity checkinClassCategoryService(String categoryId, String studioUuid, Account account) throws Exception {
		List<CreditBookingEntity> bookingEntities = creditBookingRepo.findByStudioUuidAndClientUuidAndStatusInAndBookingType(studioUuid, account.uuid(), Collections.singletonList(CreditBookingSessionStatus.BOOKED), BookingServiceType.CLASS);
		if (CollectionUtils.isEmpty(bookingEntities)) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		CreditBookingEntity creditBooking = null;
		for (CreditBookingEntity entity : bookingEntities) {
			try {
				checkWindowTime(entity);
				creditBooking = entity;
			} catch (Exception e) {
				continue;
			}
		}
		if (creditBooking == null) {
			throw new BookingValidationException(BookingErrorCode.INVALID_CHECKIN_TIME);
		}
		Boolean check = checkingCategoryBooking(categoryId, creditBooking.getClasses().get(0));
		if (!check) {
			throw new BookingValidationException(BookingErrorCode.NOT_BOOKED_YET);
		}
		
		CreditBookingSessionStatus oldStatus = creditBooking.getStatus();
		CreditBookingSessionStatus newStatus = CreditBookingSessionStatus.COMPLETED;

		creditBooking.setStatus(newStatus);
		creditBooking.setConfirmationCode(generateConfirmationCode(creditBooking));
		creditBookingRepo.save(creditBooking);

		CreditBookingTransactionEntity transaction = new CreditBookingTransactionEntity();
		transaction.setAction(SessionAction.CHECK_IN);
		transaction.setCreditBooking(creditBooking);
		transaction.setNewStatus(newStatus);
		transaction.setOldStatus(oldStatus);
		bookingTransactionRepo.save(transaction);

		deductCreditsOnCheckinCompleted(BookingServiceType.CLASS, creditBooking);

		return creditBooking;
	}
	
	private Boolean checkingCategoryBooking(String categoryId, CreditClassBookingEntity creditClassBookingEntity) {
		CMSClassCategoryBookingDTO result = cmsService.checkCategoryBelongSessions(categoryId, Collections.singletonList(String.valueOf(creditClassBookingEntity.getServiceId())));
		if (result == null) {
			throw new BookingValidationException(BookingErrorCode.CMS_NOT_RESPONSE);
		}
		return (Boolean) result.getData().get(String.valueOf(creditClassBookingEntity.getServiceId()));
	}

	public void remindClassStarting() {
		int minBeforeStarting = 60;
		LocalDateTime startTimeStart = LocalDateTime.now();
		LocalDateTime endTimeStart = startTimeStart.plusMinutes(minBeforeStarting);
		List<CreditBookingEntity> bookingEntities =
				creditBookingRepo.findByBookingTypeAndStatusAndStartTimeBetweenAndNotificationReminded(
						BookingServiceType.CLASS, CreditBookingSessionStatus.BOOKED, startTimeStart, endTimeStart, false);
		// Trigger sent noti for clients
		bookingEntities.forEach(booking -> {
			CreditClassBookingEntity classEntity = booking.getClasses().get(0);
			ScheduleEvent event = ScheduleEvent.newBuilder()
					.setEventId(UUID.randomUUID().toString())
					.setUserUuid(booking.getClientUuid())
					.setMinBeforeStarting(minBeforeStarting)
					.setClassName(classEntity.getServiceName())
					.setNotiType("CLASS_STARTING_REMIND")
					.build();
			eventHandler.publish(event);
			booking.setNotificationReminded(true);
		});
	}
	
	private String checkingClassStatus(String studioUuid, long serviceId) {
		CMSClassCategoryBookingDTO result = cmsService.checkPublishClass(studioUuid, serviceId);
		if (result == null) {
			throw new BookingValidationException(BookingErrorCode.CMS_NOT_RESPONSE);
		}
		return (String) result.getData().get(STATUS_FIELD);
	}
	
	private String checkingClubStatus(String studioUuid) {
		CMSClassCategoryBookingDTO result = cmsService.checkPublishStudio(studioUuid);
		if (result == null) {
			throw new BookingValidationException(BookingErrorCode.CMS_NOT_RESPONSE);
		}
		return (String) result.getData().get(STATUS_FIELD);
	}

	public List<BookingManagementDTO> eticketBookingManagement(String keyword, LocalDateTime start, LocalDateTime end,
			List<Integer> status, List<BookingServiceType> type , Pageable pageable) {
		List<CreditBookingSessionStatus> statusEnum = new ArrayList<CreditBookingSessionStatus>();
		status.forEach(el-> {
			statusEnum.add(CreditBookingSessionStatus.values()[el]);
		});
		return creditBookingRepo.eticketBookingManagement(keyword, start, end, statusEnum, type, pageable);
	}
	
	public Boolean checkFirstBooking(String uuid, BookingServiceType serviceType) {
		List<CreditBookingEntity> listBooking = creditBookingRepo.findByClientUuidAndBookingType(uuid, serviceType);
		return listBooking.size() == 1 ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public void sendEmailToFinance(CreditBookingEntity entity) {
		final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		String serviceType = "";
		String subject;
		switch (entity.getBookingType()) {
		case SESSION :
			serviceType = "Personal trainer session";
			break;
		case ETICKET:
			CreditETicketBookingEntity eticket = entity.getEtickets().get(0);
			serviceType = eticket.isAllDay() ? "All day" : "Off-peak time";
			break;
		case CLASS:
			serviceType = "Class";
			break;
		default:
			break;
		}
		Optional<BasicUserEntity> customerEntity = userRepo.findOneByUuid(entity.getClientUuid());
		if (!customerEntity.isPresent()) {
			return;
		}
		BasicUserEntity customer = customerEntity.get();
		String bookingTime = DATE_FORMATTER.format(entity.getCreatedDate());
		String sessionTime = DATE_FORMATTER.format(entity.getStartTime());
		subject = customer.getFullName() + " - " + serviceType + " on " + bookingTime;
		Locale locale = Locale.ENGLISH;
		Context context = new Context(locale);
		context.setVariable(CUSTOMER_NAME, "Full name: " + customer.getFullName());
		context.setVariable(USERNAME, "User name: " + customer.getUserName());
		context.setVariable(PHONE, "Phone number: " + customer.getPhone());
		context.setVariable(BOOKING_TIME, "Booking time: " + bookingTime);
		context.setVariable(SESSION_TIME, "Session time: " + sessionTime);
		context.setVariable(SERVICE_TYPE, "Type of service: " + serviceType);
		context.setVariable(CLUB_NAME, "Club name: " + entity.getStudioName());
		context.setVariable(LOGO_URL, bookingEmail.getLogoUrl());
		context.setVariable(APP_STORE, bookingEmail.getAppStore());
		context.setVariable(GOOGLE_PLAY, bookingEmail.getGooglePlay());
		context.setVariable(DECOR, bookingEmail.getDecor());
		context.setVariable(DIVIDER, bookingEmail.getDivider());
		context.setVariable(CONTACT, bookingEmail.getContact());
		String emailTempalte = templateEngine.process(BOOKING_EMAIL, context);

		EmailSender email = new EmailSender();
		Boolean sendMail = email.sendEmail(bookingEmail.getSender(), bookingEmail.getRecipients(), subject, emailTempalte);
		if (!sendMail) {
			LOG.error("Fail to send email");
		}
	}
}
