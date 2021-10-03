package asia.cmg.f8.report.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.report.dto.TransactionReportDto;
import asia.cmg.f8.report.dto.WalletActivityDto;
import asia.cmg.f8.report.entity.database.CreditTransactionType;
import asia.cmg.f8.report.entity.database.CreditWalletTransactionEntity;
import asia.cmg.f8.report.entity.database.WithdrawalStatus;

public interface CreditWalletTransactionRepository extends JpaRepository<CreditWalletTransactionEntity, Long> {

	
	final String GET_WALLET_ACTIVITIES_QUERY
				= " SELECT "
				+ " NEW asia.cmg.f8.report.dto.WalletActivityDto("
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
				+ " NEW asia.cmg.f8.report.dto.WalletActivityDto("
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
				+ " NEW asia.cmg.f8.report.dto.WalletActivityDto("
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
	
	final String SEARCH_KEYWORD = "AND ( clientUser.fullName LIKE %?3% OR clientUser.userName LIKE %?3% OR ownerUser.fullName LIKE %?3% OR ownerUser.userName LIKE %?3% OR ownerUser.email LIKE %?3% OR ownerUser.userCode LIKE %?3% OR ownerUser.phone LIKE %?3%)";
	
	final String PT_EARNING_AMOUNT_TRANSACTION = "SELECT  A.total, A.owner_uuid, A.minID, A.maxID, B.packageTotal " + 
			"FROM ( " + 
			"	SELECT cwt.owner_uuid , SUM(credit_amount) AS total, MIN(cwt.booking_id ) as minID, MAX(cwt.booking_id ) as maxID  " + 
			"	FROM credit_wallet_transactions cwt " +
			"	WHERE cwt.withdrawed = :withdrawed " + 
			"	AND cwt.owner_uuid != :leepWalletUuid " +
			"	AND cwt.transaction_type IN :earningBookingStatus "+ 
			"   AND cwt.created_date <= :endDate " + 
			"	GROUP BY cwt.owner_uuid " + 
			") A " + 
			"LEFT JOIN ( " + 
			"	SELECT SUM(csb.credit_amount ) AS packageTotal, csb.pt_uuid  " + 
			"	FROM  credit_session_bookings csb " + 
			"	WHERE " + 
			"		csb.credit_booking_id IN  " + 
			"		( " + 
			"			SELECT cwt.booking_id  " + 
			"			FROM credit_wallet_transactions cwt " + 
			"			WHERE cwt.withdrawed = :withdrawed  " + 
			"			AND cwt.owner_uuid != :leepWalletUuid " +
			"			AND cwt.transaction_type IN :earningBookingStatus	 " + 
			"		) " + 
			"	GROUP BY csb.pt_uuid " + 
			") B " + 
			" ON A.owner_uuid = B.pt_uuid";
	
	final String CLUB_EARNING_AMOUNT_TRANSACTION = "SELECT  A.total, A.owner_uuid, A.minID, A.maxID, B.packageTotal   " +  
			"FROM ( " + 
			"    SELECT  cwt.owner_uuid , SUM(credit_amount) AS total, MIN(cwt.booking_id ) as minID, MAX(cwt.booking_id ) as maxID " + 
			"    FROM credit_wallet_transactions cwt " + 
			"    WHERE cwt.withdrawed = :withdrawed " + 
			"    AND cwt.transaction_type IN :earningBookingStatus " + 
			"	 AND cwt.owner_uuid != :leepWalletUuid " +
			"    AND cwt.created_date <= :endDate " + 
			"    GROUP BY cwt.owner_uuid " + 
			") A " + 
			"INNER JOIN ( " + 
			"    SELECT SUM(cb.credit_amount ) AS packageTotal, cb.studio_uuid , cb.studio_name " + 
			"    FROM credit_bookings cb " + 
			"    WHERE   " + 
			"        cb.id IN " + 
			"        (   " + 
			"            SELECT cwt.booking_id " + 
			"            FROM credit_wallet_transactions cwt "+ 
			"            WHERE cwt.withdrawed = :withdrawed "+ 
			"			 AND cwt.owner_uuid != :leepWalletUuid " + 
			"            AND cwt.transaction_type IN :earningBookingStatus " + 
			"        )   " + 
			"    GROUP BY cb.studio_uuid " + 
			") B " + 
			" ON A.owner_uuid = B.studio_uuid ";
	
