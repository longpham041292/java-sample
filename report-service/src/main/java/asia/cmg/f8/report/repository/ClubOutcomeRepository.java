package asia.cmg.f8.report.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.report.entity.database.ClubOutcomeEntity;

public interface ClubOutcomeRepository extends JpaRepository<ClubOutcomeEntity, Long> {

	@Query(value = "SELECT c.studio_name, c.studio_address, SUM(co.credit_amount), SUM(co.before_balance), MAX(co.to_date) FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name, cb.studio_address FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2 GROUP BY co.owner_uuid \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT c.studio_name, c.studio_address, SUM(co.credit_amount), SUM(co.before_balance), MAX(co.to_date) FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name, cb.studio_address FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2 GROUP BY co.owner_uuid) d", nativeQuery = true)
	Page<Object[]> getClubProfitReport(final LocalDateTime startTime, final LocalDateTime endTime,
			final Pageable pageable);

	@Query(value = "SELECT c.studio_name, c.studio_address, SUM(co.credit_amount), SUM(co.before_balance), MAX(co.to_date) FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name, cb.studio_address FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE (co.to_date BETWEEN ?1 AND ?2) AND c.studio_name like %?3% GROUP BY co.owner_uuid \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT c.studio_name, c.studio_address, SUM(co.credit_amount), SUM(co.before_balance), MAX(co.to_date) FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name, cb.studio_address FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE (co.to_date BETWEEN ?1 AND ?2) AND c.studio_name like %?3% GROUP BY co.owner_uuid) d", nativeQuery = true)
	Page<Object[]> getClubProfitReportWithFilter(final LocalDateTime startTime, final LocalDateTime endTime,
			String name, final Pageable pageable);
	
	@Query(value = "SELECT co.id, c.studio_name, co.credit_amount, co.to_date, co.payment_status, co.payment_date FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2 ORDER BY c.studio_name, co.to_date \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT co.id, c.studio_name, co.credit_amount, co.to_date, co.payment_status, co.payment_date FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2) d", nativeQuery = true)
	Page<Object[]> getDeductCoinClubReport(final LocalDateTime startTime, final LocalDateTime endTime,
			final Pageable pageable);
	
	@Query(value = "SELECT co.id, c.studio_name, co.credit_amount, co.to_date, co.payment_status, co.payment_date FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2 AND c.studio_name like %?3% ORDER BY c.studio_name, co.to_date \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM (SELECT co.id, c.studio_name, co.credit_amount, co.to_date, co.payment_status, co.payment_date FROM club_outcome co JOIN (SELECT DISTINCT cb.studio_uuid, cb.studio_name FROM credit_bookings cb) c ON co.owner_uuid = c.studio_uuid WHERE co.to_date BETWEEN ?1 AND ?2 AND c.studio_name like %?3%) d", nativeQuery = true)
	Page<Object[]> getDeductCoinClubReportWithFilter(final LocalDateTime startTime, final LocalDateTime endTime,
			String name, final Pageable pageable);
}