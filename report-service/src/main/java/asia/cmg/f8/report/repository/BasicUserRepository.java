package asia.cmg.f8.report.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.report.entity.database.BasicUserEntity;

/**
 * Created on 11/25/16.
 */
@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface BasicUserRepository extends CrudRepository<BasicUserEntity, String> {
	
	Optional<BasicUserEntity> findOneByUserCode(final String userCode);
	
	Optional<BasicUserEntity> findOneByUuid(final String uuid);
	
	@Query(value = "SELECT COUNT(DISTINCT (su.uuid)) from session_users su "
			+ "INNER JOIN credit_bookings cb "
			+ "ON su.uuid = cb.client_uuid "
			+ "WHERE su.user_type = ?1 "
			+ "AND cb.created_date BETWEEN ?2 AND ?3 ", nativeQuery = true)
	int countActiveUser(String userType, LocalDateTime start, LocalDateTime end);
	
	@Query(value = "SELECT COUNT(DISTINCT(uuid)) FROM session_users WHERE user_type = ?1", nativeQuery = true)
	int countTotalUser(@Param("userType") final String userType);
	
	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su INNER JOIN credit_session_bookings csb ON su.uuid = csb.pt_uuid "
			 + "WHERE su.user_type = 'pt' "
			 + "AND csb.status IN ?3 "
			 + "AND csb.created_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
	int countTotalPtHasBookingByRange(LocalDateTime start, LocalDateTime end, List<Integer> STATUS_QUERY);
	
	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su WHERE user_type = ?1 "
			+ "AND su.join_date BETWEEN ?2 AND ?3", nativeQuery = true)
	int countTotalUserByRangeTime(String userType, LocalDateTime start, LocalDateTime end);
	
	@Query(value = "SELECT COUNT(DISTINCT(su.uuid)) FROM session_users su "
			 + "WHERE user_type = 'pt' "
			 + "AND su.doc_approved_date BETWEEN ?1 AND ?2 "
			 + "AND su.doc_status = 'APPROVED'", nativeQuery = true)
	int countApprovedTrainerByRange(LocalDateTime start, LocalDateTime end);
}
