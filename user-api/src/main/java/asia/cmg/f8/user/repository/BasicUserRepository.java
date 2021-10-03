package asia.cmg.f8.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.user.entity.BasicUserEntity;

public interface BasicUserRepository extends JpaRepository<BasicUserEntity, Long> {

	Optional<BasicUserEntity> findByEmail(final String email);
	Optional<BasicUserEntity> findByUuid(String uuid);
	

}
