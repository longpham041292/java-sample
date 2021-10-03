package asia.cmg.f8.commerce.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.commerce.dto.TransactionReportDto;
import asia.cmg.f8.commerce.dto.WalletActivityDto;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.WithdrawalStatus;

public interface CreditWalletTransactionRepository extends JpaRepository<CreditWalletTransactionEntity, Long> {

	
	final String GET_WALLET_ACTIVITIES_QUERY
				= " SELECT "
				+ " NEW asia.cmg.f8.commerce.dto.WalletActivityDto("
				+ " transaction.id, "
				+ " transaction.creditWalletId, "
				+ " transaction.ownerUuid, "
				+ " transaction.ownerImage, "
				+ " transaction.partnerUuid, "
				+ " transaction.partnerName, "
				+ " transaction.partnerCode, "
				+ " transaction.partnerImage, "
				+ " transaction.creditAmount, "
				+ " transaction.transactionType, "
				+ " transaction.transactionStatus, "
				+ " transaction.descriptionParams, "
				+ " UNIX_TIMESTAMP(transaction.createdDate)"
				+ ")"		
				+ " FROM CreditWalletTransactionEntity transaction"
			    + " WHERE transaction.ownerUuid =:owner_uuid"
			    + " ORDER BY transaction.id DESC";
	
	final String GET_WALLET_CASHOUT_ACTIVITIES_QUERY
				= " SELECT "
				+ " NEW asia.cmg.f8.commerce.dto.WalletActivityDto("
				+ " transaction.id, "
				+ " transaction.creditWalletId, "
				+ " transaction.ownerUuid, "
				+ " transaction.ownerImage, "
				+ " transaction.partnerUuid, "
				+ " transaction.partnerName, "
				+ " transaction.partnerCode, "
				+ " transaction.partnerImage, "
				+ " transaction.creditAmount, "
				+ " transaction.transactionType, "
				+ " transaction.transactionStatus, "
				+ " transaction.descriptionParams, "
				+ " UNIX_TIMESTAMP(transaction.createdDate)"
				+ ")"		
				+ " FROM CreditWalletTransactionEntity transaction"
			    + " WHERE transaction.ownerUuid =:owner_uuid AND transaction.creditAmount < 0"
			    + " ORDER BY transaction.id DESC";
	
	final String GET_WALLET_CASHIN_ACTIVITIES_QUERY
				= " SELECT "
				+ " NEW asia.cmg.f8.commerce.dto.WalletActivityDto("
				+ " transaction.id, "
				+ " transaction.creditWalletId, "
				+ " transaction.ownerUuid, "
				+ " transaction.ownerImage, "
				+ " transaction.partnerUuid, "
				+ " transaction.partnerName, "
				+ " transaction.partnerCode, "
				+ " transaction.partnerImage, "
				+ " transaction.creditAmount, "
				+ " transaction.transactionType, "
				+ " transaction.transactionStatus, "
				+ " transaction.descriptionParams, "
				+ " UNIX_TIMESTAMP(transaction.createdDate)"
				+ ")"		
				+ " FROM CreditWalletTransactionEntity transaction"
			    + " WHERE transaction.ownerUuid =:owner_uuid AND transaction.creditAmount > 0"
			    + " ORDER BY transaction.id DESC";
	
	final String SEARCH_KEYWORD = "AND (userEntity.fullName LIKE %?3% OR userEntity.userName LIKE %?3% OR userEntity.email LIKE %?3% OR userEntity.userCode LIKE %?3% OR userEntity.phone LIKE %?3%)";
	
	final String PT_EARNING_AMOUNT_TRANSACTION = 
			"   SELECT cwt.owner_uuid , SUM(cwt.credit_amount) AS total, MIN(cwt.id ) as minID, MAX(cwt.id ) as maxID " + 
			"	FROM credit_wallet_transactions cwt " +
			"	WHERE cwt.withdrawed = :withdrawed " + 
			"	AND cwt.owner_uuid != :leepWalletUuid " +
			"	AND cwt.transaction_type IN :earningBookingStatus "+ 
			"   AND cwt.created_date <= :endDate " + 
			"	GROUP BY cwt.owner_uuid ";
	
	final String CLUB_EARNING_AMOUNT_TRANSACTION = 
			"    SELECT  cwt.owner_uuid , SUM(cwt.credit_amount) AS total, MIN(cwt.id ) as minID, MAX(cwt.id ) as maxID " + 
			"    FROM credit_wallet_transactions cwt " + 
			"    WHERE cwt.withdrawed = :withdrawed " + 
			"    AND cwt.transaction_type IN :earningBookingStatus " + 
			"	 AND cwt.owner_uuid != :leepWalletUuid " +
			"    AND cwt.created_date <= :endDate " + 
			"    GROUP BY cwt.owner_uuid ";
	
	@Query(value = GET_WALLET_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);
	
	@Query(value = GET_WALLET_CASHIN_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletCashInActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);
	
	@Query(value = GET_WALLET_CASHOUT_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletCashOutActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);

