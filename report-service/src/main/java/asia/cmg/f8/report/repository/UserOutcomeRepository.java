package asia.cmg.f8.report.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.report.entity.database.UserOutcomeEntity;

public interface UserOutcomeRepository extends JpaRepository<UserOutcomeEntity, Long> {

	@Query(value = "SELECT su.full_name, su.username, su.email, su.phone, SUM(uo.total_package), SUM(uo.credit_amount), MAX(uo.to_date) FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 GROUP BY uo.owner_uuid \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT su.full_name, su.username, su.email, su.phone, SUM(uo.total_package), SUM(uo.credit_amount), MAX(uo.to_date) FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 GROUP BY uo.owner_uuid) c", nativeQuery = true)
	Page<Object[]> getTrainerProfitReport(final LocalDateTime startTime, final LocalDateTime endTime,
			final Pageable pageable);

	@Query(value = "SELECT su.full_name, su.username, su.email, su.phone, SUM(uo.total_package), SUM(uo.credit_amount), MAX(uo.to_date) FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 AND su.full_name LIKE %?3% GROUP BY uo.owner_uuid \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT su.full_name, su.username, su.email, su.phone, SUM(uo.total_package), SUM(uo.credit_amount), MAX(uo.to_date) FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 AND su.full_name LIKE %?3% GROUP BY uo.owner_uuid) c", nativeQuery = true)
	Page<Object[]> getTrainerProfitReportWithFilter(final LocalDateTime startTime, final LocalDateTime endTime,
			String name, final Pageable pageable);

	@Query(value = "SELECT su.full_name, su.username, su.email, su.phone, uo.credit_amount, uo.to_date, uo.owner_uuid, uo.id, uo.payment_status, uo.payment_date FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 ORDER BY su.full_name, uo.to_date DESC \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT su.full_name, su.username, su.email, su.phone, uo.credit_amount, uo.to_date, uo.owner_uuid, uo.id, uo.payment_status, uo.payment_date FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2) c", nativeQuery = true)
	Page<Object[]> getDeductCoinTrainerReport(final LocalDateTime startTime, final LocalDateTime endTime,
			final Pageable pageable);
	
	@Query(value = "SELECT su.full_name, su.username, su.email, su.phone, uo.credit_amount, uo.to_date, uo.owner_uuid, uo.id, uo.payment_status, uo.payment_date FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 AND su.full_name LIKE %?3% ORDER BY su.full_name, uo.to_date DESC \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT su.full_name, su.username, su.email, su.phone, uo.credit_amount, uo.to_date, uo.owner_uuid, uo.id, uo.payment_status, uo.payment_date FROM user_outcome uo JOIN session_users su ON uo.owner_uuid = su.uuid WHERE uo.to_date BETWEEN ?1 AND ?2 AND su.full_name LIKE %?3%) c", nativeQuery = true)
	Page<Object[]> getDeductCoinTrainerReportWithFilter(final LocalDateTime startTime, final LocalDateTime endTime, String name,
			final Pageable pageable);
}