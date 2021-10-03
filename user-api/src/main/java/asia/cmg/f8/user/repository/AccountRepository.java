package asia.cmg.f8.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.user.entity.AccountEntity;

/**
 * Created on 08/02/20.
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
	
	@Query(value = "SELECT distinct a FROM AccountEntity a WHERE username = ?1 or phone = ?1 or email = ?1")
	AccountEntity findByUserName(final String username);
	
	@Query(value = "SELECT distinct a FROM AccountEntity a WHERE uuid = ?1")
	AccountEntity findByUuid(final String uuid);
	
}
