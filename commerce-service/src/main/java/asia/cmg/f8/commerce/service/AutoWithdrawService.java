package asia.cmg.f8.commerce.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.entity.credit.ClubOutcomeEntity;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.OutComePaymentStatus;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.UserOutcomeEntity;
import asia.cmg.f8.commerce.entity.credit.WithdrawalStatus;
import asia.cmg.f8.commerce.repository.ClubOutcomeRepository;
import asia.cmg.f8.commerce.repository.CreditWalletRepository;
import asia.cmg.f8.commerce.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.repository.UserOutcomeRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;

@Service
public class AutoWithdrawService {
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
	private CommerceProperties commerceProperties;
	
	@Autowired
	private CreditWalletRepository creditWalletRepository;
	
	
	@Autowired
	private UserOutcomeRepository userOutcomeRepo;

	@Autowired
	private ClubOutcomeRepository clubOutcomeRepo;
	
	@Autowired
	private UserCreditPackageRepository userCreditPackageRepository;
	
	/**
	 * @author phong
	 * @param end
	 * @return
	 */
	@Transactional
	public Boolean handleAutoWithdrawalUserCreditByWeeklyJob(LocalDateTime endDate) {
		List<Object[]> earningTransactionList = creditWalletTransactionRepository.getListEarningAmountTransaction(
				earningSessionBookingStatus, pendingStatus, endDate, commerceProperties.getLeepAccountUuid());
		LOGGER.info("Earning transactions list: " + earningTransactionList.size());
		earningTransactionList.forEach(row -> {
			String ownerUuid = (String) row[0];
			Integer earningAmount = ((BigDecimal) row[1]).intValue();
			Integer bookingId = 0;
			Long fromWalletTransaction = ((BigInteger) row[2]).longValue();
			Long toWalletTransaction = ((BigInteger) row[3]).longValue();
			CreditWalletEntity currentCreditWallet = this.getWalletByOwnerUuid(ownerUuid);
			if (earningAmount > 0) {
				try {
					// subtract wallet credit
					String description = CreditTransactionType.WITHDRAW_CREDITS.name();
					List<String> descriptionParams = Arrays.asList(endDate.format(DATE_FORMATTER));
					Integer  balanceBefore =  currentCreditWallet.getAvailableCredit();
					Integer subtractCreditAmount = (currentCreditWallet.getAvailableCredit() > earningAmount)
							? earningAmount
							: currentCreditWallet.getAvailableCredit();
					Integer balanceAfter =  balanceBefore - subtractCreditAmount;
					CreditWalletTransactionEntity creditTransaction = this.subtractClientCredits(ownerUuid, bookingId,
							subtractCreditAmount, CreditTransactionType.WITHDRAW_CREDITS, description, descriptionParams);
					if (creditTransaction.getCreditWalletId() != 0) {
						// add new record user outcome
						UserOutcomeEntity outcomeRecord = new UserOutcomeEntity();
						outcomeRecord.setCreditAmount(earningAmount);
						outcomeRecord.setToDate(endDate);
						outcomeRecord.setOwneUuid(ownerUuid);
						outcomeRecord.setFromWalletTransactionId(fromWalletTransaction);
						outcomeRecord.setToWalletTransactionId(toWalletTransaction);
						outcomeRecord.setBalanceBefore(balanceBefore);
						outcomeRecord.setBalanceAfter(balanceAfter);
						outcomeRecord.setPaymentStatus(OutComePaymentStatus.PENDING);
						userOutcomeRepo.save(outcomeRecord);

						// Update all earning transaction by ownerUuid
						creditWalletTransactionRepository.updateWithdrawedForEarningTransactionByOwnerUuid(ownerUuid,
								earningSessionBookingStatusEntire, WithdrawalStatus.WITHDRAWED, WithdrawalStatus.PENDING , endDate);
						LOGGER.info(
								"[WalletService] - handleAutoWithdrawalUserCreditByWeeklyJob success for: ID: {}, amount: {} End Date {}",
								ownerUuid, earningAmount, endDate);
					}
				} catch (Exception e) {
					LOGGER.error(
							"[WalletService] - handleAutoWithdrawalUserCreditByWeeklyJob error detail: ID: {}, amount: {} End Date {} Message {}",
							ownerUuid, earningAmount, endDate, e.getMessage());
					return;
				}
			}
		});

		return true;
	}


