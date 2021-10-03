package asia.cmg.f8.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.database.entity.BasicUserEntity;

@Repository
public interface BasicUserEntityRepository extends JpaRepository<BasicUserEntity, Long> {
	
	@Query(name = "SELECT * FROM session_users WHERE uuid = ?1 LIMIT 1", nativeQuery = true)
	BasicUserEntity findByUuid(final String uuid);
	
	@Query(value = "SELECT * FROM session_users "
				+ "WHERE user_type = 'pt' AND extend_user_type IN ('pt_ambassador','pt_leader') AND uuid NOT IN ?1 "
				+ "LIMIT ?2", nativeQuery = true)
	List<BasicUserEntity> searchAmbassadorOrLeaderTrainers(final List<String> uuids, final int limit);
	
	@Query(value = "SELECT * FROM session_users "
				+ "WHERE user_type = 'pt' AND extend_user_type IN ('pt_ambassador','pt_leader') "
				+ "LIMIT ?1", nativeQuery = true)
	List<BasicUserEntity> searchAmbassadorOrLeaderTrainers(final int limit);
	
	@Query(value = "SELECT * FROM session_users WHERE uuid IN ?1 AND user_type = 'pt' AND doc_status = 'APPROVED' LIMIT ?2", nativeQuery = true)
	List<BasicUserEntity> getApprovedTrainersByUuidList(List<String> uuids, int limit);
} 
