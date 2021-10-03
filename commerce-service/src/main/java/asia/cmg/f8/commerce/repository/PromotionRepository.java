/**
 * 
 */
package asia.cmg.f8.commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.PromotionEntity;

/**
 * @author khoa.bui
 *
 */
@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    Optional<PromotionEntity> findOneByCouponCode(final String couponCode);
    
    @Query("select p from PromotionEntity p where p.couponCode = ?1 "
            + "and p.startedDate <= ?2 and p.endDate >= ?2 ")
    Optional<PromotionEntity> findProductByCouponCodeAndDate(final String couponCode, final Long usgeDate);
    
    @Query(value = "SELECT su.username " +
            "FROM session_users su " +
            "WHERE su.uuid = :ptUuid", nativeQuery = true)
    String getPTUserName(@Param("ptUuid") String ptUuid);
    
    Optional<PromotionEntity> findByCouponCode(final String couponCode);
}
