package asia.cmg.f8.profile.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.profile.database.entity.SearchTrackingEntity;

public interface SearchTrackingRepository extends JpaRepository<SearchTrackingEntity, Long> {
	
	@Query(value = "SELECT * FROM search_tracking WHERE account_uuid = ?1 ORDER BY modified_date DESC LIMIT ?2", nativeQuery = true)
	List<SearchTrackingEntity> getTopLatestSearch(final String userUuid, final int limit);
	
	Optional<SearchTrackingEntity> findFirstByAccountUuidAndUserUuid(final String accountUuid, final String userUuid);
}
