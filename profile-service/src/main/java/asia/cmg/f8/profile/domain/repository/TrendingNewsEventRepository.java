package asia.cmg.f8.profile.domain.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;

@Repository
public interface TrendingNewsEventRepository extends JpaRepository<TrendingEventEntity, Integer> {
	
	Optional<TrendingEventEntity> findById(long id);
	
	@Query(value = "SELECT * FROM trending_event WHERE section_id = ?1 AND language = ?2 AND activated = 1 ORDER BY `order` ASC LIMIT ?3", nativeQuery = true)
	List<TrendingEventEntity> findTrendingBySectionId(int sectionId, String language, int limit);
	
	@Query(value = "SELECT * FROM trending_event WHERE section_id = ?1 AND language = ?2 AND activated = 1 ORDER BY `order` ASC \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM trending_event WHERE section_id = ?1 AND language = ?2 AND activated = 1", nativeQuery = true)
	Page<TrendingEventEntity> findTrendingBySectionId(int sectionId, String language, Pageable pageable);
	
	@Query(value = "SELECT * FROM trending_event WHERE created_date between ?1 AND ?2 AND activated = ?3 AND title like %?4% ORDER BY id DESC \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM trending_event WHERE created_date between ?1 AND ?2 AND activated = ?3 AND title like %?4%", nativeQuery = true)
	Page<TrendingEventEntity> searchTrendingByKeyword(LocalDateTime startDate, LocalDateTime endDate, boolean activated, String keyword, Pageable pageable);
	
	@Query(value = "SELECT * FROM trending_event WHERE created_date between ?1 AND ?2 AND activated = ?3 ORDER BY id DESC \n#pageable\n", 
			countQuery = "SELECT COUNT(*) FROM trending_event WHERE created_date between ?1 AND ?2 AND activated = ?3", nativeQuery = true)
	Page<TrendingEventEntity> searchTrending(LocalDateTime startDate, LocalDateTime endDate, boolean activated, Pageable pageable);
}
