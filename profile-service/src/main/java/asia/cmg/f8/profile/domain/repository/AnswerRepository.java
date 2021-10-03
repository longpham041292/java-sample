package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.profile.database.entity.AnswerEntity;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

	List<AnswerEntity> findByOwnerUuidAndQuestionKey(final String ownerUuid, final String questionKey);
}
