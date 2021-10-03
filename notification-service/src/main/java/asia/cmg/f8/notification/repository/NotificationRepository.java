package asia.cmg.f8.notification.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.notification.database.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

	String queryGetLatestByReceiver = "SELECT * "
									+ "FROM notifications "
									+ "WHERE receiver = :receiverUuid AND display = 1 "
									+ "ORDER BY id DESC \n#pageable\n";
	String queryCountLatestByReceiver = "SELECT * "
									+ "FROM notifications "
									+ "WHERE receiver = :receiverUuid AND display = 1 ";

	@Query(value = "UPDATE notification SET display = 0 WHERE receiver = :receiverUuid", nativeQuery = true)
	void clearAllNotificationByReceiver(@Param(value = "receiverUuid") final String receiverUuid);

	@Query(value = queryGetLatestByReceiver, countQuery = queryCountLatestByReceiver, nativeQuery = true)
	Page<NotificationEntity> getLatestByReceiver(@Param(value = "receiverUuid") final String receiverUuid, final Pageable pageable);
	
	@Query(value = "SELECT COUNT(id) FROM notifications noti WHERE receiver = :user_uuid and id > :offset",
			nativeQuery = true)
	int countByReceiverAndOffset(@Param(value = "user_uuid") String userUuid, @Param("offset") long offset);
	
	@Query(value = "SELECT MAX(id) FROM notifications WHERE receiver = :user_uuid", nativeQuery = true)
	Optional<BigInteger> findMaxIdByReceiver(@Param(value = "user_uuid") String userUuid);
	
	NotificationEntity findByUuid(String uuid);
	
	@Modifying
    @Transactional
	@Query(value = "DELETE FROM notifications WHERE receiver = :receiver_uuid", nativeQuery = true)
	void deleteByReceiver(@Param(value = "receiver_uuid") String receiverUuid);
}
