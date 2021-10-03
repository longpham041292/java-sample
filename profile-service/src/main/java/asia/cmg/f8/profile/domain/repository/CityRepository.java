package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.database.entity.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Integer> {

	List<CityEntity> findByLanguageOrderBySequenceAsc(final String language);
}