	/**
	 * @author phong
	 * @param end
	 * @return
	 */
	@Transactional
	public Boolean handleAutoWithdrawalClubCreditByWeeklyJob(LocalDateTime endDate) {
		List<Object[]> earningTransactionList = creditWalletTransactionRepository.getListClubEarningAmountTransaction(
				earningClubBookingStatus, pendingStatus, endDate, commerceProperties.getLeepAccountUuid());
		LOGGER.info("Earning transactions list: " + earningTransactionList.size());
		earningTransactionList.forEach(row -> {
			String ownerUuid = (String) row[0];
			Integer earningAmount = ((BigDecimal) row[1]).intValue();
			Integer bookingId = 0;
			Long fromWalletTransaction = ((BigInteger) row[2]).longValue();
			Long toWalletTransaction = ((BigInteger) row[3]).longValue();

			CreditWalletEntity currentCreditWallet = this.getWalletByOwnerUuid(ownerUuid);
			if (earningAmount > 0) {
				try {
					// subtract wallet credit
					String description = CreditTransactionType.WITHDRAW_CREDITS.name();
					List<String> descriptionParams = Arrays.asList(endDate.format(DATE_FORMATTER));
					Integer  balanceBefore =  currentCreditWallet.getAvailableCredit();
					Integer subtractCreditAmount = (currentCreditWallet.getAvailableCredit() > earningAmount)
							? earningAmount
							: currentCreditWallet.getAvailableCredit();
					Integer  balanceAfter =  balanceBefore - subtractCreditAmount;
					CreditWalletTransactionEntity creditTransaction = this.subtractClientCredits(ownerUuid, bookingId,
							subtractCreditAmount, CreditTransactionType.WITHDRAW_CREDITS, description,
							descriptionParams);
					if (creditTransaction.getCreditWalletId() != 0) {
						// add new record user outcome
						ClubOutcomeEntity outcomeRecord = new ClubOutcomeEntity();
						outcomeRecord.setCreditAmount(earningAmount);
						outcomeRecord.setToDate(endDate);
						outcomeRecord.setOwneUuid(ownerUuid);
						outcomeRecord.setFromWalletTransactionId(fromWalletTransaction);
						outcomeRecord.setToWalletTransactionId(toWalletTransaction);
						outcomeRecord.setBalanceBefore(balanceBefore);
						outcomeRecord.setBalanceAfter(balanceAfter);
						outcomeRecord.setPaymentStatus(OutComePaymentStatus.PENDING);
						clubOutcomeRepo.save(outcomeRecord);

						// Update all earning transaction by ownerUuid
						creditWalletTransactionRepository.updateWithdrawedForEarningTransactionByOwnerUuid(ownerUuid,
								earningClubBookingStatusEntire, WithdrawalStatus.WITHDRAWED, WithdrawalStatus.PENDING, endDate);
						LOGGER.info(
								"[WalletService] - handleAutoWithdrawalClubCreditByWeeklyJob success for: ID: {}, amount: {} End Date {}",
								ownerUuid, earningAmount, endDate);
					}
				} catch (Exception e) {
					LOGGER.error(
							"[WalletService] - handleAutoWithdrawalClubCreditByWeeklyJob error detail: ID: {}, amount: {} End Date {} Message {}",
							ownerUuid, earningAmount, endDate, e.getMessage());
					return;
				}
			}
		});
		return true;
	}
	
	public CreditWalletEntity getWalletByOwnerUuid(final String ownerUuid) {
		try {
			return creditWalletRepository.findByOwnerUuid(ownerUuid);
		} catch (Exception e) {
			return null;
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

			return walletTransaction;
		} catch (Exception e) {
			throw e;
		}
	}

}
