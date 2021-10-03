package asia.cmg.f8.commerce.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.OnepayUserEntity;

/**
 * Created on 11/25/16.
 */
@Repository
public interface OnepayUserRepository extends CrudRepository<OnepayUserEntity, String> {
	
	Optional<OnepayUserEntity> findOneByUserUuid(final String userUuid);
	
	Optional<OnepayUserEntity> findOneById(final Long Id);
}
