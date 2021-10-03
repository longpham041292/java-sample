package asia.cmg.f8.session.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.entity.BasicUserEntity;

/**
 * Created on 11/25/16.
 */
@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface BasicUserRepository extends CrudRepository<BasicUserEntity, String> {
	final int COMPLETED_CODE = 7;
	final int BURNED_CODE = 6;
	final int CANCELLED = 3;
	final int TRAINER_CANCELLED = 4;
	final int EU_CANCELLED = 5;
	final String COUNT_NUM_OF_CONFIRMED = ",(SELECT COUNT(ss.uuid) FROM session_sessions ss WHERE ss.package_uuid = sp.uuid AND ss.pt_uuid = sp.pt_uuid AND ss.user_uuid = sp.user_uuid AND ss.status = 'CONFIRMED') AS num_of_confirmed ";
	final List<Integer> CANCELLED_LIST =  Arrays.asList(CANCELLED, TRAINER_CANCELLED, EU_CANCELLED);
	final List<Integer> COMPLETED_BURNED_STATUS =  Arrays.asList(COMPLETED_CODE, BURNED_CODE);
	String SEARCH_CLIENT_OF_PT = "SELECT client.user_uuid, client.avatar, client.full_name, client.num_of_burned, client.num_of_sessions, client.price, client.commission, client.expired_date, client.status, client.package_uuid, client.username, client.order_code "
			+ ",client.num_of_confirmed, client.order_uuid, client.contract_number "
			+ "FROM (select sp.user_uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions, so.price, so.commission, so.expired_date, sp.status, sp.uuid as package_uuid, su.username, sp.order_uuid, so.contract_number, so.order_code "
			+ COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su RIGHT JOIN session_session_packages sp ON sp.user_uuid = su.uuid "
			+ "  JOIN session_orders so ON sp.order_uuid = so.uuid "
			+ "WHERE sp.pt_uuid = ?1 AND su.full_name LIKE %?2% "
			+ "ORDER BY sp.created_date desc) client GROUP BY client.order_code ";

	String SEARCH_PT_OF_USER = "SELECT trainer.pt_uuid, trainer.avatar, trainer.full_name, trainer.num_of_burned, trainer.num_of_sessions, trainer.price, trainer.commission, trainer.expired_date, trainer.status, trainer.package_uuid, trainer.order_code "
			+ ",trainer.num_of_confirmed, trainer.order_uuid, trainer.contract_number, trainer.total_price "
			+ "FROM (select sp.pt_uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions, if(so.free_order, 0, so.price) AS price, so.price AS total_price, so.commission, so.expired_date, sp.status, sp.uuid as package_uuid, sp.order_uuid, so.contract_number, so.order_code "
			+ COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su RIGHT JOIN session_session_packages sp ON sp.pt_uuid = su.uuid "
			+ "JOIN session_orders so ON sp.order_uuid = so.uuid "
			+ "WHERE sp.user_uuid = ?1 AND su.full_name LIKE %?2% " + "ORDER BY sp.created_date desc) trainer "
			+ "GROUP BY trainer.order_code ";

	String PAGEABLE_QUERY = "\n#pageable\n ";

	Optional<BasicUserEntity> findOneByUuid(final String uuid);

	@Query(value = "SELECT su.uuid, su.avatar, su.full_name, su.city, su.country, su.activated, so.num_of_burned, so.num_of_sessions, so.expired_date, "
			+ "sp.status AS package_status, sp.uuid AS package_uuid, sp.pt_uuid AS trainer_uuid, su.email, su.join_date, su.user_code, su.username " // +
																																						// COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su LEFT JOIN session_orders so ON su.uuid = so.user_uuid "
			+ "LEFT JOIN session_orders so1 ON su.uuid = so1.user_uuid "
			+ "AND (so.order_date < so1.order_date OR (so.order_date = so1.order_date AND so.uuid < so1.uuid)) "
			+ "LEFT JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN ?2 "
			+ "WHERE su.user_type = 'eu' AND so1.order_date IS NULL "
			+ "AND (su.full_name LIKE %?1% or su.username LIKE %?1% or su.email LIKE %?1% or su.user_code LIKE %?1% or su.phone LIKE %?1%) GROUP BY su.uuid \n#pageable\n", nativeQuery = true)
	List<Object[]> listUserWithOrder(final String keyword, final List<String> validPackageStatus,
			final Pageable pageable);
	
	
	@Query(value = "SELECT su.uuid, su.avatar, su.full_name, su.city, su.country, su.activated, so.num_of_burned, so.num_of_sessions, so.expired_date, "
			+ "sp.status AS package_status, sp.uuid AS package_uuid, sp.pt_uuid as trainer_uuid, su.email, su.join_date, su.user_code, su.username " // +
																																						// COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su LEFT JOIN session_orders so ON su.uuid = so.user_uuid "
			+ "LEFT JOIN session_orders so1 ON su.uuid = so1.user_uuid "
			+ "AND (so.order_date < so1.order_date OR (so.order_date = so1.order_date AND so.uuid < so1.uuid)) "
			+ "LEFT JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN ?2 "
			+ "WHERE su.user_type = 'eu' AND so1.order_date IS NULL "
			+ "AND su.join_date BETWEEN ?3 AND ?4 "
			+ "AND (su.full_name LIKE %?1% or su.username LIKE %?1% or su.email LIKE %?1% or su.user_code LIKE %?1% or su.phone LIKE %?1%) GROUP BY su.uuid \n#pageable\n", nativeQuery = true)
	List<Object[]> listUserByTimeRange(final String keyword, final List<String> validPackageStatus,
			final LocalDateTime fromDate, final LocalDateTime toDate, final Pageable pageable );

	@Query(value = "SELECT su.uuid, su.full_name, su.city, su.country, su.activated, so.num_of_burned, so.num_of_sessions, so.expired_date, su.email, su.join_date ,su.username, su.user_code, su.phone "
			+ COUNT_NUM_OF_CONFIRMED + "FROM session_users su LEFT JOIN session_orders so ON su.uuid = so.user_uuid "
			+ "LEFT JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status in ?1 "
			+ "WHERE su.user_type = 'eu' AND su.join_date BETWEEN ?2 AND ?3 AND ((so.order_date IS NULL) "
			+ "OR so.order_date = (SELECT max(order_date) FROM session_orders sub WHERE sub.user_uuid = su.uuid)) "
			+ "ORDER BY join_date DESC", nativeQuery = true)
	List<Object[]> listUserWithOrder(List<String> validPackageStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "SELECT count(distinct(su.uuid)) "
			+ "FROM session_users su JOIN session_session_packages sp ON su.uuid = sp.user_uuid "
			+ "JOIN session_orders so ON sp.order_uuid = so.uuid "
			+ "WHERE su.activated = 1 AND so.expired_date >= CURRENT_DATE " + "and sp.status IN :validPackageStatus "
			+ "AND so.num_of_burned < so.num_of_sessions", nativeQuery = true)
	int countActiveMembers(@Param("validPackageStatus") final List<String> validPackageStatus);

	@Query(value = SEARCH_PT_OF_USER + PAGEABLE_QUERY, nativeQuery = true)
	List<Object[]> listPTsOfUser(final String userUuid, final String keyword, final Pageable pageable);

	@Query(value = "SELECT DISTINCT(trainer.pt_uuid), trainer.avatar, trainer.full_name, trainer.num_of_burned, trainer.num_of_sessions, trainer.price, trainer.commission, trainer.expired_date, trainer.status, trainer.package_uuid, trainer.order_code "
			+ ",trainer.num_of_confirmed, trainer.order_uuid, trainer.contract_number, trainer.total_price "
			+ "FROM (SELECT sp.pt_uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions, if(so.free_order, 0, so.price) AS price, so.price AS total_price, "
			+ "so.commission, so.expired_date, sp.status, sp.uuid AS package_uuid, sp.order_uuid, so.order_code, so.contract_number "
			+ COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su RIGHT JOIN session_session_packages sp ON sp.pt_uuid = su.uuid "
			+ "JOIN session_orders so ON sp.order_uuid = so.uuid "
			+ "WHERE sp.user_uuid = ?1 AND su.full_name like %?2% " + "AND su.activated = 1 "
			+ "AND (so.expired_date is NULL OR so.expired_date >= CURRENT_DATE) "
			+ "AND sp.status in ?3 AND so.num_of_burned < so.num_of_sessions "
			+ "ORDER BY sp.created_date desc ) trainer " + "GROUP BY trainer.order_code "
			+ PAGEABLE_QUERY, nativeQuery = true)
	List<Object[]> listPTsOfUserWithActiveOrder(final String userUuid, final String keyword,
			List<String> validPackageStatus, final Pageable pageable);

	@Query(value = "SELECT status, count(if(UNIX_TIMESTAMP(session_created_time) >= :startTime "
			+ "AND UNIX_TIMESTAMP(session_created_time) < :middleTime , 1, null)) last, "
			+ "COUNT(if(UNIX_TIMESTAMP(session_created_time) >= :middleTime "
			+ "AND UNIX_TIMESTAMP(session_created_time) < :endTime , 1, null)) current "
			+ "FROM session_session_daily_mv " + "WHERE user_uuid = :userUuid AND status IN :status "
			+ "GROUP BY status", nativeQuery = true)
	List<Object[]> calculateSessionOfUserByTimeRange(@Param("userUuid") final String userUuid,
			@Param("startTime") final long startTime, @Param("middleTime") final long middleTime,
			@Param("endTime") final long endTime, @Param("status") final List<String> status);

	@Query(value = "SELECT so.created_date, so.price FROM session_orders so " + "WHERE so.user_uuid = :userUuid "
			+ "AND UNIX_TIMESTAMP(so.created_date) between :startTime and :endTime", nativeQuery = true)
	List<Object[]> getCompletedOrdersOfUserInRange(@Param("userUuid") final String userUuid,
			@Param("startTime") final long startTime, @Param("endTime") final long endTime);

	@Query(value = "SELECT sp.created_date, "
			+ "   sum((if(ss.status != :expireStatus , 1, 0) / so.num_of_sessions) * so.price ) AS revenue,"
			+ "   sum((if(ss.status != :expireStatus , 1, 0) / so.num_of_sessions) * so.price * (100 - so.commission) / 100 ) AS serviceFee "
			+ "FROM session_orders so JOIN session_session_packages sp ON so.uuid = sp.order_uuid "
			+ "   JOIN session_sessions ss ON ss.package_uuid = sp.uuid "
			+ "WHERE sp.pt_uuid = :ptUuid AND UNIX_TIMESTAMP(sp.created_date) BETWEEN :startTime AND :endTime "
			+ "GROUP BY sp.uuid", nativeQuery = true)
	List<Object[]> getCompletedOrdersOfTrainerInRange(@Param("ptUuid") final String ptUuid,
			@Param("startTime") final long startTime, @Param("endTime") final long endTime,
			@Param("expireStatus") final String expireStatus);

	@Query(value = SEARCH_CLIENT_OF_PT + PAGEABLE_QUERY, nativeQuery = true)
	List<Object[]> listClientOfPtWithOrder(final String userId, final String keyword, Pageable pageable);

	@Query(value = "SELECT client.user_uuid, client.avatar, client.full_name, client.num_of_burned, client.num_of_sessions, client.price, client.commission, client.expired_date, client.status, client.package_uuid, client.username, client.order_code "
			+ ",client.num_of_confirmed, client.order_uuid, client.contract_number "
			+ "FROM (select sp.order_uuid, sp.user_uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions, so.price, so.commission, so.expired_date, sp.status, sp.uuid AS package_uuid, su.username, so.order_code, so.contract_number "
			+ COUNT_NUM_OF_CONFIRMED
			+ "FROM session_users su RIGHT JOIN session_session_packages sp ON sp.user_uuid = su.uuid "
			+ "  JOIN session_orders so ON sp.order_uuid = so.uuid "
			+ "WHERE sp.pt_uuid = :userId AND sp.status IN :validStatus AND su.full_name LIKE %:keyword% "
			+ "AND (so.expired_date IS NULL OR (so.num_of_burned < so.num_of_sessions AND so.expired_date >= CURRENT_DATE)) "
			+ "ORDER BY sp.created_date desc) client " + "GROUP BY client.order_code "
			+ "\n#pageable\n ", nativeQuery = true)
	List<Object[]> listClientOfPtWithActiveOrder(@Param("userId") final String userId,
			@Param("keyword") final String keyword, @Param("validStatus") final List<String> validStatus,
			Pageable pageable);

	@Query("SELECT se " + "FROM BasicUserEntity se "
			+ "WHERE (se.uuid = :userUuid OR se.uuid = :trainerUuid) AND se.activated = true")
	List<BasicUserEntity> getActivatedUserByUuid(@Param("userUuid") final String userUuid,
			@Param("trainerUuid") final String trainerUuid);

	@Query("SELECT se " + "FROM BasicUserEntity se " + "WHERE se.userName = :userName ")
	List<BasicUserEntity> getActivatedUserbyUsername(@Param("userName") final String userName);

	@Query("SELECT se " + "FROM BasicUserEntity se " + "WHERE se.usercode = :userCode ")
	List<BasicUserEntity> getUserByUserCode(@Param("userCode") final String userCode);

	@Query("SELECT se FROM BasicUserEntity se WHERE se.userType = 'pt' ")
	List<BasicUserEntity> getUsers(Pageable pageable);

	@Query(value = "SELECT DISTINCT(uuid) FROM session_users ORDER BY Id DESC ", nativeQuery = true)
	List<String> getAllUserUuids();

	@Query(value = "SELECT DISTINCT(uuid) FROM session_users WHERE user_Type = 'eu' AND user_code IS NOT NULL AND user_code != '' ", nativeQuery = true)
	List<String> getEuUserHaveUsercode();
	
	@Query(value = "SELECT DISTINCT(uuid) FROM session_users WHERE user_Type = :userType", nativeQuery = true)
	List<String> getAllUserUuidsByUserType(@Param("userType") final String userType);
	
	@Query(value = 	"SELECT lv.code, lv.pt_booking_credit, lv.pt_commission, lv.pt_burned_commission " + 
					"FROM session_users su JOIN `level` lv ON su.`level` = lv.code " + 
					"WHERE su.uuid = ?1 AND doc_status = 'approved'", nativeQuery = true)
	List<Object[]> getBookingCreditOfApprovedTrainer(final String ptUuid);

	
	@Query(value = "SELECT COUNT(DISTINCT(uuid)) FROM session_users WHERE user_type = ?1", nativeQuery = true)
	int countTotalUser(@Param("userType") final String userType);

	@Query(value = "SELECT count(DISTINCT (su.uuid)) "
					+ "FROM session_users su "
					+ "WHERE su.user_type = :userType "
					+ "AND csb.created_date BETWEEN :start AND :end", nativeQuery = true)
	int countTotalEndUserRegistered(@Param("userType") final String userType, @Param("start") final LocalDateTime start,  @Param("end") final LocalDateTime end);

	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su WHERE user_type = ?1 "
			+ "AND su.join_date BETWEEN ?2 AND ?3", nativeQuery = true)
	int countTotalUserByRangeTime(String userType, LocalDateTime start, LocalDateTime end);

	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su "
			 + "WHERE user_type = 'pt' "
			 + "AND su.doc_approved_date BETWEEN ?1 AND ?2 "
			 + "AND su.doc_status = 'APPROVED'", nativeQuery = true)
	int countApprovedTrainerByRange(LocalDateTime start, LocalDateTime end);

	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su INNER JOIN credit_session_bookings csb ON su.uuid = csb.pt_uuid "
			 + "WHERE su.user_type = 'pt' "
			 + "AND csb.status IN ?3 "
			 + "AND csb.created_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
	int countTotalPtHasBookingByRange(LocalDateTime start, LocalDateTime end, List<Integer> STATUS_QUERY);

	@Query(value = "SELECT COUNT(DISTINCT (su.uuid)) from session_users su "
			+ "INNER JOIN credit_bookings cb "
			+ "ON su.uuid = cb.client_uuid "
			+ "WHERE su.user_type = ?1 "
			+ "AND cb.created_date BETWEEN ?2 AND ?3 ", nativeQuery = true)
	int countActiveUser(String userType, LocalDateTime start, LocalDateTime end);
	
	List<BasicUserEntity> findByUuidIn(List<String> uuids);
}
