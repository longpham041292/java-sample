package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
	
	String QUERY_FINDBYLEVELANDOPTION = "SELECT s.* " + 
										"FROM subscriptions s LEFT JOIN subscription_types st ON s.subscription_type_id = st.id " + 
										"					  LEFT JOIN order_options op ON st.option_id = op.id " + 
										"WHERE s.status = 'ACTIVE' " + 
										"	AND s.country = ?1 " + 
										"	AND st.level_code = ?2 " + 
										"	AND op.id = ?3 " +
										"ORDER BY s.number_of_month";
	
    @Query(value =  "SELECT s.* " + 
    				"FROM subscriptions s LEFT JOIN subscription_types st ON s.subscription_type_id = st.id " + 
					"ORDER BY st.level_code, st.option_id, s.number_of_month", nativeQuery = true)
    List<SubscriptionEntity> findAll();

    @Query("select s from SubscriptionEntity s where s.uuid = ?1")
    Optional<SubscriptionEntity> findSubscriptionByUuid(String subscriptionUuid);

    @Query("select s from SubscriptionEntity s join SubscriptionTypeEntity st " +
            "on s.subscriptionTypeId = st.id where st.level.code = ?1 and s.country = ?2 "
            + "and s.status = 'ACTIVE'")
    List<SubscriptionEntity> findByLevel(String level, String country);
    
    @Query(value = QUERY_FINDBYLEVELANDOPTION, nativeQuery = true)
    List<SubscriptionEntity> findByLevelAndOption(String country, String levelCode, Integer optionId);
}
