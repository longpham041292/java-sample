package asia.cmg.f8.report.service;

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

import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.report.config.ReportServiceProperties;
import asia.cmg.f8.report.dto.ClubProfitSharingReportDTO;
import asia.cmg.f8.report.dto.DeductCoinClubDTO;
import asia.cmg.f8.report.dto.DeductCoinTrainerDTO;
import asia.cmg.f8.report.dto.TopUpReportDto;
import asia.cmg.f8.report.dto.TrainerProfitSharingReportDTO;
import asia.cmg.f8.report.dto.TransactionReportDto;
import asia.cmg.f8.report.dto.UserCreditFlowStatisticDto;
import asia.cmg.f8.report.dto.WalletActivityDto;
import asia.cmg.f8.report.entity.database.ClubOutcomeEntity;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;
import asia.cmg.f8.report.entity.database.CreditPackageEntity;
import asia.cmg.f8.report.entity.database.CreditPackageType;
import asia.cmg.f8.report.entity.database.CreditTransactionStatus;
import asia.cmg.f8.report.entity.database.CreditTransactionType;
import asia.cmg.f8.report.entity.database.CreditWalletTransactionEntity;
import asia.cmg.f8.report.entity.database.OutComePaymentStatus;
import asia.cmg.f8.report.entity.database.UserCreditPackageEntity;
import asia.cmg.f8.report.entity.database.UserOutcomeEntity;
import asia.cmg.f8.report.entity.database.WalletCashFlow;
import asia.cmg.f8.report.entity.database.WithdrawalStatus;
import asia.cmg.f8.report.repository.ClubOutcomeRepository;
import asia.cmg.f8.report.repository.CreditBookingRepository;
import asia.cmg.f8.report.repository.CreditPackageRepository;
import asia.cmg.f8.report.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.report.repository.OrderRepository;
import asia.cmg.f8.report.repository.UserCreditPackageRepository;
import asia.cmg.f8.report.repository.UserOutcomeRepository;

@Service
public class FinanceReportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceReportService.class);

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
	private UserCreditPackageRepository userCreditPackageRepository;

	@Autowired
	private UserOutcomeRepository userOutcomeRepo;

	@Autowired
	private ClubOutcomeRepository clubOutcomeRepo;

	@Autowired
	private ReportServiceProperties reportProperties;

	@Autowired
	private CreditPackageRepository creditPackageRepository;
	
	@Autowired
	private CreditBookingRepository creditBookingRepo;

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


	/**
	 * @author phong
	 * @param cashFlow, start, end, keyword, pageable
	 * @return
	 */
	public Page<TopUpReportDto> getTopUpActivitiesAllUser(LocalDateTime start, LocalDateTime end, String keyword,
			Pageable pageable) {
		return orderRepo.getTopUpReport(start, end, keyword, CreditPackageType.UNIT, pageable);
	}

	public Page<TransactionReportDto> getClientWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		return creditWalletTransactionRepository.getClientWalletActivitiesReport(start, end, keyword,
				reportProperties.getLeepAccountUuid(), pageable);
	}

	public List<TransactionReportDto> getClubWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		List<CreditTransactionType> clubStatus = Arrays.asList(
				CreditTransactionType.PAY_COMPLETED_ETICKET,
				CreditTransactionType.PAY_BURNED_ETICKET,
				CreditTransactionType.PAY_COMPLETED_ETICKET,
				CreditTransactionType.PAY_COMPLETED_CLASS);
		return creditWalletTransactionRepository.getClubWalletActivitiesReport(start, end, keyword,
				reportProperties.getLeepAccountUuid(), clubStatus, pageable);
//		return raws.stream().map(this::createClientWalletTransactionReportDto).collect(Collectors.toList());

	}
	
	public Page<TransactionReportDto> getTrainerWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, Pageable pageable) {
		return creditWalletTransactionRepository.getTrainerWalletActivitiesReport(start, end, keyword,
				reportProperties.getLeepAccountUuid(), pageable);
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
					: Math.round(((BigDecimal) row[2]).intValue() / (1 - reportProperties.getPit())));
			clubProfitSharingReportDTO.setTotalAmount(row[3] == null ? 0 : ((BigDecimal) row[3]).intValue());
			clubProfitSharingReportDTO.setToDate(row[4] == null ? "" : row[4].toString());
			clubProfitSharingReportDTO.setVatAmount(
					Math.round(clubProfitSharingReportDTO.getTotalAmount() / reportProperties.getVat()));
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
					: Math.round(((BigDecimal) row[5]).intValue() / (1 - reportProperties.getPit())));
			trainerProfitSharingReportDTO.setVatAmount(
					Math.round(trainerProfitSharingReportDTO.getTotalAmount() / reportProperties.getVat()));
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
			trainerProfitSharingReportDTO.setToDate(row[6] == null ? "" : row[6].toString());
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
			deductCoinClubDTO.setToDate(row[3] == null ? "" : row[3].toString());
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
			Integer usedCoin = getUsedCoin(uuid, toDate.minusDays(7), toDate);
			deductCoinTrainerDTO.setEarnCoin(earnCoin == null ? 0 : earnCoin);
			deductCoinTrainerDTO.setTopUp(topUp == null ? 0 : topUp);
			deductCoinTrainerDTO.setUsedCoin(usedCoin == null ? 0 : usedCoin);
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
			deductCoinTrainerDTO.setToDate(toDate.toString());
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
	
	private Integer getUsedCoin(String uuid, LocalDateTime fromDate, LocalDateTime toDate) {
		return creditWalletTransactionRepository.getUsedCoin(uuid, fromDate, toDate);
	}

}
