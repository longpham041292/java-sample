package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.profile.database.entity.QuestionEntity;
import rx.Observable;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

	@Query(value = "SELECT * FROM question WHERE used_for = ?1 AND language = ?2 AND hide = false ORDER BY sequence ASC", nativeQuery = true)
	List<QuestionEntity> findByUserTypeAndLanguage(final String userType, final String language);
	
	@Query(value = "SELECT * FROM question WHERE used_for = ?1 AND language = ?2 AND hide = false AND sequence > ?3 ORDER BY sequence ASC", nativeQuery = true)
	List<QuestionEntity> findByUserTypeAndLanguageAndSequence(final String userType, final String language, final int lastSequence);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE question SET hide = ?1 WHERE `key` = ?2", nativeQuery = true)
	int updateQuestionStatusByKey(final boolean hide, final String key);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE question SET weight = ?1 WHERE `key` = ?2", nativeQuery = true)
	int updateQuestionWeightByKey(final int weight, final String key);
	
	@Query(value = "select * from question where language = ?2 and used_for = ?1 and question_type <> 'guide' order by `sequence` asc;", nativeQuery = true)
	List<QuestionEntity> findByLanguageAndUsedFor(final String userType, final String language);
	
	@Query(value = "select * from question where language = ?2 and usedFor = ?1 and hide = false order by `sequence` asc", nativeQuery = true)
	List<QuestionEntity> findByLanguageAndUsedForAndHideIsFalse(final String userType, final String language);
	
	@Query(value = "select * from question where `key` = ?1 and used_for = ?2 and hide = false", nativeQuery = true)
	List<QuestionEntity> findByKeyAndUsedForAndHideIsFalse(final String key, final String userType);
	
	@Query(value = "SELECT * FROM question WHERE required = 1 AND hide = 0 AND `language` = ?2 and used_for = ?1", nativeQuery = true)
	List<QuestionEntity> findRequiredQuestionsByLanguageAndUserType(final String userType, final String language);
}
