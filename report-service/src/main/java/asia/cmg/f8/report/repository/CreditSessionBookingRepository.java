package asia.cmg.f8.report.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.report.dto.BookingServiceDTO;
import asia.cmg.f8.report.dto.CreditBookingSessionStatus;
import asia.cmg.f8.report.entity.database.BookingServiceType;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;
import asia.cmg.f8.report.entity.database.CreditSessionBookingEntity;

public interface CreditSessionBookingRepository extends JpaRepository<CreditSessionBookingEntity, Long> {

	String PAGEABLE_QUERY = " \n#pageable\n ";
	String QUERY_GET_SESSIONS_HISTORY = "SELECT * " + "FROM credit_session_bookings "
			+ "WHERE user_uuid = ?1 AND pt_uuid = ?2 AND status IN ?3 " + "ORDER BY id DESC " + PAGEABLE_QUERY;
	String COUNT_QUERY_GET_SESSIONS_HISTORY = "SELECT COUNT(*) " + "FROM credit_session_bookings "
			+ "WHERE user_uuid = ?1 AND pt_uuid = ?2 AND status IN ?3";

	String QUERY_GET_RECENT_TRAINERS = "SELECT sb.pt_uuid, su.username, su.full_name, su.avatar, su.`level`, lv.pt_booking_credit, MAX(sb.start_time) "
			+ "FROM credit_session_bookings sb JOIN session_users su ON sb.pt_uuid = su.uuid AND su.doc_status = 'approved' "
			+ "								 JOIN `level` lv ON su.`level` = lv.code "
			+ "WHERE sb.user_uuid = :euUuid AND sb.status IN :status " + "GROUP BY sb.pt_uuid "
			+ "ORDER BY sb.start_time DESC" + PAGEABLE_QUERY;
	String COUNT_QUERY_GET_RECENT_TRAINERS = "SELECT COUNT(DISTINCT sb.pt_uuid) "
			+ "FROM credit_session_bookings sb JOIN session_users su ON sb.pt_uuid = su.uuid AND su.doc_status = 'approved' "
			+ "WHERE sb.user_uuid = :euUuid AND sb.status IN :status " + "GROUP BY sb.pt_uuid";

	String QUERY_GET_RECENT_CLIENTS = "SELECT sb.user_uuid, su.username, su.full_name, su.avatar, MAX(sb.start_time) "
			+ "FROM credit_session_bookings sb JOIN session_users su ON sb.user_uuid = su.uuid "
			+ "WHERE sb.pt_uuid = :ptUuid AND sb.status IN :status " + "GROUP BY sb.user_uuid "
			+ "ORDER BY sb.start_time DESC" + PAGEABLE_QUERY;
	String COUNT_QUERY_GET_RECENT_CLIENTS = "SELECT COUNT(*) " + "FROM credit_session_bookings sb "
			+ "WHERE sb.pt_uuid = :ptUuid AND sb.status IN :status " + "GROUP BY sb.user_uuid";

	List<CreditSessionBookingEntity> findByStartTimeBetweenAndUserUuidOrderByStartTime(LocalDateTime startTime,
			LocalDateTime endTime, String UserUuid);

	List<CreditSessionBookingEntity> findByStartTimeBetweenAndPtUuidOrderByStartTime(LocalDateTime startTime,
			LocalDateTime endTime, String ptUuid);

	List<CreditSessionBookingEntity> findByUserUuidAndStatusNotIn(String userUuid,
			List<CreditBookingSessionStatus> status);

	@Query(value = "SELECT t " + "FROM CreditBookingEntity t " + "WHERE (clientUuid = ?1) "
			+ "		AND ((startTime <= ?2 AND endTime > ?2) OR (startTime < ?3 AND endTime > ?3)) "
			+ "		AND (status IN ?4) AND (bookingType IN ?5)")
	List<CreditBookingEntity> findOverlappedBookedByClientAndTime(String clientUuid, LocalDateTime startTime,
			LocalDateTime endTime, List<CreditBookingSessionStatus> statuses, List<BookingServiceType> bookingTypes);

