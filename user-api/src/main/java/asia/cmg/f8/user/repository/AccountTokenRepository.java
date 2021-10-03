package asia.cmg.f8.user.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.user.entity.AccountTokenEntity;

/**
 * Created on 08/02/20.
 */
@Repository
public interface AccountTokenRepository extends JpaRepository<AccountTokenEntity, Long> {
	
	@Query(value = "SELECT a FROM AccountTokenEntity a WHERE uuid = ?1")
	AccountTokenEntity findByUUID(final String uuid);
	
	@Query(value = "SELECT a FROM AccountTokenEntity a WHERE accessToken = ?1")
	AccountTokenEntity findByAccessToken(final String accessToken);
	
	AccountTokenEntity findByAccessTokenAndRefreshToken(final String accessToken, final String refreshToken);
	
	Optional<AccountTokenEntity> findByUuid(final String uuid);

	AccountTokenEntity findByResetPasswordToken(String resetPasswordToken);
}
