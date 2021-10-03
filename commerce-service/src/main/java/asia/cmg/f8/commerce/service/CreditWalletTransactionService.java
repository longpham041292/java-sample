package asia.cmg.f8.commerce.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;

@Service
@Transactional
public class CreditWalletTransactionService {

	@Autowired
	CreditWalletTransactionRepository walletTransactionRepo;
	
	private static final Logger LOG = LoggerFactory.getLogger(CreditWalletTransactionService.class);

	/**
	 * 
	 * @param creditWalletId
	 * @param ownerUuid
	 * @param currentCreditBalance
	 * @param creditAmount
	 * @param transactionType
	 * @param transactionStatus
	 * @param description
	 */
	public CreditWalletTransactionEntity createCreditWalletTransaction(final long creditWalletId, 
											final String ownerUuid,
											final String ownerImage,
											final int currentCreditBalance,
											final int creditAmount, 
											final CreditTransactionType transactionType, 
											final CreditTransactionStatus transactionStatus, 
											final String description,
											final List<String> descriptionParams) throws Exception {
		try {
			CreditWalletTransactionEntity walletTransaction = new CreditWalletTransactionEntity();
			
			walletTransaction.setOldCreditBalance(currentCreditBalance);
			walletTransaction.setNewCreditBalance(currentCreditBalance + creditAmount);
			walletTransaction.setCreditAmount(creditAmount);
			walletTransaction.setCreditWalletId(creditWalletId);
			walletTransaction.setDescription(description);
			walletTransaction.setDescriptionParams(CommerceUtils.convertListToStringWithSemicolon(descriptionParams));
			walletTransaction.setOwnerUuid(ownerUuid);
			walletTransaction.setOwnerImage(ownerImage);
			walletTransaction.setTransactionType(transactionType);
			
			return walletTransactionRepo.save(walletTransaction);
		} catch (Exception e) {
			LOG.error("Could not create credit wallet transaction with exception: {}", e.getMessage());
			throw e;
		}
	}
	
	/**
	 * @param walletTransaction
	 */
	public CreditWalletTransactionEntity createCreditWalletTransaction(final CreditWalletTransactionEntity walletTransaction) throws Exception {
		try {
			return walletTransactionRepo.save(walletTransaction);
		} catch (Exception e) {
			LOG.error("Could not create credit wallet transaction with exception: {}", e.getMessage());
			throw e;
		}
	}
}
