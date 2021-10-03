package asia.cmg.f8.commerce.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackagesNotificationsEntity;
import asia.cmg.f8.commerce.repository.BasicUserRepository;
import asia.cmg.f8.commerce.repository.CreditWalletRepository;
import asia.cmg.f8.commerce.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.commerce.repository.OrderCreditPackageRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageNotificationRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;

@Service
@Transactional(readOnly = true)
public class InternalWalletCreditService {

	private static final Logger LOG = LoggerFactory.getLogger(InternalWalletCreditService.class);
	public static final Integer ONE_DAY = 1;
	public static final Integer ONE_WEEK = 7;
	public static final Integer TWO_WEEK = 14;
	public static final Integer ONE_MONTH = 30;

	@Autowired
	private UserCreditPackageRepository userCreditPackageRepo;

	@Autowired
	private CreditWalletRepository walletRepo;

	@Autowired
	private CreditWalletTransactionRepository walletTransactionRepo;

	@Autowired
	private BasicUserRepository basicUserRepo;

	@Autowired
	private UserCreditPackageNotificationRepository userCreditPackageNotificationRepo;

	@Autowired
	private CommerceEventService commerceEventService;

	@Autowired
	private OrderCreditPackageRepository orderCreditPackageRepo;

	@Transactional(readOnly = false)
	public void processExpiredPurchasedCreditPackages() throws Exception {
		try {
			List<UserCreditPackageEntity> expiredPackages = userCreditPackageRepo.getExpiredCreditPackages();

			for (UserCreditPackageEntity userCreditPackageEntity : expiredPackages) {
				LOG.info("[processExpiredPurchasedCreditPackages] Processing expired purchased package: {}",
						userCreditPackageEntity.toString());

				int expiredCredit = userCreditPackageEntity.getTotalCredit() - userCreditPackageEntity.getUsedCredit();
				String ownerUuid = userCreditPackageEntity.getOwnerUuid();
				CreditWalletEntity wallet = walletRepo.findByOwnerUuid(ownerUuid);

				if (wallet != null) {
					// Subtract credit in the wallet
					BasicUserEntity userInfo = basicUserRepo.findOneByUuid(ownerUuid).get();
					int currentCredits = wallet.getAvailableCredit();
					int updatedWalletCredits = currentCredits - expiredCredit;
					if (updatedWalletCredits < 0) {
						updatedWalletCredits = 0;
					}
					wallet.setAvailableCredit(updatedWalletCredits);
					wallet.setTotalCredit(updatedWalletCredits);
					walletRepo.save(wallet);

					// Update expired package
					userCreditPackageEntity.setUsedCredit(userCreditPackageEntity.getTotalCredit());
					userCreditPackageRepo.save(userCreditPackageEntity);

					// Record a wallet transaction
					CreditWalletTransactionEntity walletTransaction = new CreditWalletTransactionEntity();
					long expiredDateInMilis = userCreditPackageEntity.getExpiredDate().atZone(ZoneId.systemDefault())
							.toInstant().toEpochMilli();
					List<String> descriptionParams = Arrays.asList(
							String.valueOf(userCreditPackageEntity.getOrder().getOrderCreditPackageEntries().get(0).getCreditPackage().getCreditType()),
							String.valueOf(expiredDateInMilis));

					walletTransaction.setCreditAmount(expiredCredit);
					walletTransaction.setCreditWalletId(wallet.getId());
					walletTransaction.setDescription(PaymentConstant.CREDIT_EXPIRED);
					walletTransaction
							.setDescriptionParams(CommerceUtils.convertListToStringWithSemicolon(descriptionParams));
					walletTransaction.setNewCreditBalance(updatedWalletCredits);
					walletTransaction.setOldCreditBalance(currentCredits);
					walletTransaction.setOwnerImage(userInfo.getAvatar());
					walletTransaction.setOwnerUuid(ownerUuid);
					walletTransaction.setTransactionType(CreditTransactionType.EXPIRED_CREDITS);
					walletTransactionRepo.save(walletTransaction);

					LOG.info("[processExpiredPurchasedCreditPackages] Already processed expired purchased package: {}",
							userCreditPackageEntity.toString());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional(readOnly = false)
	public void expiredUserCreditPackageNotification() {
		List<UserCreditPackageEntity> unExpiredPackages = userCreditPackageRepo.getUnexpiredCreditPackage();
		for (UserCreditPackageEntity userCreditPackage : unExpiredPackages) {
			List<UserCreditPackagesNotificationsEntity> listNotification = userCreditPackageNotificationRepo
					.findByUserCreditPackage(userCreditPackage);
			if (listNotification.size() < 4) {
				Integer remainDays = (int) ChronoUnit.DAYS.between(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0),
						userCreditPackage.getExpiredDate().withHour(0).withMinute(0).withSecond(0).withNano(0));
				if (remainDays == ONE_MONTH || remainDays == TWO_WEEK || remainDays == ONE_WEEK
						|| remainDays == ONE_DAY) {
					OrderCreditEntryEntity orderCredit = orderCreditPackageRepo
							.findByOrderId(userCreditPackage.getOrder().getId());
					Boolean notification = commerceEventService
							.publishNotificationExpiredCreditPackageEvent(orderCredit);
					if (notification) {
						UserCreditPackagesNotificationsEntity creditPackageNotificationEntity = new UserCreditPackagesNotificationsEntity();
						creditPackageNotificationEntity.setUserCreditPackage(userCreditPackage);
						creditPackageNotificationEntity.setExpiredRemainingDay(remainDays);
						userCreditPackageNotificationRepo.save(creditPackageNotificationEntity);
					}
				}
			}
		}
	}
}
