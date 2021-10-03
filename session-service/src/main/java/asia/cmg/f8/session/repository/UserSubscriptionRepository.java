package asia.cmg.f8.session.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.session.dto.UserSubscriptionDto;
import asia.cmg.f8.session.entity.UserSubscriptionEntity;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionEntity, Long> {
	
	  @Query(value = "SELECT new asia.cmg.f8.session.dto.UserSubscriptionDto(u, s.startTime, s.endTime, s.numberOfMonth, s.limitDay) " 
          	+ "FROM UserSubscriptionEntity s JOIN BasicUserEntity u ON s.ptUuid = u.uuid "
          	+ "WHERE (s.euUuid = :eu_uuid) AND "
          	+ "( (:active = true AND s.endTime >= NOW()) OR (:active = false AND s.endTime < NOW())) "
          	+ "ORDER by s.endTime DESC ",
          	countQuery = "SELECT COUNT(u.uuid) "
  		 	+ "FROM UserSubscriptionEntity s JOIN BasicUserEntity u ON s.ptUuid = u.uuid " 
           	+ "WHERE (s.euUuid = :eu_uuid) AND " 
           	+ "( (:active = true AND s.endTime >= NOW()) OR (:active = false AND s.endTime < NOW())) ")
		Page<UserSubscriptionDto> getUserSubscriptionsByEu(@Param("eu_uuid") String euUuid, @Param("active") Boolean active, final Pageable pageable);
		
	  @Query(value = "SELECT new asia.cmg.f8.session.dto.UserSubscriptionDto(u, s.startTime, s.endTime, s.numberOfMonth, s.limitDay) " 
	          	+ "FROM UserSubscriptionEntity s JOIN BasicUserEntity u ON s.euUuid = u.uuid "
	          	+ "WHERE (s.ptUuid = :pt_uuid) AND "
	          	+ "( (:active = true AND s.endTime >= NOW()) OR (:active = false AND s.endTime < NOW())) "
	          	+ "ORDER by s.endTime DESC ",
	          	countQuery = "SELECT COUNT(u.uuid) "
	  		 	+ "FROM UserSubscriptionEntity s JOIN BasicUserEntity u ON s.euUuid = u.uuid " 
	           	+ "WHERE (s.ptUuid = :pt_uuid) AND " 
	           	+ "( (:active = true AND s.endTime >= NOW()) OR (:active = false AND s.endTime < NOW())) ")
		Page<UserSubscriptionDto> getUserSubscriptionsByPt(@Param("pt_uuid") String ptUuid, @Param("active") Boolean active, final Pageable pageable);
}