	@Query(value = GET_WALLET_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);
	
	@Query(value = GET_WALLET_CASHIN_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletCashInActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);
	
	@Query(value = GET_WALLET_CASHOUT_ACTIVITIES_QUERY)
	Page<WalletActivityDto> getWalletCashOutActivities(@Param("owner_uuid") String owner_uuid, Pageable pageable);

	@Query(value = " SELECT "
			+ " NEW asia.cmg.f8.report.dto.TransactionReportDto("
			+ " transaction, "
			+ " ownerUser, "
			+ " creditBooking , "
			+ " clientUser "
			+ ")"		
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " INNER JOIN BasicUserEntity ownerUser ON ownerUser.uuid = transaction.ownerUuid "
			+ " LEFT JOIN CreditBookingEntity creditBooking ON transaction.bookingId = creditBooking.id "
			+ " LEFT JOIN BasicUserEntity clientUser ON creditBooking.clientUuid = clientUser.uuid"
		    + " WHERE transaction.createdDate BETWEEN ?1 AND ?2 "
		    + " AND transaction.ownerUuid != ?4 " + SEARCH_KEYWORD)
	Page<TransactionReportDto> getClientWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, Pageable pageable);
	
	@Query(value = " SELECT "
			+ " NEW asia.cmg.f8.report.dto.TransactionReportDto("
			+ " transaction, "
			+ " ownerUser, "
			+ " creditBooking, "
			+ " clientUser "
			+ ")"		
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " INNER JOIN BasicUserEntity ownerUser ON ownerUser.uuid = transaction.ownerUuid "
			+ " LEFT JOIN CreditBookingEntity creditBooking ON transaction.bookingId = creditBooking.id "
			+ " LEFT JOIN BasicUserEntity clientUser ON creditBooking.clientUuid = clientUser.uuid "
			+ " WHERE transaction.createdDate BETWEEN ?1 AND ?2 "
		    + " AND ownerUser.userType = 'pt' "
		    + " AND transaction.ownerUuid != ?4 " + SEARCH_KEYWORD)
	Page<TransactionReportDto> getTrainerWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, Pageable pageable);
	
	@Query(value = " SELECT "
			+ " NEW asia.cmg.f8.report.dto.TransactionReportDto("
			+ " transaction, "
			+ " ownerUser, "
			+ " creditBooking, "
			+ " clientUser "
			+ ")"		
			+ " FROM CreditWalletTransactionEntity transaction "
			+ " LEFT JOIN CreditBookingEntity creditBooking ON transaction.bookingId = creditBooking.id "
			+ " LEFT JOIN BasicUserEntity ownerUser ON ownerUser.uuid = creditBooking.clientUuid "
			+ " LEFT JOIN BasicUserEntity clientUser ON creditBooking.clientUuid = clientUser.uuid "
			+ " WHERE transaction.createdDate BETWEEN ?1 AND ?2 "
			+ " AND transaction.transactionType IN ?5 "
		    + " AND transaction.ownerUuid != ?4 "
		    + " AND creditBooking.studioName LIKE %?3% ")
	List<TransactionReportDto> getClubWalletActivitiesReport(LocalDateTime start, LocalDateTime end,
			String keyword, String leepWalletUuid, List<CreditTransactionType> type,  Pageable pageable);
	
	

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
			+ "SET transaction.withdrawed = :withdrawed "
			+ "WHERE transaction.transactionType IN :earningBookingStatus "
			+ "AND transaction.createdDate <= :endDate "
			+ "AND transaction.ownerUuid = :ownerUuid ")
	void updateWithdrawedForEarningTransactionByOwnerUuid(@Param("ownerUuid") String ownerUuid, 
														@Param("earningBookingStatus") List<CreditTransactionType> earningBookingStatus,
														@Param("withdrawed") WithdrawalStatus withdrawed, 
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
	
	@Query(value = "SELECT SUM(cwt.credit_amount) FROM credit_wallet_transactions cwt WHERE cwt.transaction_type in (4, 10, 11) AND cwt.owner_uuid = ?1 AND cwt.created_date BETWEEN ?2 AND ?3", nativeQuery = true)
	Integer getUsedCoin(String uuid, LocalDateTime fromDate, LocalDateTime toDate);
}
