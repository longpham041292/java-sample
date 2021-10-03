package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.SubscriptionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionTypeEntity, Long> {
	
    @Query(value = "select * from subscription_types order by level_code, option_id", nativeQuery = true)
    List<SubscriptionTypeEntity> findAll();

    @Query("select st from SubscriptionTypeEntity st where st.uuid = ?1")
    Optional<SubscriptionTypeEntity> findSubscriptionTypeByUuid(final String subscriptionTypeUuid);
    
//    @Query(value = "SELECT st.* FROM subscription_types as st LEFT JOIN level as l on st.level_code = l.code WHERE l.code = ?1 AND option_id = ?2", nativeQuery = true)
//    Optional<SubscriptionTypeEntity> findSubscriptionTypeByLevelAndOption(final String levelCode, final Integer option);
    
    @Query(value = "SELECT * FROM subscription_types WHERE level_code = ?1 AND option_id = ?2", nativeQuery = true)
    Optional<SubscriptionTypeEntity> findSubscriptionTypeByLevelAndOption(final String levelCode, final Integer option);
}
