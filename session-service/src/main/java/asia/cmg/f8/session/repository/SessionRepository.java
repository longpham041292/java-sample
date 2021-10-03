package asia.cmg.f8.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 11/22/16.
 */
@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface SessionRepository extends JpaRepository<SessionEntity, Long>, JpaSpecificationExecutor {
	final String COUNT_NUM_OF_CONFIRMED = ",(SELECT count(ss.uuid) FROM session_sessions ss WHERE ss.package_uuid = sp.uuid AND ss.pt_uuid = sp.pt_uuid AND ss.user_uuid = sp.user_uuid AND ss.status = 'CONFIRMED') AS num_of_confirmed ";
    long MAX_TIMESTAMP = 9999999999L;

    Optional<SessionEntity> findOneByUuid(final String uuid);

    @Query(value = "SELECT ss.uuid as session_uuid, sum(so.num_of_burned) as session_burned, sum(so.num_of_sessions) as session_number, " +
            "  ss.start_time, ss.end_time " +
            "FROM session_session_packages sp JOIN session_orders so ON so.uuid = sp.order_uuid " +
            "LEFT JOIN ( " +
            "  SELECT * from session_sessions session " +
            "  WHERE session.user_uuid = :userUuid AND " +
            "        session.status = :confirmedStatus AND " +
            "        session.start_time BETWEEN now() AND now() + INTERVAL 7 DAY " +
            "  ORDER BY session.start_time ASC LIMIT 1 ) ss ON so.user_uuid = ss.user_uuid " +
            "WHERE sp.user_uuid = :userUuid AND sp.status in :validPackageStatus " +
            "      AND so.num_of_burned < so.num_of_sessions " +
            "      AND (so.expired_date >= CURRENT_DATE OR so.expired_date is NULL) " +
            "GROUP BY so.user_uuid", nativeQuery = true)
    List<Object[]> getCountDownSession(@Param("userUuid") final String userId,
                                       @Param("validPackageStatus") final List<String> validPackageStatus,
                                       @Param("confirmedStatus") final String confirmedStatus);

    @Query(
            value = "select ss.* "
                    + "from session_sessions ss join session_session_packages sp on ss.package_uuid = sp.uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE ss.user_uuid = :userId AND ss.pt_uuid = :trainerId "
                    + "AND ( ss.status in :availableStatus OR (ss.status = 'PENDING' AND ss.start_time < NOW())) "
                    + "order by if(so.expired_date is null, " + MAX_TIMESTAMP
                    + ", UNIX_TIMESTAMP(so.expired_date)) - UNIX_TIMESTAMP(now())",
            nativeQuery = true)
    List<SessionEntity> getAvailableSessionsByUserAndTrainer(@Param("userId") String userId,
                                                             @Param("trainerId") String trainerId,
                                                             @Param("availableStatus") List<String> availableStatus);

    @Query(
            value = "select ss.uuid as sessionUuid, ss.start_time, so.uuid as orderUuid, so.expired_date, so.number_of_limit_day "
                    + "from session_sessions ss join session_session_packages sp on ss.package_uuid = sp.uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE ss.user_uuid = :userId AND ss.pt_uuid = :trainerId AND sp.Status != 'INACTIVE' "
                    + "AND ( ss.status in :availableStatus OR (ss.status = 'PENDING' AND ss.start_time < NOW())) "
                    + "AND (so.expired_date IS NULL OR UNIX_TIMESTAMP(so.expired_date) >= UNIX_TIMESTAMP(CURRENT_DATE)) "
                    + "order by if(so.expired_date is null, " + MAX_TIMESTAMP
                    + ", UNIX_TIMESTAMP(so.expired_date)), so.number_of_limit_day, so.created_date, ss.id",
            nativeQuery = true)
    List<Object[]> getAvailableSessionAndOrderByUserAndTrainer(@Param("userId") String userId,
                                                               @Param("trainerId") String trainerId,
                                                               @Param("availableStatus") List<String> availableStatus);
    
    @Query(
            value = "select ss.uuid as sessionUuid, ss.start_time, so.uuid as orderUuid, so.expired_date, so.number_of_limit_day "
                    + "from session_sessions ss join session_session_packages sp on ss.package_uuid = sp.uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE ss.user_uuid = :userId AND ss.package_uuid = :packageId AND sp.Status != 'INACTIVE' "
                    + "AND ( ss.status in :availableStatus OR (ss.status = 'PENDING' AND ss.start_time < NOW())) "
                    + "AND (so.expired_date IS NULL OR UNIX_TIMESTAMP(so.expired_date) >= UNIX_TIMESTAMP(CURRENT_DATE)) "
                    + "order by if(so.expired_date is null, " + MAX_TIMESTAMP
                    + ", UNIX_TIMESTAMP(so.expired_date)), so.number_of_limit_day, so.created_date, ss.id",
            nativeQuery = true)
    List<Object[]> getAvailableSessionAndOrderByUserAndPackage(@Param("userId") String userId,
                                                               @Param("packageId") String packageId,
                                                               @Param("availableStatus") List<String> availableStatus);
    

    @Query(
            value = "SELECT ss.package_uuid, ss.status, min(ss.start_time) as start_time, ss.pt_uuid, sp.num_of_sessions, sp.num_of_burned " +
            		"FROM session_sessions ss " +
            		"JOIN session_session_packages sp ON sp.uuid = ss.package_uuid " + 
            		"JOIN session_orders so ON sp.order_uuid = so.uuid " +
                    "WHERE (so.expired_date IS NULL OR UNIX_TIMESTAMP(so.expired_date) >= CURRENT_DATE) AND ss.user_uuid = :userUuid AND ss.status=:status AND UNIX_TIMESTAMP(ss.start_time) between :startTime and :endTime " +
                    "GROUP BY package_uuid, status order by start_time asc ",
            nativeQuery = true)
    List<Object[]> getSessionEntitiesScheduleByUser( @Param("userUuid") final String userUuid,
                                                     @Param("startTime") final long startTime,
                                                     @Param("endTime") final long endTime,
                                                     @Param("status") final String status);

    @Query(value = "SELECT session FROM SessionEntity session "
            + "WHERE session.packageUuid = :packageUuid " + "AND (session.status IN :status "
            + "OR ( session.status IN :bookedStatus AND session.startTime > current_timestamp )) ")
    List<SessionEntity> filterByPackageAndStatus(@Param("packageUuid") String uuid,
                                                 @Param("status") List<SessionStatus> status,
                                                 @Param("bookedStatus") List<SessionStatus> bookedStatus);

    @Query(
            value = "SELECT ss.user_uuid, su.avatar, su.full_name, so.commission, so.price, sp.num_of_sessions, "
                    + "count(if(ss.status IN :open , 1, NULL)) as open, "
                    + "count(if(ss.status = :confirmed , 1, NULL)) as confirmed, "
                    + "count(if(ss.status = :euCancelled , 1, NULL)) as euCancelled, "
                    + "count(if(ss.status = :burned , 1, NULL)) as burned, "
                    + "count(if(ss.status = :completed , 1, NULL)) as completed, "
                    + "count(if(ss.status = :expired , 1, NULL)) as expired, "
                    + "count(if(ss.status = :transferred , 1, NULL)) as transferred, "
                    + "count(if(ss.status in :revenue , 1, NULL)) as ptFeeSession, "
                    + "count(*) as revenueSession "
                    + "FROM session_sessions ss JOIN session_session_packages sp on ss.package_uuid = sp.uuid "
                    + "JOIN session_orders so ON sp.order_uuid = so.uuid "
                    + "LEFT JOIN session_users su ON su.uuid = ss.user_uuid "
                    + "WHERE ss.pt_uuid = :trainerUuid "
                    + "AND (so.expired_date is NULL or (UNIX_TIMESTAMP(so.expired_date) > :startTime )) "
                    + "GROUP BY so.uuid \n#pageable\n", nativeQuery = true)
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<Object[]> getActionStatisticByTrainer(@Param("trainerUuid") final String trainerUuid,
                                               @Param("startTime") final long startTime,
                                               @Param("open") List<String> open,
                                               @Param("confirmed") final String confirmed,
                                               @Param("euCancelled") final String euCancelled,
                                               @Param("burned") final String burned,
                                               @Param("completed") final String completed,
                                               @Param("expired") final String expired,
                                               @Param("transferred") final String transferred,
                                               @Param("revenue") final List<String> revenue,
                                               final Pageable pageable);

    @Query(value = "SELECT ss.uuid as session_uuid, so.product_name, so.uuid as order_uuid, " +
            "   @returnStatus \\:= if((DATE(ss.status_modified_date) = CURDATE()), ss.last_status, ss.status) as status," +
            "   ((if(@returnStatus in :revenueStatus, 1, 0) / so.num_of_sessions) * so.price * (100 - so.commission) / 100) as serviceFee, " +
            "   so.order_date, ss.user_uuid, so.expired_date, " +
            "   if(@returnStatus in :revenueStatus, ss.start_time, 'NOT_STARTED') as usedDate ,su.full_name ,su.username " +
            "FROM session_sessions ss JOIN session_session_packages sp on ss.package_uuid = sp.uuid " +
            "JOIN session_orders so ON sp.order_uuid = so.uuid " +
            "LEFT JOIN session_users su ON su.uuid = ss.user_uuid " +
            "WHERE ss.pt_uuid = :trainerUuid " +
            "AND ( UNIX_TIMESTAMP(ss.status_modified_date) BETWEEN :startTime AND :endTime "
            +
            "     OR " +
            "     (DATE(ss.status_modified_date) = CURDATE() AND UNIX_TIMESTAMP(ss.last_status_modified_date) BETWEEN :startTime AND :endTime) "
            +
            "    )", nativeQuery = true)
    List<Object[]> getActivitiesByTrainer(@Param("trainerUuid") final String trainerUuid,
                                          @Param("startTime") final long startTime,
                                          @Param("endTime") final long endTime,
                                          @Param("revenueStatus") final List<String> revenueStatus);

    @Query(value = "SELECT se FROM SessionEntity se WHERE se.packageUuid IN (" +
            "       SELECT sp.uuid FROM SessionPackageEntity sp, OrderEntity oe " +
            "       WHERE oe.uuid = sp.orderUuid AND DATE(oe.expiredDate) <= CURRENT_DATE) " +
            "   AND se.status in :expirableStatus")
    List<SessionEntity> getExpiredSession(@Param("expirableStatus") final List<SessionStatus> expirableStatus);

    @Query(
            value = "SELECT date(CONVERT_TZ(ss.status_modified_date, '+00:00', :timezone)), "
                    + "sum(((100 - so.commission) / 100) * so.price / so.num_of_sessions) amount, "
                    + "count(1) session "
                    + "FROM session_session_packages sp join session_sessions ss on sp.uuid=ss.package_uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE sp.pt_uuid = :ptUuid "
                    + "and (UNIX_TIMESTAMP(ss.status_modified_date) between :startTime and :endTime) "
                    + "and ss.status in :status "
                    + "GROUP BY date(CONVERT_TZ(ss.status_modified_date, '+00:00', :timezone)) asc",
            nativeQuery = true)
    List<Object[]> getStatsForTrainerInTimePeriod(@Param("ptUuid") final String ptUuid,
                                                  @Param("startTime") final long startTime, @Param("endTime") final long endTime,
                                                  @Param("status") final List<String> revenueStatus,
                                                  @Param("timezone") final String timezone);

    @Query(
            value = "SELECT ss.start_time, "
                    + "(((100 - so.commission) / 100) * so.price / so.num_of_sessions) amount "
                    + "FROM session_sessions ss " 
                    + "join session_session_packages sp on sp.uuid=ss.package_uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE ss.pt_uuid = :ptUuid "
                    + "and (UNIX_TIMESTAMP(ss.start_time) between :startTime and :endTime) "
                    + "and ss.status in :status", nativeQuery = true)
    List<Object[]> getStatsSessionForTrainerInTimePeriod(@Param("ptUuid") final String ptUuid,
                                                         @Param("startTime") final long startTime, @Param("endTime") final long endTime,
                                                         @Param("status") final List<String> revenueStatus);
//////////////////// 2-11-2017
    @Query(
            value = "SELECT su.uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions " + COUNT_NUM_OF_CONFIRMED
                    + "FROM session_sessions ss "
            		+"join session_session_packages sp on sp.uuid=ss.package_uuid "
                    + "join session_users su on sp.user_uuid = su.uuid "
                    + "WHERE ss.pt_uuid = :ptUuid and su.user_type = 'eu' "
                    + "and (UNIX_TIMESTAMP(ss.start_time) between :startTime and :endTime) "
                    + "and ss.status in :status group by sp.uuid", nativeQuery = true)
    List<Object[]> getClientBurnedInOneDayOfTrainer(@Param("ptUuid") final String ptUuid,
                                                    @Param("startTime") final long startTime, @Param("endTime") final long endTime,
                                                    @Param("status") final List<String> revenueStatus);
///////////////
    @Query(
            value = "SELECT count(if(data.date >= :startTime && data.date < :middleTime && data.status in :status, 1, null)) last, "
                    + "              count(if(data.date >= :middleTime && data.date < :endTime && data.status in :status, 1, null)) current "
                    + "FROM (SELECT if((DATE(ss.start_time) = CURDATE()), ss.last_status, ss.status) as status, "
                    + "      if((DATE(ss.start_time) = CURDATE()), UNIX_TIMESTAMP(ss.start_time), UNIX_TIMESTAMP(ss.start_time)) as date "
                    + "FROM session_sessions ss "
                    + "WHERE ss.pt_uuid = :trainerUuid AND ( UNIX_TIMESTAMP(ss.start_time) BETWEEN :startTime AND :endTime "
                    + "OR (DATE(ss.start_time) = CURDATE() AND UNIX_TIMESTAMP(ss.start_time) BETWEEN :startTime AND :endTime))) as data;",
            nativeQuery = true)
    Object[] calculateBurnedSessionOfTrainerByTimeRange(@Param("trainerUuid") final String trainerUuid,
                                                        @Param("startTime") final long startTime,
                                                        @Param("middleTime") final long middleTime,
                                                        @Param("endTime") final long endTime,
                                                        @Param("status") final List<String> status);

    @Query(
            value = "SELECT sum((if(data.date >= :startTime && data.date < :middleTime && data.status in :status, 1, 0) / data.num_of_sessions ) * data.price * (100 - data.commission) / 100) as last, "
                    + "              sum((if(data.date >= :middleTime && data.date < :endTime && data.status in :status, 1, 0) / data.num_of_sessions ) * data.price * (100 - data.commission) / 100) as current "
                    + "FROM (SELECT if((DATE(ss.start_time) = CURDATE()), ss.last_status, ss.status) as status, "
                    + "      if((DATE(ss.start_time) = CURDATE()), UNIX_TIMESTAMP(ss.start_time) , UNIX_TIMESTAMP(ss.start_time)) as date, "
                    + "      so.num_of_sessions, so.price, so.commission "
                    + "FROM session_sessions ss JOIN session_session_packages sp ON ss.package_uuid = sp.uuid JOIN session_orders so ON sp.order_uuid = so.uuid "
                    + "WHERE ss.pt_uuid = :trainerUuid AND ( UNIX_TIMESTAMP(ss.start_time) BETWEEN :startTime AND :endTime "
                    + "OR (DATE(ss.start_time) = CURDATE() AND UNIX_TIMESTAMP(ss.start_time) BETWEEN :startTime AND :endTime))) as data;",
            nativeQuery = true)
    List<Object[]> calculatePaidOutOfTrainerByTimeRange(@Param("trainerUuid") final String trainerUuid,
                                                        @Param("startTime") final long startTime,
                                                        @Param("middleTime") final long middleTime,
                                                        @Param("endTime") final long endTime,
                                                        @Param("status") final List<String> status);
    @Query("SELECT se " +
            "FROM SessionEntity se " +
            "WHERE se.userUuid = :userId AND " +
            "(se.status in :bookingStatus AND se.startTime > NOW())")
    List<SessionEntity> getBookingSessionsByUser(@Param("userId") String userId,
                                                 @Param("bookingStatus") List<SessionStatus> bookingStatus);

    @Query("SELECT se " +
            "FROM SessionEntity se " +
            "WHERE se.ptUuid = :trainerId AND " +
            "(se.status in :bookingStatus AND se.startTime > NOW())")
    List<SessionEntity> getBookingSessionsByTrainer(@Param("trainerId") String trainerId,
                                                    @Param("bookingStatus") List<SessionStatus> bookingStatus);
    
    @Query(value = "select distinct user_uuid, pt_uuid from session_sessions where user_uuid  not in (select user_uuid from reset_auto_follow) and pt_uuid  not in (select user_uuid from reset_auto_follow)", nativeQuery = true)
    List<Object[]> getAllUserFromSession();  
    
    @Query(value = "select distinct user_uuid, pt_uuid from session_sessions where user_uuid = :userUuid or pt_uuid= :userUuid ", nativeQuery = true)
    List<Object[]> getAllUserFromSessionByUserUuid(@Param("userUuid") final String userUuid);  
    
    @Query("SELECT se " +
            "FROM SessionEntity se " +
            "WHERE se.status LIKE :bookingStatus AND se.startTime < NOW() ")
    List<SessionEntity> getAllPastSessionByStatus(@Param("bookingStatus") SessionStatus bookingStatus);
    
    @Query(value = "SELECT COUNT(*) FROM session_sessions WHERE user_uuid = ?1", nativeQuery = true)
    int countAllHistorySession(final String eu_uuid);
}
