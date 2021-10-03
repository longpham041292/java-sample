package asia.cmg.f8.session.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.session.entity.UserSkillsEntity;

@Repository
public interface UserSkillsRepository extends JpaRepository<UserSkillsEntity, Long> {

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM user_skills WHERE user_id = ?1", nativeQuery = true)
	int deleteByUserId(final Long userId);
}
