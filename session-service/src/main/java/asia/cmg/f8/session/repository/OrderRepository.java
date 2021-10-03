package asia.cmg.f8.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;

/**
 * Created on 12/22/16.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findOneByUuid(final String uuid);

    @Query(value = "SELECT oe " +
            "FROM OrderEntity oe, SessionPackageEntity sp " +
            "WHERE oe.uuid = sp.orderUuid AND sp.uuid = :sessionPackageUuid")
    Optional<OrderEntity> findOneBySessionPackageUuid(@Param("sessionPackageUuid") final String sessionPackageUuid);

    @Query(
            value = "SELECT UNIX_TIMESTAMP(oe.expired_date) as expiredDate, oe.number_of_limit_day, UNIX_TIMESTAMP(min(fs.start_time)) as first_session "
                    + "FROM session_session_packages sp "
                    + "JOIN session_orders oe ON sp.order_uuid = oe.uuid "
                    + "LEFT JOIN (SELECT ss.start_time, ss.package_uuid, ss.status FROM session_sessions ss "
                    + "  WHERE ss.status IN :bookingStatus) as fs ON fs.package_uuid = sp.uuid "
                    + "WHERE sp.user_uuid = :userId AND sp.pt_uuid = :trainerId AND sp.num_of_burned < sp.num_of_sessions "
                    + "AND (oe.expired_date is NULL or oe.expired_date >= CURRENT_DATE) "
                    + "AND sp.status IN :validPackageStatus " + "GROUP BY sp.uuid",
            nativeQuery = true)
    Object[] getMaxValidExpiredDateByUser(@Param("userId") String userId,
                                          @Param("trainerId") String trainerId,
                                          @Param("validPackageStatus") List<String> validPackageStatus,
                                          @Param("bookingStatus") List<String> bookingStatus);

    @Query(
            value = "SELECT so.uuid, so.expired_date, so.number_of_limit_day, min(ss.start_time), sp.uuid as package_uuid "
                    + "FROM session_orders so join session_session_packages sp ON sp.order_uuid = so.uuid "
                    + "join session_sessions ss on ss.package_uuid = sp.uuid "
                    + "where sp.user_uuid = :userId AND sp.pt_uuid = :trainerId AND sp.num_of_burned < sp.num_of_sessions "
                    + "AND (so.expired_date is NULL or so.expired_date >= CURRENT_DATE) "
                    + "AND ss.status in :sessionStatus AND sp.status in :pkgStatus "
                    + "AND (:packageUuid IS NULL || sp.uuid = :packageUuid) "
                    + "group by so.uuid order by so.created_date asc;", nativeQuery = true)
    List<Object[]> getActiveOrdersAndTimeRange(@Param("userId") String userId,
            @Param("trainerId") String trainerId,
            @Param("packageUuid") String packageUuid,
            @Param("sessionStatus") List<String> sessionStatus,
            @Param("pkgStatus") List<String> pkgStatus);

    @Query(value = "select count(0) " +
            "from OrderEntity so join SessionPackageEntity sp on so.uuid = sp.orderUuid " +
            "where sp.ptUuid = :trainerId " +
            "and sp.userUuid = :userId " +
            "and sp.status in :validPackageStatus " +
            "and so.numOfBurned < so.numOfSessions " +
            "and ( so.expiredDate > NOW() or so.expiredDate is null) ")
    Integer countValidOrderByTrainerAndUser(@Param("userId") final String userId,
                                                        @Param("trainerId") final String trainerId,
                                                        @Param("validPackageStatus") final List<SessionPackageStatus> validPackageStatus);
    
    
}
