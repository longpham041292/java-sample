package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.database.entity.TrendingUserDistrictEntity;

@Repository
public interface TrendingUserDistrictRepository extends JpaRepository<TrendingUserDistrictEntity, Long> {
	
	List<TrendingUserDistrictEntity> findByCityKeyAndDistrictKey(final String cityKey, final String districtKey);
	
	@Query(value = "SELECT * FROM trending_user_district WHERE district_key = ?1 ORDER BY `order` ASC LIMIT ?2", nativeQuery = true)
	List<TrendingUserDistrictEntity> searchTrainersByDistrictKey(final String districtKey, final int limit);
}
