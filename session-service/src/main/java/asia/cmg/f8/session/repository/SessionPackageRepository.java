package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created on 12/22/16.
 */
@Repository
public interface SessionPackageRepository extends JpaRepository<SessionPackageEntity, Long> {

    @Query(value = "select count(id) from SessionPackageEntity where orderUuid=:orderUuid")
    Integer countPackage(@Param("orderUuid") final String orderUuid);

    Optional<SessionPackageEntity> findOneByOrderUuid(final String orderUuid);  
    
    @Query(value = "SELECT sp " +
            "FROM SessionPackageEntity sp, SessionEntity se " +
            "WHERE se.uuid = :sessionUuid AND " +
            "   se.packageUuid = sp.uuid")
    Optional<SessionPackageEntity> getSessionPackageBySessionUuid(@Param("sessionUuid") final String sessionUuid);

	@Query(value = "SELECT sp FROM SessionPackageEntity sp "
			+ "WHERE sp.uuid = :sessionPackageUuid")
	Optional<SessionPackageEntity> getSessionPackageBySessionPackageUuid(
			@Param("sessionPackageUuid") final String sessionPackageUuid);

    @Query("SELECT sp " +
            "FROM SessionPackageEntity sp " +
            "WHERE sp.uuid = :uuid AND sp.numOfBurned < sp.numOfSessions AND sp.status in :transferableStatus")
    Optional<SessionPackageEntity> getTransferableSessionPackageByUuid(
            @Param("uuid") final String Uuid,
            @Param("transferableStatus") final List<SessionPackageStatus> transferableStatus);

    Optional<SessionPackageEntity> getSessionPackageByUuid(final String uuid);

    @Query(value = "SELECT osp.uuid as oldPackageUuid, " +
            "       nsp.uuid as newPackageUuid, " +
            "       su.uuid as userUuid, " +
            "       su.full_name as userFullName, " +
            "       opt.uuid as oldTrainerUuid, " +
            "       opt.full_name as oldTrainerFullName," +
            "       npt.uuid as newTrainerUuid, " +
            "       npt.full_name as newTrainerFullName," +
            "       so.expired_date as orderExpiredDate, " +
            "       so.num_of_burned as numberOfBurned, " +
            "       so.num_of_sessions as numberOfSessions, " +
            "       su.email as userEmail, " +
            "       opt.email as oldTrainerEmail, " +
            "       npt.email as newTrainerEmail, " +
            "       nsp.created_date as createdDate, " +
            "       nsp.modified_date as modifiedDate " +
            "FROM session_session_packages osp " +
            "LEFT JOIN session_session_packages nsp ON osp.order_uuid = nsp.order_uuid AND nsp.uuid = :newPackageUuid " +
            "LEFT JOIN session_orders so ON so.uuid = nsp.order_uuid " +
            "LEFT JOIN session_users su ON su.uuid = osp.user_uuid " +
            "LEFT JOIN session_users opt ON opt.uuid = osp.pt_uuid " +
            "LEFT JOIN session_users npt ON npt.uuid = nsp.pt_uuid " +
            "WHERE osp.uuid = :oldPackageUuid", nativeQuery = true)
    List<Object[]> getTransferSessionPackageInfo(@Param("oldPackageUuid") String oldPackageUuid,
                                                 @Param("newPackageUuid") String newPackageUuid);

    @Query(value = "SELECT sp.uuid, so.expired_date as expired_date, sp.num_of_burned, sp.num_of_sessions, sp.pt_uuid " +
                   "FROM session_session_packages sp " +
                        "JOIN session_orders so ON sp.order_uuid=so.uuid " +
                    "WHERE sp.user_uuid = :userUuid AND sp.status <> :status " +
                    "AND (UNIX_TIMESTAMP(so.expired_date) > :expiredDate OR so.expired_date IS NULL) " +
                    "ORDER BY so.expired_date ASC", nativeQuery = true)
    List<Object[]> getSessionPackageEntitiesbyUser(@Param("userUuid")final String userUuid,
                                                                    @Param("expiredDate")final long expiredDate,
                                                                    @Param("status")final SessionPackageStatus differentStatus);
    @Query(
            value = "SELECT count(distinct su.uuid) as new_customer "
                    + "FROM session_session_packages ssp JOIN session_users su on su.uuid = ssp.user_uuid "
                    + "WHERE ssp.pt_uuid = :ptUuid AND su.activated = TRUE "
                    + "AND (unix_timestamp(ssp.created_date) between :startTime and :endTime) "
                    + "AND ssp.user_uuid not in (SELECT DISTINCT user_uuid FROM session_session_packages sp "
                    + "WHERE sp.pt_uuid = :ptUuid AND unix_timestamp(sp.created_date) < :startTime)",
            nativeQuery = true)
    int countNewCustomerOfTrainerInRange(@Param("ptUuid") final String ptUuid,
                                         @Param("startTime") final long startTime,
                                         @Param("endTime") final long endTime);

	@Modifying
	@Query(value = "UPDATE session_session_packages SET last_status = status, status = :status "
			+ "WHERE uuid = :uuid", nativeQuery = true)
	void updateSessionPackageStatus(@Param("uuid") final String packageSessionUuid,
			@Param("status") final String status);
}
