package asia.cmg.f8.profile.domain.repository;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import asia.cmg.f8.profile.database.entity.UserDistrictLocationEntity;
import asia.cmg.f8.profile.dto.LocationDistanceDTO;

public interface UserDistrictLocationRepository extends JpaRepository<UserDistrictLocationEntity, Long> {

	final String QUERY_NEAREST_TRAINERS = 
										"SELECT t.uuid, t.username, t.full_name, t.avatar, t.level, t.pt_booking_credit " + 
										"FROM " + 
										"	(SELECT DISTINCT pt.uuid, pt.username, pt.full_name, pt.avatar, pt.`level`, lv.pt_booking_credit, " + 
										"				((DEGREES(ACOS(LEAST(1.0, COS(RADIANS(?1)) " + 
										"		         * COS(RADIANS(latitude)) " + 
										"		         * COS(RADIANS(?2 - longtitude)) " + 
										"		         + SIN(RADIANS(?1)) " + 
										"		         * SIN(RADIANS(latitude)))))) * 111.111) distance_in_km " + 
										"	FROM user_district_location udl JOIN session_users pt ON udl.user_uuid = pt.uuid AND udl.user_type = 'pt' AND pt.doc_status = 'approved' " +
										"  									JOIN `level` lv ON pt.`level` = lv.code " +
										"	) AS t " + 
										"WHERE t.distance_in_km <= ?3 " +
										"GROUP BY uuid " +
										"ORDER BY t.distance_in_km " + 
										"LIMIT ?4";
	
	List<UserDistrictLocationEntity> findByUserUuid(final String userUuid);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM user_district_location WHERE user_uuid = ?1", nativeQuery = true)
	int deleteByUserUuid(final String userUuid); 
	
	@Query(value = "SELECT * "
					+ "FROM user_district_location "
					+ "WHERE user_uuid = ?1 "
					+ "GROUP BY district_key, user_uuid", nativeQuery = true)
	List<UserDistrictLocationEntity> getByUserUuid(final String userUuid);
	
	List<LocationDistanceDTO> getTopNearestDistrictLocation(final Double latitude, final Double longtitude, final Integer maxRadiusInKm, final int limit);
	
	@Query(value = "SELECT udl.* " + 
					"FROM user_district_location udl JOIN session_users su ON udl.user_uuid = su.uuid AND su.doc_status = 'APPROVED' " + 
					"WHERE udl.user_type = ?1 AND udl.district_key = ?2 " + 
					"ORDER BY id DESC " +
					"LIMIT ?3" , nativeQuery = true)
	List<UserDistrictLocationEntity> searchByUserTypeAndDistrictKey(final String userType, final String districtKey, final int limit);
	
	@Query(value = "SELECT udl.* " + 
					"FROM user_district_location udl JOIN session_users su ON udl.user_uuid = su.uuid AND su.doc_status = 'APPROVED' " + 
					"WHERE udl.user_type = ?1 AND udl.district_key = ?2 " + 
					"ORDER BY id DESC" , nativeQuery = true)
	List<UserDistrictLocationEntity> searchByUserTypeAndDistrictKey(final String userType, final String districtKey);
	
	@Query(value = QUERY_NEAREST_TRAINERS, nativeQuery = true)
	List<Object[]> findNearestTrainersByLocation(final Double latitude, final Double longtitude, final Integer maxRadiusInKm, final int limit);
}
