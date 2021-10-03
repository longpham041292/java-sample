package asia.cmg.f8.commerce.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.dto.ClubProfitSharingReportDTO;
import asia.cmg.f8.commerce.dto.CmsCreateWalletRequest;
import asia.cmg.f8.commerce.dto.CreditWalletDTO;
import asia.cmg.f8.commerce.dto.DeductCoinClubDTO;
import asia.cmg.f8.commerce.dto.DeductCoinTrainerDTO;
import asia.cmg.f8.commerce.dto.StudioDto;
import asia.cmg.f8.commerce.dto.TopUpReportDto;
import asia.cmg.f8.commerce.dto.TrainerProfitSharingReportDTO;
import asia.cmg.f8.commerce.dto.TransactionReportDto;
import asia.cmg.f8.commerce.dto.UserCreditFlowStatisticDto;
import asia.cmg.f8.commerce.dto.WalletActivityDto;
import asia.cmg.f8.commerce.dto.WalletCashFlow;
import asia.cmg.f8.commerce.entity.credit.ClubOutcomeEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.OutComePaymentStatus;
import asia.cmg.f8.commerce.entity.credit.Partner;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.UserOutcomeEntity;
import asia.cmg.f8.commerce.entity.credit.WithdrawalStatus;
import asia.cmg.f8.commerce.repository.BasicUserRepository;
import asia.cmg.f8.commerce.repository.ClubOutcomeRepository;
import asia.cmg.f8.commerce.repository.CreditPackageRepository;
import asia.cmg.f8.commerce.repository.CreditWalletRepository;
import asia.cmg.f8.commerce.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.commerce.repository.OrderRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageTransationRepository;
import asia.cmg.f8.commerce.repository.UserOutcomeRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.PagedResponse;

