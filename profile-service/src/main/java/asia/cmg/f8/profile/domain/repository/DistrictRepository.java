package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.dto.DistrictDistanceDTO;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, Integer> {

	List<DistrictEntity> findByKeyAndLanguage(final String key, final String language);
	
	List<DistrictEntity> findByCityKeyAndLanguageOrderBySequenceAsc(final String cityKey, final String language);
	
	List<DistrictEntity> findByLanguage(final String language);
	
	List<DistrictDistanceDTO> getTopNearestDistricts(final Double latitude, final Double longitude, final int limit);
}