	@Query(value = QUERY_GET_RECENT_TRAINERS, countQuery = COUNT_QUERY_GET_RECENT_TRAINERS, nativeQuery = true)
	Page<Object[]> getRecentBookedTrainersByUserUuid(@Param("euUuid") String euUuid,
			@Param("status") List<Integer> status, Pageable pageable);

	@Query(value = QUERY_GET_RECENT_CLIENTS, countQuery = COUNT_QUERY_GET_RECENT_CLIENTS, nativeQuery = true)
	Page<Object[]> getRecentBookedClientsByTrainerUuid(@Param("ptUuid") String ptUuid,
			@Param("status") List<Integer> status, Pageable pageable);

	Optional<CreditSessionBookingEntity> findFirstByUuidAndPtUuid(final String uuid, final String ptUuid);

	Optional<CreditSessionBookingEntity> findFirstByUserUuidAndPtUuidAndCreditBookingId(final String userUuid,
			final String ptUuid, final long creditBookingId);

	@Query(value = "SELECT * FROM credit_session_bookings WHERE status = ?1 AND end_time <= NOW()", nativeQuery = true)
	List<CreditSessionBookingEntity> getConfirmedSessionsOverEndTime(final int status);

	@Query(value = "SELECT * FROM credit_session_bookings WHERE status = ?1 AND end_time < NOW()", nativeQuery = true)
	List<CreditSessionBookingEntity> getBurnedSessionsToDeduct(final int status);

	@Query(value = QUERY_GET_SESSIONS_HISTORY, countQuery = COUNT_QUERY_GET_SESSIONS_HISTORY, nativeQuery = true)
	Page<CreditSessionBookingEntity> getSessionsHistory(String clientUuid, String trainerUuid, List<Integer> statusList,
			Pageable pageable);

	List<CreditSessionBookingEntity> findByStartTimeBetweenAndPtUuidAndStatusInOrderByStartTime(LocalDateTime startTime,
			LocalDateTime endTime, String ptUuid, List<CreditBookingSessionStatus> completedCode);

	@Query(value = "SELECT count(csb.uuid), sum(csb.credit_amount) " + "FROM credit_session_bookings csb "
			+ "WHERE csb.status IN ?3 " + "AND csb.created_date BETWEEN ?1 AND ?2", nativeQuery = true)
	List<Object[]> countCancelledSessionsBookingByRange(LocalDateTime start, LocalDateTime end,
			List<Integer> cancelledCode);

	@Query(value = "SELECT csb.status, COUNT(csb.uuid) AS counting FROM credit_session_bookings csb "
			+ "WHERE csb.status IN ?3 " + "AND csb.created_date BETWEEN ?1 AND ?2 "
			+ "GROUP BY csb.status", nativeQuery = true)
	List<Object[]> countStatusBookingByRange(LocalDateTime start, LocalDateTime end, List<Integer> statusCode);

	@Query(value = "SELECT COUNT(DISTINCT (csb.booked_by)) " + "FROM credit_session_bookings csb "
			+ "WHERE csb.created_date BETWEEN ?1 AND ?2 " + "AND csb.pt_uuid = ?3", nativeQuery = true)
	Integer getStatsNewClients(LocalDateTime start, LocalDateTime end, String ptUuid);

