package asia.cmg.f8.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.database.entity.QuestionOptionEntity;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOptionEntity, Integer> {

	
}
