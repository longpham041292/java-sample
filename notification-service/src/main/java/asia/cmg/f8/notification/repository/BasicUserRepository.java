package asia.cmg.f8.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.notification.database.entity.BasicUserEntity;

@Repository
public interface BasicUserRepository extends JpaRepository<BasicUserEntity, Long> {

	BasicUserEntity findByUuid(final String uuid);
}