@Service
public class WalletService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

	public final List<Integer> earningSessionBookingStatus = Arrays.asList(
			CreditTransactionType.PAY_COMPLETED_SESSION.ordinal(), CreditTransactionType.PAY_BURNED_SESSION.ordinal());
	public final List<Integer> pendingStatus = Arrays.asList(WithdrawalStatus.PENDING.ordinal());

	public final List<CreditTransactionType> earningSessionBookingStatusEntire = Arrays
			.asList(CreditTransactionType.PAY_COMPLETED_SESSION, CreditTransactionType.PAY_BURNED_SESSION);
	public final List<Integer> earningClubBookingStatus = Arrays.asList(
			CreditTransactionType.PAY_COMPLETED_CLASS.ordinal(), CreditTransactionType.PAY_BURNED_CLASS.ordinal(),
			CreditTransactionType.PAY_COMPLETED_ETICKET.ordinal(), CreditTransactionType.PAY_BURNED_ETICKET.ordinal());
	public final List<CreditTransactionType> earningClubBookingStatusEntire = Arrays.asList(
			CreditTransactionType.PAY_COMPLETED_CLASS, CreditTransactionType.PAY_BURNED_CLASS,
			CreditTransactionType.PAY_COMPLETED_ETICKET, CreditTransactionType.PAY_BURNED_ETICKET);
	public final List<WithdrawalStatus> WithdrawalStatusEntire = Arrays.asList(WithdrawalStatus.PENDING);
	public final String DATE_PATTERN = "dd/MM/yyyy";
	public final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

	@Autowired
	private CreditWalletTransactionRepository creditWalletTransactionRepository;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private CreditWalletRepository creditWalletRepository;

	@Autowired
	private UserCreditPackageRepository userCreditPackageRepository;

	@Autowired
	private UserCreditPackageTransationRepository userCreditPackageTransationRepository;

	@Autowired
	private BasicUserRepository basicUserRepo;

	@Autowired
	private UserOutcomeRepository userOutcomeRepo;

	@Autowired
	private ClubOutcomeRepository clubOutcomeRepo;

	@Autowired
	private CommerceProperties commerceProperties;

	@Autowired
	private CreditPackageRepository creditPackageRepository;

	public UserCreditPackageEntity getNextExpiredPackage(String ownerUuid) {
		try {
			Optional<UserCreditPackageEntity> entityOpt = userCreditPackageRepository
					.getNextExpiredPackageByOwner(ownerUuid);
			if (entityOpt.isPresent()) {
				return entityOpt.get();
			}
		} catch (Exception e) {
		}

		return null;
	}

	public CreditWalletEntity getWalletByOwnerUuid(final String ownerUuid) {
		try {
			return creditWalletRepository.findByOwnerUuid(ownerUuid);
		} catch (Exception e) {
			return null;
		}
	}

	public CreditWalletDTO getWalletDTOByOwnerUuid(final String ownerUuid) {
		try {
			return creditWalletRepository.getWalletByOwnerUuid(ownerUuid);
		} catch (Exception e) {
			return null;
		}
	}

	public CreditWalletEntity topupCreditWallet(final String ownerUuid, final int credit, final Double amount)
			throws Exception {
		try {
			CreditWalletEntity creditWallet = creditWalletRepository.findByOwnerUuid(ownerUuid);
			if (creditWallet == null) {
				throw new Exception("The credit wallet does not exist");
			}

			int updatedCredit = creditWallet.getTotalCredit() + credit;
			Double upadatedAccumulationAmount = creditWallet.getAccumulationAmount() + amount;

			creditWallet.setAvailableCredit(updatedCredit);
			creditWallet.setTotalCredit(updatedCredit);
			creditWallet.setAccumulationAmount(upadatedAccumulationAmount);
			creditWallet
					.setLevel(creditWallet.getLevel().upgradeCreditWalletLevelOperation(upadatedAccumulationAmount));

			return creditWalletRepository.save(creditWallet);
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public CreditWalletTransactionEntity plusCredits(String ownerUuid, long bookingId, int creditAmount,
			CreditTransactionType transactionType, String desciption, List<String> descriptionParams) throws Exception {
		try {
			// Process updating wallet record
			CreditWalletEntity wallet = creditWalletRepository.findByOwnerUuid(ownerUuid);
//			BasicUserEntity userInfo = basicUserRepo.findOneByUuid(ownerUuid).get();
			int currentCredits = wallet.getAvailableCredit();
			int updatedCredit = currentCredits + creditAmount;
			wallet.setAvailableCredit(updatedCredit);
			wallet.setTotalCredit(updatedCredit);
			creditWalletRepository.save(wallet);

			// Process creating wallet transaction record
			CreditWalletTransactionEntity walletTransaction = new CreditWalletTransactionEntity();
			walletTransaction.setCreditAmount(creditAmount);
			walletTransaction.setBookingId(bookingId);
			walletTransaction.setCreditWalletId(wallet.getId());
			walletTransaction.setDescription(desciption);
			walletTransaction.setDescriptionParams(CommerceUtils.convertListToStringWithSemicolon(descriptionParams));
			walletTransaction.setNewCreditBalance(updatedCredit);
			walletTransaction.setOldCreditBalance(currentCredits);
//			walletTransaction.setOwnerImage(userInfo.getAvatar());
			walletTransaction.setOwnerUuid(ownerUuid);
			walletTransaction.setTransactionType(transactionType);
			walletTransaction = creditWalletTransactionRepository.save(walletTransaction);
			
			if (transactionType == CreditTransactionType.REFUND_CANCEL_CLASS
					|| transactionType == CreditTransactionType.REFUND_CANCEL_ETICKET
					|| transactionType == CreditTransactionType.REFUND_CANCEL_SESSION) {
				refundUserCreditPackage(bookingId, walletTransaction);
			}

			return walletTransaction;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public CreditWalletTransactionEntity subtractClientCredits(String ownerUuid, long bookingId, int creditAmount,
			CreditTransactionType transactionType, String desciption, List<String> descriptionParams) throws Exception {
		try {
			CreditWalletEntity wallet = creditWalletRepository.findByOwnerUuid(ownerUuid);
			if (wallet == null) {
				LOGGER.info("The wallet of user {} is not existed", ownerUuid);
				throw new Exception(String.format("User's wallet is not existed"));
			}
			if (wallet.getAvailableCredit() < creditAmount) {
				LOGGER.info("User's wallet is not enough credits {}", wallet.getAvailableCredit());
				throw new Exception(String.format("User's wallet is not enough credits"));
			}

			// Process update credit wallet and credit wallet transaction
			int currentCreditOfWallet = wallet.getAvailableCredit();
			int newCreditsOfWallet = wallet.getAvailableCredit() - creditAmount;
			wallet.setAvailableCredit(newCreditsOfWallet);
			wallet.setTotalCredit(newCreditsOfWallet);
			creditWalletRepository.save(wallet);

			CreditWalletTransactionEntity walletTransaction = new CreditWalletTransactionEntity();
			walletTransaction.setOwnerUuid(wallet.getOwnerUuid());
			walletTransaction.setBookingId(bookingId);
			walletTransaction.setTransactionType(transactionType);
			walletTransaction.setCreditWalletId(wallet.getId());
			walletTransaction.setOldCreditBalance(currentCreditOfWallet);
			walletTransaction.setCreditAmount(creditAmount * (-1));
			walletTransaction.setNewCreditBalance(newCreditsOfWallet);
			walletTransaction.setDescription(desciption);
			walletTransaction.setDescriptionParams(CommerceUtils.convertListToStringWithSemicolon(descriptionParams));
			walletTransaction = creditWalletTransactionRepository.save(walletTransaction);

			// Process update UserCreditPackageEntity and UserCreditPackageTransactionEntity
			int requiredCreditAmount = creditAmount;
			int usingCreditAmount = 0;
			List<UserCreditPackageEntity> availableCreditPackages = userCreditPackageRepository
					.getAvailableCreditPackages(ownerUuid);
			for (UserCreditPackageEntity userCreditPackageEntity : availableCreditPackages) {
				int availableCreditsOfPackage = userCreditPackageEntity.getTotalCredit()
						- userCreditPackageEntity.getUsedCredit();

				if (requiredCreditAmount > 0) {
					if (availableCreditsOfPackage < requiredCreditAmount) {
						requiredCreditAmount = requiredCreditAmount - availableCreditsOfPackage;
						usingCreditAmount = availableCreditsOfPackage;
					} else {
						usingCreditAmount = requiredCreditAmount;
						requiredCreditAmount = 0;
					}

					userCreditPackageEntity.setUsedCredit(userCreditPackageEntity.getUsedCredit() + usingCreditAmount);
					userCreditPackageRepository.save(userCreditPackageEntity);

					UserCreditPackageTransactionEntity packageTransaction = generateCreditPackageTransaction(
							walletTransaction.getId(), ownerUuid, usingCreditAmount, userCreditPackageEntity,
							availableCreditsOfPackage);
					userCreditPackageTransationRepository.save(packageTransaction);
				}
			}
			return walletTransaction;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public CreditWalletEntity studioCheckin(StudioDto studio, Account account) {
		try {
			CreditWalletEntity wallet = creditWalletRepository.findByOwnerUuid(account.uuid());

			List<UserCreditPackageEntity> availableCreditPackages = userCreditPackageRepository
					.getAvailableCreditPackages(account.uuid());

			if (availableCreditPackages == null || availableCreditPackages.isEmpty())
				return null;

			Long walletTransactionId = createWalletTransactionEntry(studio, wallet);

			processCreditCheckinTransaction(walletTransactionId, studio, account, availableCreditPackages);

			return updateWalletCredit(studio, wallet);
		} catch (Exception e) {
			LOGGER.error("[WalletService] error detail: {}", e.getMessage());
			return null;
		}
	}

	public CreditWalletEntity createWallet(CreditWalletEntity walletEntity) throws Exception {
		try {
			return creditWalletRepository.saveAndFlush(walletEntity);
		} catch (Exception e) {
			throw e;
		}
	}

	public CreditWalletEntity createWallet(CmsCreateWalletRequest request) throws Exception {
		try {
			CreditWalletEntity walletEntity = new CreditWalletEntity();
			walletEntity.setLevel(CreditWalletLevel.BASIC);
			walletEntity.setOwnerUuid(request.getStudioUuid());
			walletEntity.setOwnerName(request.getStudioName());
			walletEntity.setPartner(Partner.CMS);

			return creditWalletRepository.saveAndFlush(walletEntity);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<WalletActivityDto> getWalletActivities(String owner_uuid, Pageable pageable) {
		return creditWalletTransactionRepository.getWalletActivities(owner_uuid, pageable).getContent();
	}

	public Page<WalletActivityDto> getWalletActivities(String owner_uuid, WalletCashFlow cashFlow, Pageable pageable) {
		switch (cashFlow) {
		case ALL:
			return creditWalletTransactionRepository.getWalletActivities(owner_uuid, pageable);
		case IN:
			return creditWalletTransactionRepository.getWalletCashInActivities(owner_uuid, pageable);
		case OUT:
			return creditWalletTransactionRepository.getWalletCashOutActivities(owner_uuid, pageable);
		default:
			return null;
		}
	}

	public CreditWalletEntity getCMSStudioWallet(final long id, final String ownerUuid) {
		return creditWalletRepository.getCMSStudioWallet(id, ownerUuid);
	}

	private void processCreditCheckinTransaction(Long walletTransactionId, StudioDto studio, Account account,
			List<UserCreditPackageEntity> availableCreditPackages) {
		int creditTotal = 0;

		List<UserCreditPackageEntity> usingCreditPackages = new ArrayList<>();
		List<UserCreditPackageTransactionEntity> usingCreditPackageTransactions = new ArrayList<>();
		int index = 0;
		while (creditTotal != studio.getCheckinCredit()) {

			int creditRequired = studio.getCheckinCredit() - creditTotal;
			int usingCredit = 0;
			UserCreditPackageEntity creditPackage = availableCreditPackages.get(index);
			int remainingCredit = creditPackage.getTotalCredit() - creditPackage.getUsedCredit();
			if (remainingCredit <= creditRequired) {
				usingCredit = remainingCredit;
			} else {
				usingCredit = creditRequired;
			}

			// Update used credit
			creditTotal += usingCredit;
			creditPackage.setUsedCredit(creditPackage.getUsedCredit() + usingCredit);
			creditPackage.setModifiedDate(LocalDateTime.now());

			UserCreditPackageTransactionEntity packageTransaction = generateCreditPackageTransaction(
					walletTransactionId, account.uuid(), usingCredit, creditPackage, remainingCredit);

			usingCreditPackageTransactions.add(packageTransaction);
			usingCreditPackages.add(creditPackage);

			index++;
		}

		userCreditPackageRepository.save(usingCreditPackages);
		userCreditPackageTransationRepository.save(usingCreditPackageTransactions);
	}

	/**
	 *
	 * @param creditWalletTransactionId
	 * @param ownerUuid
	 * @param usingCredit
	 * @param creditPackage
	 * @param remainingCredit
	 * @return
	 */
	private UserCreditPackageTransactionEntity generateCreditPackageTransaction(Long creditWalletTransactionId,
			String ownerUuid, int usingCredit, UserCreditPackageEntity creditPackage, int remainingCredit) {
		// Add used credit transaction
		UserCreditPackageTransactionEntity packageTransaction = new UserCreditPackageTransactionEntity();
		packageTransaction.setCreatedDate(LocalDateTime.now());
		packageTransaction.setCurrentCredit(remainingCredit);
		packageTransaction.setUsedCredit(usingCredit);
		packageTransaction.setRemainingCredit(creditPackage.getTotalCredit() - creditPackage.getUsedCredit());
		packageTransaction.setUserCreditPackageId(creditPackage.getId());
		packageTransaction.setOwnerUuid(ownerUuid);
		packageTransaction.setCreditWalletTransactionId(creditWalletTransactionId);
		packageTransaction.setTransactionType(CreditPackageTransactionType.BOOKED);
		return packageTransaction;
	}

	private Long createWalletTransactionEntry(StudioDto studio, CreditWalletEntity wallet) {
		CreditWalletTransactionEntity entity = new CreditWalletTransactionEntity();

		entity.setOwnerUuid(wallet.getOwnerUuid());
		entity.setTransactionType(CreditTransactionType.STUDIO_CHECKIN);
		entity.setCreditWalletId(wallet.getId());

		entity.setPartnerCode(studio.getUuid());
		entity.setPartnerUuid(studio.getUuid());
		entity.setPartnerName(studio.getName());
		entity.setPartnerImage(studio.getImage());

		entity.setOldCreditBalance(wallet.getAvailableCredit());
		entity.setCreditAmount(studio.getCheckinCredit() * (-1));
		entity.setNewCreditBalance(wallet.getAvailableCredit() - studio.getCheckinCredit());
		entity.setDescriptionParams(studio.getName());
		entity.setDescription(
				CreditTransactionType.STUDIO_CHECKIN.getText() + "_" + studio.getUuid() + "_" + studio.getName());

		return creditWalletTransactionRepository.save(entity).getId();
	}

	private CreditWalletEntity updateWalletCredit(StudioDto studio, CreditWalletEntity wallet) {

		int wallCreditRemaining = wallet.getAvailableCredit() - studio.getCheckinCredit();
		wallet.setAvailableCredit(wallCreditRemaining);
		wallet.setTotalCredit(wallCreditRemaining);
		creditWalletRepository.save(wallet);

		return wallet;
	}

	/**
	 * @author phong
	 * @param cashFlow, start, end, keyword, pageable
	 * @return
	 */
	public Page<TopUpReportDto> getTopUpActivitiesAllUser(LocalDateTime start, LocalDateTime end, String keyword,
			Pageable pageable) {
		return orderRepo.getTopUpReport(start, end, keyword, pageable);
	}

	public Page<TransactionReportDto> getClientWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		return creditWalletTransactionRepository.getClientWalletActivitiesReport(start, end, keyword,
				commerceProperties.getLeepAccountUuid(), pageable);
	}

	public List<TransactionReportDto> getClubWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		List<Integer> clubStatus = Arrays.asList(CreditTransactionType.PAY_COMPLETED_ETICKET.ordinal(),
				CreditTransactionType.PAY_BURNED_ETICKET.ordinal(),
				CreditTransactionType.PAY_COMPLETED_ETICKET.ordinal(),
				CreditTransactionType.PAY_COMPLETED_CLASS.ordinal());
		List<Object[]> raws = creditWalletTransactionRepository.getClubWalletActivitiesReport(start, end, keyword,
				commerceProperties.getLeepAccountUuid(), clubStatus, pageable);
		return raws.stream().map(this::createClientWalletTransactionReportDto).collect(Collectors.toList());

	}

	private TransactionReportDto createClientWalletTransactionReportDto(Object[] objects) {
		final String DATE_PATTERN = "dd/MM/yyyy hh:mm:ss a";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

		final Long creditWalletId = ((BigInteger) objects[0]).longValue();
		final String ownerUuid = (String) objects[1];
		final String clientName = (String) objects[2];
		final String partnerName = (String) objects[3];
		final Integer creditAmount = (Integer) objects[4];
		Integer type = (Integer) objects[5];
		final String transactionType = CreditTransactionType.values()[type].toString();
		Integer status = (Integer) objects[6];
		final String transactionStatus = CreditTransactionStatus.values()[status].toString();
		Timestamp time = (Timestamp) objects[7];
		LocalDateTime localTime = time.toLocalDateTime();
		final String transactionDate = localTime == null ? "" : DATE_FORMATTER.format(localTime);
		final Long bookingId = ((BigInteger) objects[8]).longValue();
		final String partnerUsername = (String) objects[9];

		TransactionReportDto dto = new TransactionReportDto();
		dto.setCreditWalletId(creditWalletId);
		dto.setOwnerUuid(ownerUuid);
		dto.setClientName(clientName);
		dto.setPartnerName(partnerName);
		dto.setPartnerUsername(partnerUsername);
		dto.setCreditAmount(creditAmount);
		dto.setTransactionType(transactionType);
		dto.setTransactionStatus(transactionStatus);
		dto.setTransactionDate(transactionDate);
		dto.setBookingId(bookingId);
		return dto;
	}

	public Page<TransactionReportDto> getTrainerWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		return creditWalletTransactionRepository.getTrainerWalletActivitiesReport(start, end, keyword,
				commerceProperties.getLeepAccountUuid(), pageable);
	}

	public List<UserCreditFlowStatisticDto> getAllCreditSesionsBookingOfTrainer(LocalDateTime start, LocalDateTime end,
			String ptUuid) {
		List<CreditTransactionType> completedCode = Arrays.asList(CreditTransactionType.PAY_BURNED_SESSION,
				CreditTransactionType.PAY_COMPLETED_SESSION);
		final List<CreditWalletTransactionEntity> result = creditWalletTransactionRepository
				.getAllCreditSesionsBookingOfTrainer(start, end, ptUuid, completedCode);
		final List<UserCreditFlowStatisticDto> stats = new ArrayList<UserCreditFlowStatisticDto>();

		result.stream().forEach(entry -> {
			final Long time = entry.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond();
			UserCreditFlowStatisticDto dto = new UserCreditFlowStatisticDto(time, entry.getCreditAmount());
			stats.add(dto);
		});
		return stats;
	}

	public List<UserCreditFlowStatisticDto> getClientSpendingByRangeTime(LocalDateTime start, LocalDateTime end,
			String ptUuid) {
		final List<CreditWalletTransactionEntity> result = creditWalletTransactionRepository
				.getClientSpendingByRangeTime(start, end, ptUuid);
		final List<UserCreditFlowStatisticDto> stats = new ArrayList<UserCreditFlowStatisticDto>();

		result.stream().forEach(entry -> {
			final Long time = entry.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond();
			UserCreditFlowStatisticDto dto = new UserCreditFlowStatisticDto(time, entry.getCreditAmount());
			stats.add(dto);
		});
		return stats;
	}

	public UserOutcomeEntity updateUserOutComePaymentStatus(Long id) {
		UserOutcomeEntity outcome = userOutcomeRepo.findOne(id);
		if (outcome.getPaymentStatus() == OutComePaymentStatus.PENDING) {
			outcome.setPaymentStatus(OutComePaymentStatus.PAID);
			outcome.setPaymentDate(LocalDateTime.now());
			userOutcomeRepo.save(outcome);
			return outcome;
		}
		return null;

	}

	public ClubOutcomeEntity updateClubOutComePaymentStatus(Long id) {
		ClubOutcomeEntity outcome = clubOutcomeRepo.findOne(id);
		if (outcome.getPaymentStatus() == OutComePaymentStatus.PENDING) {
			outcome.setPaymentStatus(OutComePaymentStatus.PAID);
			outcome.setPaymentDate(LocalDateTime.now());
			clubOutcomeRepo.save(outcome);
			return outcome;
		}
		return null;
	}

	public PagedResponse<ClubProfitSharingReportDTO> getClubProfitSharingReport(long startTime, long endTime,
			String name, Pageable pageable) {
		List<ClubProfitSharingReportDTO> result = new ArrayList<ClubProfitSharingReportDTO>();
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		Double price = 0d;
		Optional<CreditPackageEntity> creditPackageEntity = creditPackageRepository
				.getUnitPackage(CreditPackageType.UNIT.toValue());
		if (creditPackageEntity.isPresent()) {
			price = creditPackageEntity.get().getTotalPrice();
		}
		Page<Object[]> entitiesPage = name == null ? clubOutcomeRepo.getClubProfitReport(from, to, pageable)
				: clubOutcomeRepo.getClubProfitReportWithFilter(from, to, name, pageable);
		List<Object[]> clubProfitSharing = entitiesPage.getContent();
		for (Object[] row : clubProfitSharing) {
			ClubProfitSharingReportDTO clubProfitSharingReportDTO = new ClubProfitSharingReportDTO();
			clubProfitSharingReportDTO.setClubName(row[0] == null ? "" : row[0].toString());
			clubProfitSharingReportDTO.setClubAddress(row[1] == null ? "" : row[1].toString());
			clubProfitSharingReportDTO.setClubCoinEarn(row[2] == null ? null
					: Math.round(((BigDecimal) row[2]).intValue() / (1 - commerceProperties.getPit())));
			clubProfitSharingReportDTO.setTotalAmount(row[3] == null ? 0 : ((BigDecimal) row[3]).intValue());
			clubProfitSharingReportDTO.setVatAmount(
					Math.round(clubProfitSharingReportDTO.getTotalAmount() / commerceProperties.getVat()));
			clubProfitSharingReportDTO.setAmountExcludeVat(
					clubProfitSharingReportDTO.getTotalAmount() - clubProfitSharingReportDTO.getVatAmount());
			clubProfitSharingReportDTO.setLeepFee(
					clubProfitSharingReportDTO.getAmountExcludeVat() - clubProfitSharingReportDTO.getClubCoinEarn());
			clubProfitSharingReportDTO
					.setClubNetIncome(row[2] == null ? null : Math.round(((BigDecimal) row[2]).intValue() * price));
			clubProfitSharingReportDTO
					.setClubGrossIncome(Math.round(clubProfitSharingReportDTO.getClubCoinEarn() * price));
			clubProfitSharingReportDTO.setTaxWitholding(
					clubProfitSharingReportDTO.getClubGrossIncome() - clubProfitSharingReportDTO.getClubNetIncome());
			result.add(clubProfitSharingReportDTO);
		}
		PagedResponse<ClubProfitSharingReportDTO> pageResponse = new PagedResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount((int) entitiesPage.getTotalElements());
		return pageResponse;
	}

	public PagedResponse<TrainerProfitSharingReportDTO> getTrainerProfitSharingReport(long startTime, long endTime,
			String name, Pageable pageable) {
		List<TrainerProfitSharingReportDTO> result = new ArrayList<TrainerProfitSharingReportDTO>();
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		Double price = 0d;
		Optional<CreditPackageEntity> creditPackageEntity = creditPackageRepository
				.getUnitPackage(CreditPackageType.UNIT.toValue());
		if (creditPackageEntity.isPresent()) {
			price = creditPackageEntity.get().getTotalPrice();
		}
		Page<Object[]> entitiesPage = name == null ? userOutcomeRepo.getTrainerProfitReport(from, to, pageable)
				: userOutcomeRepo.getTrainerProfitReportWithFilter(from, to, name, pageable);
		List<Object[]> trainerProfitSharing = entitiesPage.getContent();
		for (Object[] row : trainerProfitSharing) {
			TrainerProfitSharingReportDTO trainerProfitSharingReportDTO = new TrainerProfitSharingReportDTO();
			trainerProfitSharingReportDTO.setPtName(row[0] == null ? "" : row[0].toString());
			trainerProfitSharingReportDTO.setFullName(row[0] == null ? "" : row[0].toString());
			trainerProfitSharingReportDTO.setPtUsername(row[1] == null ? "" : row[1].toString());
			trainerProfitSharingReportDTO.setEmailAddress(row[2] == null ? "" : row[2].toString());
			trainerProfitSharingReportDTO.setPhoneNumber(row[3] == null ? "" : row[3].toString());
			trainerProfitSharingReportDTO.setTotalAmount(row[4] == null ? null : ((BigDecimal) row[4]).intValue());
			trainerProfitSharingReportDTO.setPtCoinEarn(row[5] == null ? null
					: Math.round(((BigDecimal) row[5]).intValue() / (1 - commerceProperties.getPit())));
			trainerProfitSharingReportDTO.setVatAmount(
					Math.round(trainerProfitSharingReportDTO.getTotalAmount() / commerceProperties.getVat()));
			trainerProfitSharingReportDTO.setAmountExcludeVat(
					trainerProfitSharingReportDTO.getTotalAmount() - trainerProfitSharingReportDTO.getVatAmount());
			trainerProfitSharingReportDTO.setLeepFee(trainerProfitSharingReportDTO.getAmountExcludeVat()
					- trainerProfitSharingReportDTO.getPtCoinEarn());
			trainerProfitSharingReportDTO
					.setPtNetIncome(row[5] == null ? null : Math.round(((BigDecimal) row[5]).intValue() * price));
			trainerProfitSharingReportDTO
					.setPtGrossIncome(Math.round(trainerProfitSharingReportDTO.getPtCoinEarn() * price));
			trainerProfitSharingReportDTO.setTaxWitholding(
					trainerProfitSharingReportDTO.getPtGrossIncome() - trainerProfitSharingReportDTO.getPtNetIncome());
			result.add(trainerProfitSharingReportDTO);
		}
		PagedResponse<TrainerProfitSharingReportDTO> pageResponse = new PagedResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount((int) entitiesPage.getTotalElements());
		return pageResponse;
	}

	public PagedResponse<DeductCoinClubDTO> getDeductCoinClubReport(long startTime, long endTime, String name,
			Pageable pageable) {
		List<DeductCoinClubDTO> result = new ArrayList<DeductCoinClubDTO>();
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		Double price = 0d;
		Optional<CreditPackageEntity> creditPackageEntity = creditPackageRepository
				.getUnitPackage(CreditPackageType.UNIT.toValue());
		if (creditPackageEntity.isPresent()) {
			price = creditPackageEntity.get().getTotalPrice();
		}
		Page<Object[]> entitiesPage = name == null ? clubOutcomeRepo.getDeductCoinClubReport(from, to, pageable)
				: clubOutcomeRepo.getDeductCoinClubReportWithFilter(from, to, name, pageable);
		List<Object[]> deductCoinClub = entitiesPage.getContent();
		for (Object[] row : deductCoinClub) {
			DeductCoinClubDTO deductCoinClubDTO = new DeductCoinClubDTO();
			deductCoinClubDTO.setOutcomeId(((BigInteger) row[0]).longValue());
			deductCoinClubDTO.setClubName(row[1] == null ? "" : row[1].toString());
			deductCoinClubDTO.setEarnCoin(row[2] == null ? null : (Integer) row[2]);
			deductCoinClubDTO.setOutcomeTodate(
					((Timestamp) row[3]).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			deductCoinClubDTO.setPaymentStatus(OutComePaymentStatus.values()[(Integer) row[4]]);
			deductCoinClubDTO.setPaymentDate(row[5] == null ? null
					: ((Timestamp) row[5]).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			deductCoinClubDTO.setBalanceCoin(deductCoinClubDTO.getEarnCoin());
			deductCoinClubDTO.setDeductCoin(deductCoinClubDTO.getEarnCoin());
			deductCoinClubDTO.setAmountReturn(deductCoinClubDTO.getDeductCoin() * price);
			result.add(deductCoinClubDTO);
		}
		PagedResponse<DeductCoinClubDTO> pageResponse = new PagedResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount((int) entitiesPage.getTotalElements());
		return pageResponse;
	}

	public PagedResponse<DeductCoinTrainerDTO> getDeductCoinTrainerReport(long startTime, long endTime, String name,
			Pageable pageable) {
		List<DeductCoinTrainerDTO> result = new ArrayList<DeductCoinTrainerDTO>();
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		Double price = 0d;
		Optional<CreditPackageEntity> creditPackageEntity = creditPackageRepository
				.getUnitPackage(CreditPackageType.UNIT.toValue());
		if (creditPackageEntity.isPresent()) {
			price = creditPackageEntity.get().getTotalPrice();
		}
		Page<Object[]> entitiesPage = name == null ? userOutcomeRepo.getDeductCoinTrainerReport(from, to, pageable)
				: userOutcomeRepo.getDeductCoinTrainerReportWithFilter(from, to, name, pageable);
		List<Object[]> deductCoinTrainer = entitiesPage.getContent();
		for (Object[] row : deductCoinTrainer) {
			DeductCoinTrainerDTO deductCoinTrainerDTO = new DeductCoinTrainerDTO();
			deductCoinTrainerDTO.setPtName(row[0] == null ? "" : row[0].toString());
			deductCoinTrainerDTO.setPtUsername(row[1] == null ? "" : row[1].toString());
			deductCoinTrainerDTO.setEmail(row[2] == null ? "" : row[2].toString());
			deductCoinTrainerDTO.setPhone(row[3] == null ? "" : row[3].toString());
			deductCoinTrainerDTO.setBalanceCoin(row[4] == null ? null : (Integer) row[4]);
			LocalDateTime toDate = ((Timestamp) row[5]).toLocalDateTime();
			String uuid = row[6].toString();
			Integer earnCoin = getEarnCoin(uuid, toDate.minusDays(7), toDate);
			Integer topUp = getTopUp(uuid, toDate.minusDays(7), toDate);
			deductCoinTrainerDTO.setEarnCoin(earnCoin == null ? 0 : earnCoin);
			deductCoinTrainerDTO.setTopUp(topUp == null ? 0 : topUp);
			deductCoinTrainerDTO.setUsedCoin(deductCoinTrainerDTO.getEarnCoin() + deductCoinTrainerDTO.getTopUp()
					- deductCoinTrainerDTO.getBalanceCoin());
			deductCoinTrainerDTO
					.setDeductCoin(deductCoinTrainerDTO.getBalanceCoin() < deductCoinTrainerDTO.getEarnCoin()
							? deductCoinTrainerDTO.getBalanceCoin()
							: deductCoinTrainerDTO.getEarnCoin());
			deductCoinTrainerDTO.setExchange(deductCoinTrainerDTO.getDeductCoin() * price);
			deductCoinTrainerDTO.setOutcomeId(((BigInteger) row[7]).longValue());
			deductCoinTrainerDTO.setOutcomeTodate(toDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			deductCoinTrainerDTO.setPaymentStatus(OutComePaymentStatus.values()[(Integer) row[8]]);
			deductCoinTrainerDTO.setPaymentDate(row[9] == null ? null
					: ((Timestamp) row[9]).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			result.add(deductCoinTrainerDTO);
		}
		PagedResponse<DeductCoinTrainerDTO> pageResponse = new PagedResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount((int) entitiesPage.getTotalElements());
		return pageResponse;
	}

	private Integer getEarnCoin(String uuid, LocalDateTime fromDate, LocalDateTime toDate) {
		return creditWalletTransactionRepository.getEarnCoin(uuid, fromDate, toDate);
	}

	private Integer getTopUp(String uuid, LocalDateTime fromDate, LocalDateTime toDate) {
		return creditWalletTransactionRepository.getTopUp(uuid, fromDate, toDate);
	}

	private void refundUserCreditPackage(Long bookingId, CreditWalletTransactionEntity creditWalletTransEntity) throws Exception {
		List<CreditTransactionType> transactionType = new ArrayList<>();
		transactionType.add(CreditTransactionType.BOOKING_CLASS);
		transactionType.add(CreditTransactionType.BOOKING_ETICKET);
		transactionType.add(CreditTransactionType.BOOKING_SESSION);
		Optional<CreditWalletTransactionEntity> walletTransaction = creditWalletTransactionRepository
				.findByBookingIdAndTransactionTypeIn(bookingId, transactionType);
		if (!walletTransaction.isPresent()) {
			throw new Exception("The credit wallet transaction does not exist");
		}
		CreditWalletTransactionEntity creditWalletTransaction = walletTransaction.get();
		List<UserCreditPackageTransactionEntity> listUserCreditTransaction = userCreditPackageTransationRepository
				.findByCreditWalletTransactionId(creditWalletTransaction.getId());
		for (UserCreditPackageTransactionEntity entity : listUserCreditTransaction) {
			UserCreditPackageEntity userCreditPackage = userCreditPackageRepository.findOne(entity.getUserCreditPackageId());
			
			UserCreditPackageTransactionEntity userCreditTrans = new UserCreditPackageTransactionEntity();
			userCreditTrans.setCreatedDate(LocalDateTime.now());
			userCreditTrans.setCurrentCredit(userCreditPackage.getTotalCredit() - userCreditPackage.getUsedCredit());
			userCreditTrans.setUsedCredit(entity.getUsedCredit() * -1);
			userCreditTrans.setRemainingCredit(userCreditPackage.getTotalCredit() - userCreditPackage.getUsedCredit() + entity.getUsedCredit());
			userCreditTrans.setUserCreditPackageId(userCreditPackage.getId());
			userCreditTrans.setOwnerUuid(userCreditPackage.getOwnerUuid());
			userCreditTrans.setCreditWalletTransactionId(creditWalletTransEntity.getId());
			userCreditTrans.setTransactionType(CreditPackageTransactionType.REFUNDED);
			userCreditPackageTransationRepository.save(userCreditTrans);
			
			userCreditPackage.setUsedCredit(userCreditPackage.getUsedCredit() - entity.getUsedCredit());
			userCreditPackageRepository.save(userCreditPackage);
		}
	}
}
