package asia.cmg.f8.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.report.entity.database.UserCreditPackageEntity;

@Repository
public interface UserCreditPackageRepository extends JpaRepository<UserCreditPackageEntity, Long> {

	
	final String GET_AVAILABLE_CREDIT_PACKAGES_QUERY
	= " SELECT *" 
	+ " FROM user_credit_packages ucp" 
	+ " WHERE owner_uuid = :owner_uuid AND ucp.expired_date > NOW() AND ucp.used_credit < ucp.total_credit"
	+ " ORDER BY ucp.expired_date";
	
	final String GET_EXPIRED_CREDIT_PACKAGES_QUERY = 
			"SELECT * "
			+ "FROM user_credit_packages ucp "
			+ "WHERE ucp.expired_date <= NOW() AND ucp.used_credit < ucp.total_credit"; 
	
	/**
	 * Get available UserCreditPackages which are:
	 * - Not expire
	 * - Has used credit less than total credit
	 * @param date
	 * @return
	 */
	@Query(value = GET_AVAILABLE_CREDIT_PACKAGES_QUERY, nativeQuery = true)
	List<UserCreditPackageEntity> getAvailableCreditPackages(@Param("owner_uuid") String ownerUuid);
	
	@Query(value = GET_EXPIRED_CREDIT_PACKAGES_QUERY, nativeQuery = true)
	List<UserCreditPackageEntity> getExpiredCreditPackages();
	
	@Query(value = 	"SELECT * " + 
					"FROM user_credit_packages " + 
					"WHERE owner_uuid = ?1 AND expired_date > NOW() AND used_credit < total_credit " + 
					"ORDER BY expired_date " + 
					"LIMIT 1", nativeQuery = true)
	Optional<UserCreditPackageEntity> getNextExpiredPackageByOwner(final String ownerUuid);
	
	@Query(value = "SELECT * FROM user_credit_packages ucp WHERE ucp.expired_date > NOW() AND ucp.used_credit < ucp.total_credit", nativeQuery = true)
	List<UserCreditPackageEntity> getUnexpiredCreditPackage();
}
