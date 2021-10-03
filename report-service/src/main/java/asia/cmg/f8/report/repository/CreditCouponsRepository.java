package asia.cmg.f8.report.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.report.dto.CreditCouponsDTO;
import asia.cmg.f8.report.entity.database.CreditCouponEntity;
import asia.cmg.f8.report.entity.database.CreditCouponStatus;

@Repository
public interface CreditCouponsRepository extends JpaRepository<CreditCouponEntity, Integer> {

	Optional<CreditCouponEntity> findByCode(String code);

	CreditCouponEntity findFirstByOrderByIdDesc();

	@Query(value = "SELECT new asia.cmg.f8.report.dto.CreditCouponsDTO(c) FROM CreditCouponEntity c WHERE c.createdDate BETWEEN ?1 AND ?2 AND c.status in ?3 ORDER BY c.createdDate DESC")
	Page<CreditCouponsDTO> getCreditCoupons(LocalDateTime from, LocalDateTime to, List<CreditCouponStatus> status,
			Pageable pageable);

	@Query(value = "SELECT new asia.cmg.f8.report.dto.CreditCouponsDTO(c) FROM CreditCouponEntity c WHERE c.createdDate BETWEEN ?1 AND ?2 AND c.name = ?3 AND c.status in ?4 ORDER BY c.createdDate DESC")
	Page<CreditCouponsDTO> getCreditCouponsByName(LocalDateTime from, LocalDateTime to, String name,
			List<CreditCouponStatus> status, Pageable pageable);

}
