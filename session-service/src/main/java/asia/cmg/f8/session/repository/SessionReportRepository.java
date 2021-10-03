package asia.cmg.f8.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.session.dto.SessionExportDTO;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

@Repository
public interface SessionReportRepository extends JpaRepository<SessionEntity, Long> {

	String exportSessionQueryFilterByCheckingClubUuid = "SELECT new asia.cmg.f8.session.dto.SessionExportDTO(ss.id, ss.startTime, ss.endTime, ss.status," + 
																											"eu.uuid, eu.fullName, pt.fullName, pt.level," + 
																											"ss.bookingClubName, ss.bookingClubAddress," + 
																											"ss.checkinClubName, ss.checkinClubAddress, ss.checkinDate) " + 
														"FROM SessionEntity ss " + 
														"	JOIN BasicUserEntity eu ON ss.userUuid = eu.uuid " + 
														"	JOIN BasicUserEntity pt ON ss.ptUuid = pt.uuid " + 
														"WHERE ss.checkinClubUuid = :checkingClubUuid " +
														"   AND ss.status = :status " + 
														"	AND ss.startTime BETWEEN :startTime AND :endTime";
	
	String exportSessionQuery = "SELECT new asia.cmg.f8.session.dto.SessionExportDTO(ss.id, ss.startTime, ss.endTime, ss.status," + 
																					"eu.uuid, eu.fullName, pt.fullName, pt.level," + 
																					"ss.bookingClubName, ss.bookingClubAddress," + 
																					"ss.checkinClubName, ss.checkinClubAddress, ss.checkinDate) " + 
								"FROM SessionEntity ss " + 
								"	JOIN BasicUserEntity eu ON ss.userUuid = eu.uuid " + 
								"	JOIN BasicUserEntity pt ON ss.ptUuid = pt.uuid " + 
								"WHERE ss.status = :status " + 
								"	AND ss.startTime BETWEEN :startTime AND :endTime";
						
	@Query(value = exportSessionQueryFilterByCheckingClubUuid)
	List<SessionExportDTO> exportSessionQueryFilterByCheckingClubUuid(@Param("status") SessionStatus status,
																	@Param("startTime") LocalDateTime starTime,
																	@Param("endTime") LocalDateTime endTime,
																	@Param("checkingClubUuid") String checkingClubUuid);
	
	@Query(value = exportSessionQuery)
	List<SessionExportDTO> exportSessionQuery(@Param("status") SessionStatus status,
											@Param("startTime") LocalDateTime starTime,
											@Param("endTime") LocalDateTime endTime);
}
