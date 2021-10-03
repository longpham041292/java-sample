package asia.cmg.f8.session.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPTiMatchRepository extends JpaRepository<UserPtiMatchEntity, Long> {

	String QUERY_PAGEABLE = " \n#pageable\n ";
	
	String QUERY_SEARCHED_MATCHED_TRAINERS = "SELECT su.uuid, su.username, su.full_name, su.avatar," + 
											"		pti.average, pti.personality, pti.training_style, pti.interest," + 
											"		su.`level` as pt_level, lvl.pt_booking_credit " + 
											"FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.doc_status = 'approved' " + 
											"                       JOIN `level` lvl ON su.`level` = lvl.code " + 
											"WHERE eu_uuid = :euUuid AND average > 0 " + 
											"ORDER BY average DESC" + QUERY_PAGEABLE;
	
	String COUNT_QUERY_SEARCHED_MATCHED_TRAINERS = 	"SELECT COUNT(*) " + 
													"FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.doc_status = 'approved' " + 
													"                       JOIN `level` lvl ON su.`level` = lvl.code " + 
													"WHERE eu_uuid = :euUuid AND average > 0";
	
	@Query(	value = QUERY_SEARCHED_MATCHED_TRAINERS, 
			countQuery = COUNT_QUERY_SEARCHED_MATCHED_TRAINERS, 
			nativeQuery = true)
	Page<Object[]> searchMatchedTrainer(@Param(value = "euUuid") final String euUuid, final Pageable pageable);
}
