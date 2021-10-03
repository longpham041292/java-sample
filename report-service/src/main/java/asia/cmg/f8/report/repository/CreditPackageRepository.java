package asia.cmg.f8.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.report.dto.CreditPackageDTO;
import asia.cmg.f8.report.entity.database.CreditPackageEntity;
import asia.cmg.f8.report.entity.database.CreditPackageType;

@Repository
public interface CreditPackageRepository extends JpaRepository<CreditPackageEntity, Long> {

	@Query(value = "SELECT * FROM credit_packages WHERE active = 1", nativeQuery = true)
	List<CreditPackageEntity> getActiveCreditPackages();
	
	@Query(value = "SELECT * FROM credit_packages WHERE type = ?1", nativeQuery = true)
	Optional<CreditPackageEntity> getUnitPackage(int packageType);
	
	@Query(value = "SELECT new asia.cmg.f8.report.dto.CreditPackageDTO(c) FROM CreditPackageEntity c WHERE c.creditType = ?1")
	CreditPackageDTO getCreditPackageByType(CreditPackageType type);
}
