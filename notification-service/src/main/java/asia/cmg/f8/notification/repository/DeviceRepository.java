package asia.cmg.f8.notification.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.notification.database.entity.DeviceEntity;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

	String queryGetDevicesByGroupId = "SELECT * FROM device WHERE device_group_id = :group_id AND activated = 1 \n#pageable\n";
	
	String queryCountGetDevicesByGroupId = "SELECT COUNT(*) FROM device WHERE device_group_id = :group_id AND activated = 1";
	
	List<DeviceEntity> getByUserUuid(final String userUuid);
	
	DeviceEntity getByDeviceIdAndUserUuid(final String deviceId, final String userUuid);
	
	@Query(value = queryGetDevicesByGroupId, countQuery = queryCountGetDevicesByGroupId, nativeQuery = true)
	Page<DeviceEntity> getDevicesByGroupId(@Param("group_id") final int deviceGroupId, final Pageable pageable);
	
	@Modifying
	@Query(value = "UPDATE device de set de.activated = 0 where de.user_uuid = :uuid  and de.id != :id", nativeQuery = true)
	void updateStatusOldDeviceToDeactiveExceptOne(@Param("uuid") String uuid, @Param("id") long id);

	List<DeviceEntity> getByActivatedTrueAndUserUuid(final String userUuid);
}
