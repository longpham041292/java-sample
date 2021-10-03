package asia.cmg.f8.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.notification.database.entity.DeviceGroupEntity;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroupEntity, Integer> {
	
	@Query(value = "SELECT * FROM device_group WHERE activated = 1", nativeQuery = true)
	List<DeviceGroupEntity> getAllActivatedGroups();
	
	DeviceGroupEntity getByUuid(final String uuid);
}
