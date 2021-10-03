package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;

@Repository
public interface HomeSectionRepository extends JpaRepository<HomeSectionEntity, Integer> {

	@Query(value = "SELECT * FROM home_section ORDER BY `order` ASC", nativeQuery = true)
	List<HomeSectionEntity> findAllSections();
	
	@Query(value = "SELECT * FROM home_section WHERE activated = 1 ORDER BY `order` ASC", nativeQuery = true)
	List<HomeSectionEntity> findAllActivatedSections();
}
