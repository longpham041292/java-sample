package asia.cmg.f8.profile.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.profile.database.entity.UserPtiMatchEntity;
import asia.cmg.f8.profile.dto.SuggestedTrainersDTO;
import asia.cmg.f8.profile.dto.UserPTiMatchDTO_V2;

@Repository
public interface UserPTiMatchRepository extends JpaRepository<UserPtiMatchEntity, Long> {

	@Query(value = "SELECT pti.* FROM user_ptimatch pti WHERE eu_uuid = :eu_uuid AND average > 0 ORDER BY average DESC \n#pageable\n", countQuery = "SELECT COUNT(*) FROM user_ptimatch WHERE eu_uuid = :eu_uuid AND average > 0", nativeQuery = true)
	Page<UserPtiMatchEntity> getByEuUuid(@Param(value = "eu_uuid") final String euUuid, final Pageable pageable);

	List<UserPTiMatchDTO_V2> getByEuUuid_V2(@Param(value = "eu_uuid") final String euUuid,
			@Param("offset") final int offset, @Param("size") final int size);

	@Query(value = "SELECT pti.* FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.`level` in :levels "
			+ "WHERE eu_uuid = :eu_uuid AND average > 0 "
			+ "ORDER BY average DESC \n#pageable\n", countQuery = "SELECT COUNT(*) FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.`level` in :levels "
					+ "WHERE eu_uuid = :eu_uuid AND average > 0", nativeQuery = true)
	Page<UserPtiMatchEntity> getByEuUuidAndPtLevel(@Param(value = "eu_uuid") final String euUuid,
			@Param("levels") final List<String> levels, final Pageable pageable);

	List<UserPTiMatchDTO_V2> getByEuUuidAndPtLevel_V2(@Param(value = "eu_uuid") final String euUuid,
			@Param("levels") final List<String> levels, @Param("offset") final int offset,
			@Param("size") final int size);

	@Query(value = "SELECT pti.* " + "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid "
			+ "						  JOIN user_skills sk ON su.uuid = sk.user_uuid AND sk.skill_key IN :skills "
			+ "WHERE pti.eu_uuid = :eu_uuid AND average > 0 "
			+ "ORDER BY average DESC \n#pageable\n", countQuery = "SELECT COUNT(*) "
					+ "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid "
					+ "						  JOIN user_skills sk ON su.uuid = sk.user_uuid AND sk.skill_key IN :skills "
					+ "WHERE pti.eu_uuid = :eu_uuid AND average > 0", nativeQuery = true)
	Page<UserPtiMatchEntity> getByEuUuidAndPtSkills(@Param("eu_uuid") final String euUuid,
			@Param("skills") final Set<String> skills, final Pageable pageable);

	List<UserPTiMatchDTO_V2> getByEuUuidAndPtSkills_V2(@Param("eu_uuid") final String euUuid,
			@Param("skills") final Set<String> skills, @Param("offset") final int offset,
			@Param("size") final int size);

	@Query(value = "SELECT pti.* FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.enable_subscribe = 1 "
			+ "WHERE eu_uuid = :eu_uuid AND average > 0 "
			+ "ORDER BY average DESC \n#pageable\n", countQuery = "SELECT COUNT(*) FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.enable_subscribe = 1 "
					+ "WHERE eu_uuid = :eu_uuid AND average > 0", nativeQuery = true)
	Page<UserPtiMatchEntity> getByEuUuidAndPtEnableSubscribe(@Param(value = "eu_uuid") final String euUuid,
			final Pageable pageable);

	List<UserPTiMatchDTO_V2> getByEuUuidAndPtEnableSubscribe_V2(@Param(value = "eu_uuid") final String euUuid,
			@Param("offset") final int offset, @Param("size") final int size);

	@Query(value = "SELECT * FROM user_ptimatch WHERE eu_uuid = :eu_uuid AND pt_uuid = :pt_uuid LIMIT 1", nativeQuery = true)
	Optional<UserPtiMatchEntity> getByEuUuidAndPtUuid(@Param(value = "eu_uuid") final String euUuid,
			@Param(value = "pt_uuid") final String ptUuid);

	@Query(value = "SELECT COUNT(*) FROM user_ptimatch pti WHERE eu_uuid = :eu_uuid", nativeQuery = true)
	int countPtiResultByUserUuid(@Param(value = "eu_uuid") final String euUuid);

	@Query(value = "SELECT new asia.cmg.f8.profile.dto.SuggestedTrainersDTO(up.ptUuid, bu.userName, bu.fullName, bu.avatar, bu.level, lv.ptBookingCredit) "
			+ "FROM UserPtiMatchEntity up JOIN BasicUserEntity bu ON up.ptUuid = bu.uuid JOIN LevelEntity lv ON bu.level = lv.code "
			+ "WHERE up.euUuid = ?1 AND up.ptUuid NOT IN ?2 AND bu.docStatus = ?3 ORDER BY up.average DESC")
	List<SuggestedTrainersDTO> findPtiMatch(String euUuid, List<String> ptUuid, DocumentStatusType docStatus, Pageable pageable);
}
