package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.domain.entity.LocalizationEntity;

@Repository
public interface LocalizationRepository extends JpaRepository<LocalizationEntity, Long> {

	@Query(value = "SELECT * FROM localization_data WHERE category = ?1 AND language = ?2", nativeQuery = true)
	List<LocalizationEntity> getByCategoryAndLanguage(final String category, final String language);
	
	@Query(value = "SELECT * FROM localization_data WHERE category in ?1 AND language = ?2", nativeQuery = true)
	List<LocalizationEntity> getByCategoriesAndLanguage(final List<String> categories, final String language);
	
	@Query(value = "SELECT * FROM localization_data WHERE language = ?1", nativeQuery = true)
	List<LocalizationEntity> getByLanguage(final String language);
	
	@Query(value = "SELECT * FROM localization_data WHERE category = ?1 AND language = ?2 AND value like CONCAT('%', CONVERT(?3, CHAR CHARACTER SET utf8), '%')", nativeQuery = true)
	List<LocalizationEntity> searchByCategoryAndLanguage(final String category, final String language, final String keyword);
}