	@Query(value = "SELECT COUNT(DISTINCT (csb.uuid)), sum(csb.credit_amount)" + "FROM credit_session_bookings csb "
			+ "WHERE csb.status IN ?4 " + "AND csb.pt_uuid = ?3 "
			+ "AND csb.created_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
	List<Object[]> getStatsTotalCompletedSession(LocalDateTime start, LocalDateTime end, String ptUuid,
			List<Integer> completedCode);

	@Query(value = "SELECT sum(csb.credit_amount) " + "FROM credit_session_bookings csb " + "WHERE csb.status IN ?4 "
			+ "AND csb.pt_uuid = ?3 " + "AND csb.created_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
	BigInteger getStatsTotalEarning(LocalDateTime start, LocalDateTime end, String ptUuid, List<Integer> completedCode);

	@Query(value = "SELECT count(cb.id), sum(cb.credit_amount) " + "FROM credit_bookings cb " + "WHERE cb.status IN ?3 "
			+ "AND cb.created_date BETWEEN ?1 AND ?2 " + "AND cb.booking_type = ?4", nativeQuery = true)
	List<Object[]> countCancelledSessionsBookingByRange(LocalDateTime start, LocalDateTime end,
			List<Integer> cancelledCode, Integer serviceType);

	@Query(value = "SELECT su.avatar, su.full_name, su.username, su.phone, su.email, csb.user_uuid "
			+ "FROM credit_session_bookings csb INNER JOIN session_users su ON csb.user_uuid = su.uuid "
			+ "WHERE csb.pt_uuid = ?1 GROUP BY csb.user_uuid \n#pageable\n", countQuery = "SELECT COUNT(DISTINCT su.full_name) "
					+ "FROM credit_session_bookings csb INNER JOIN session_users su ON csb.user_uuid = su.uuid "
					+ "WHERE csb.pt_uuid = ?1", nativeQuery = true)
	Page<Object[]> getPTClients(final String uuid, final Pageable pageable);

	@Query(value = "SELECT su.avatar, su.full_name, su.username, su.phone, su.email, csb.user_uuid "
			+ "FROM credit_session_bookings csb INNER JOIN session_users su ON csb.user_uuid = su.uuid "
			+ "WHERE csb.pt_uuid = :uuid AND (su.full_name LIKE %:filter% OR su.email LIKE %:filter% OR su.phone LIKE %:filter%) "
			+ "GROUP BY csb.user_uuid \n#pageable\n", countQuery = "SELECT COUNT(DISTINCT su.full_name) "
					+ "FROM credit_session_bookings csb INNER JOIN session_users su ON csb.user_uuid = su.uuid "
					+ "WHERE csb.pt_uuid = :uuid AND (su.full_name LIKE %:filter% OR su.email LIKE %:filter% OR su.phone LIKE %:filter%)", nativeQuery = true)
	Page<Object[]> getPTClientsWithFilter(@Param("uuid") final String uuid, @Param("filter") String filter,
			final Pageable pageable);

	@Query(value = "SELECT COUNT(*), SUM(csb.credit_amount) FROM credit_session_bookings csb where csb.pt_uuid = ?1 and csb.user_uuid = ?2 and csb.status = 7", nativeQuery = true)
	List<Object[]> getTotalSessionCompletedAndTotalLeepCoin(final String ptUuid, final String userUuid);

	@Query(value = "SELECT * FROM credit_session_bookings csb WHERE csb.credit_booking_id = ?1", nativeQuery = true)
	CreditSessionBookingEntity findByCreditBookingId(Long id);
	
	@Query(value = "SELECT NEW "
			+ " asia.cmg.f8.report.dto.BookingServiceDTO("
			+ " entity, pt, eu) "
			+ " FROM CreditSessionBookingEntity entity "
			+ " LEFT JOIN BasicUserEntity pt ON entity.ptUuid = pt.uuid "
			+ " LEFT JOIN BasicUserEntity eu ON entity.userUuid = eu.uuid "
			+ " WHERE "
			+ " ( "
			+ "	  pt.fullName LIKE %:keyword% OR  eu.fullName LIKE %:keyword% "
			+ "   OR pt.userName LIKE %:keyword% OR eu.userName LIKE %:keyword% "
			+ " ) "
			+ " AND entity.status IN :status "
			+ " AND entity.createdDate BETWEEN :start AND :end ")
	List<BookingServiceDTO> sessionBookingManagement( 
			@Param("keyword") String keyword, 
			@Param("start") LocalDateTime start, @Param("end")  LocalDateTime end, 
			@Param("status") List<CreditBookingSessionStatus> status,
			Pageable pageable);
}
