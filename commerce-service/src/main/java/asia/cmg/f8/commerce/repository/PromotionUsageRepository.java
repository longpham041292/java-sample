/**
 * 
 */
package asia.cmg.f8.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.commerce.entity.PromotionUsageEntity;

/**
 * @author khoa.bui
 *
 */
public interface PromotionUsageRepository extends JpaRepository<PromotionUsageEntity, Long> {

	Long countByCouponCode(final String couponCode);
	Long countByUserUuid(final String userUuid);
	
	@Query("SELECT COUNT(pu.id) FROM PromotionUsageEntity pu WHERE pu.userUuid=:userUuid and pu.couponCode=:couponCode")
    Long countByUserUuidAndCouponCode(@Param("userUuid") final String userUuid, @Param("couponCode") final String couponCode);
}
