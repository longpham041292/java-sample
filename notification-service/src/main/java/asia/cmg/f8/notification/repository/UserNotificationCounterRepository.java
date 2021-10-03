package asia.cmg.f8.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.notification.entity.UserNotificationCounterEntity;

@Repository
public interface UserNotificationCounterRepository extends JpaRepository<UserNotificationCounterEntity, Long>{
	public UserNotificationCounterEntity findOneByUserUuid(String uuid);
	
	@Query("update UserNotificationCounterEntity counter set counter.offset = :offset WHERE counter.userUuid = :userUuid")
	public void updateOffsetForUser(@Param("userUuid") String userUuid, @Param("offset") long offset);
}