	@Query(value = " SELECT "
			+ " NEW asia.cmg.f8.commerce.dto.TransactionReportDto("
			+ " transaction, "
			+ " userEntity"
			+ ")"		
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " INNER JOIN BasicUserEntity userEntity ON userEntity.uuid = transaction.ownerUuid "
		    + " WHERE transaction.createdDate BETWEEN ?1 AND ?2 "
		    + " AND transaction.ownerUuid != ?4 " + SEARCH_KEYWORD)
	Page<TransactionReportDto> getClientWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, Pageable pageable);
	
	@Query(value = " SELECT "
			+ " NEW asia.cmg.f8.commerce.dto.TransactionReportDto("
			+ " transaction, "
			+ " userEntity"
			+ ")"		
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " INNER JOIN BasicUserEntity userEntity ON userEntity.uuid = transaction.ownerUuid "
		    + " WHERE transaction.createdDate BETWEEN ?1 AND ?2 "
		    + " AND userEntity.userType = 'pt' "
		    + " AND transaction.ownerUuid != ?4 " + SEARCH_KEYWORD)
	Page<TransactionReportDto> getTrainerWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, Pageable pageable);
	
	@Query(value = " SELECT "
			+ " cwt.credit_wallet_id, cwt.owner_uuid, cb.studio_name, "
			+ " su.full_name, cwt.credit_amount, cwt.transaction_type, "
			+ " cwt.transaction_status, cwt.created_date, cwt.booking_id, su.username"		
			+ " FROM credit_wallet_transactions cwt "
			+ " LEFT JOIN credit_bookings cb ON cb.id = cwt.booking_id "
			+ " LEFT JOIN session_users su ON su.uuid = cb.client_uuid "
		    + " WHERE cwt.created_date BETWEEN ?1 AND ?2 "
		    + " AND cwt.transaction_type IN ?5 "
		    + " AND cwt.owner_uuid != ?4 "
		    + " AND cb.studio_name LIKE %?3% \n-- #pageable\n  ", nativeQuery = true)
	List<Object[]> getClubWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, List<Integer> type,  Pageable pageable);
	
	

	@Query(value = PT_EARNING_AMOUNT_TRANSACTION, nativeQuery = true)
	
	List<Object[]> getListEarningAmountTransaction(@Param("earningBookingStatus") List<Integer> earningBookingStatus,
													@Param("withdrawed")  List<Integer> withdrawed,
													@Param("endDate") LocalDateTime endDate,
													@Param("leepWalletUuid") String leepWalletUuid);
	
	@Query(value = CLUB_EARNING_AMOUNT_TRANSACTION, nativeQuery = true)
	List<Object[]> getListClubEarningAmountTransaction(@Param("earningBookingStatus") List<Integer> earningBookingStatus,
													@Param("withdrawed")  List<Integer> withdrawed,
													@Param("endDate") LocalDateTime endDate,
													@Param("leepWalletUuid") String leepWalletUuid);
	
	@Modifying
	@Query(value = "UPDATE CreditWalletTransactionEntity transaction "
			+ "SET transaction.withdrawed = :newStatus "
			+ "WHERE transaction.transactionType IN :earningBookingStatus "
			+ "AND transaction.createdDate <= :endDate "
			+ "AND transaction.withdrawed = :oldStatus "
			+ "AND transaction.ownerUuid = :ownerUuid ")
	void updateWithdrawedForEarningTransactionByOwnerUuid(@Param("ownerUuid") String ownerUuid, 
														@Param("earningBookingStatus") List<CreditTransactionType> earningBookingStatus,
														@Param("newStatus") WithdrawalStatus newStatus, 
														@Param("oldStatus") WithdrawalStatus oldStatus, 
														@Param("endDate") LocalDateTime endDate);

	@Query(value = "SELECT transaction "
			+ "FROM CreditWalletTransactionEntity transaction "
			+ "INNER JOIN BasicUserEntity user ON transaction.ownerUuid = user.uuid "
			+ "WHERE user.userType = 'pt' "
			+ "AND user.uuid = :ptUuid "
			+ "AND transaction.transactionType IN :earningBookingStatus "
			+ "AND transaction.createdDate BETWEEN :start AND :end ")
	List<CreditWalletTransactionEntity> getAllCreditSesionsBookingOfTrainer(@Param("start") LocalDateTime start, 
																			@Param("end") LocalDateTime end, 
																			@Param("ptUuid") String ptUuid, 
																			@Param("earningBookingStatus") List<CreditTransactionType> earningBookingStatus);
	
	@Query(value = "SELECT transaction "
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " INNER JOIN BasicUserEntity user ON transaction.ownerUuid = user.uuid "
			+ " WHERE user.uuid = :uuid "
			+ " AND transaction.creditAmount < 0 "
			+ " AND transaction.createdDate BETWEEN :start AND :end ")
	List<CreditWalletTransactionEntity> getClientSpendingByRangeTime(@Param("start") LocalDateTime start, 
																			@Param("end") LocalDateTime end, 
																			@Param("uuid") String ptUuid);
	@Query(value = "SELECT SUM(cwt.credit_amount) FROM credit_wallet_transactions cwt WHERE cwt.transaction_type IN (5,6) AND cwt.owner_uuid = ?1 AND cwt.created_date BETWEEN ?2 AND ?3", nativeQuery = true)
	Integer getEarnCoin(String uuid, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = "SELECT SUM(cwt.credit_amount) FROM credit_wallet_transactions cwt WHERE cwt.transaction_type = 1 AND cwt.owner_uuid = ?1 AND cwt.created_date BETWEEN ?2 AND ?3", nativeQuery = true)
	Integer getTopUp(String uuid, LocalDateTime fromDate, LocalDateTime toDate);
	
	Optional<CreditWalletTransactionEntity> findByBookingIdAndTransactionTypeIn(Long bookingId, List<CreditTransactionType> transactionType);
}
