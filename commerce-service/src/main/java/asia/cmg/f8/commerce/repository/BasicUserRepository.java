package asia.cmg.f8.commerce.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.BasicUserEntity;

/**
 * Created on 11/25/16.
 */
@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface BasicUserRepository extends CrudRepository<BasicUserEntity, String> {
	
	Optional<BasicUserEntity> findOneByUserCode(final String userCode);
	
	Optional<BasicUserEntity> findOneByUuid(final String uuid);
}
