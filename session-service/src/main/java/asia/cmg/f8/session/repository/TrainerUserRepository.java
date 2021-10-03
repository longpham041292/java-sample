package asia.cmg.f8.session.repository;

import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.session.entity.BasicUserEntity;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;



/**
 * 
 * @author tung.nguyenthanh
 *
 */
@Repository
public interface TrainerUserRepository extends CrudRepository<BasicUserEntity, String> {


    @Query(value = "SELECT count(distinct ss.pt_uuid) "
            + "FROM session_sessions ss left join session_users so on ss.pt_uuid = so.uuid "
            + "WHERE so.activated = true and ss.status in ?1",
            nativeQuery = true)
    int countCurrentActiveTrainers(final List<String> scheduleStatus);

    @Query(value = "SELECT count(if(UNIX_TIMESTAMP(ss.doc_approved_date) >= ?1 "
            + "and UNIX_TIMESTAMP(ss.doc_approved_date) < ?2 , 1, null)) last, "
            + "count(if(UNIX_TIMESTAMP(ss.doc_approved_date) >= ?2 "
            + "and UNIX_TIMESTAMP(ss.doc_approved_date) < ?3 , 1, null)) current "
            + "FROM session_users ss " + "WHERE ss.activated = 1 and ss.doc_status = ?4 "
            + "and UNIX_TIMESTAMP(ss.doc_approved_date) between ?1 and ?3", nativeQuery = true)
    List<Object[]> getStatsTrainerNewCert(final long startTime, final long middleTime,
            final long endTime, final String approve);

    @Query(value = "SELECT count(if(UNIX_TIMESTAMP(ss.session_created_time) >= ?1 "
            + "and UNIX_TIMESTAMP(ss.session_created_time) < ?2 , 1, null)) last, "
            + "count(if(UNIX_TIMESTAMP(ss.session_created_time) >= ?2 "
            + "and UNIX_TIMESTAMP(ss.session_created_time) < ?3 , 1, null)) current "
            + "FROM session_session_daily_mv ss WHERE ss.status in ?4 "
            + "and UNIX_TIMESTAMP(ss.session_created_time) between ?1 and ?3", nativeQuery = true)
    List<Object[]> getStatsTrainerScheduledSession(final long startTime, final long middleTime,
            final long endTime, final List<String> scheduledStatus);

    @Query(value = "SELECT ss.session_created_local_date, count(ss.status) as scheduled, "
            + "count(distinct(ss.user_uuid)) as client, count(distinct(ss.pt_uuid)) as trainer, "
            + "count(if(ss.status in :cancelled, 1, null)) cancelled "
            + "FROM session_session_daily_mv ss "
            + "WHERE (UNIX_TIMESTAMP(ss.session_created_time) between :startTime and :endTime) "
            + "and ss.status in :scheduled group by ss.session_created_local_date",
            nativeQuery = true)
    List<Object[]> getStatsTrainerClientScheduledCancelled(
            @Param("startTime") final long startTime, @Param("endTime") final long endTime,
            @Param("scheduled") final List<String> scheduledStatus,
            @Param("cancelled") final List<String> cancelledStatus);

    @Query(
            value = "SELECT su.uuid, su.full_name, su.avatar ,su.city, su.country, su.username, "
                    + "su.doc_status, su.level, sr.pt_total_fee, sr.total_commission, su.join_date, su.activated, su.user_code, su.phone "
                    + "FROM session_users su LEFT JOIN session_trainer_revenue_mv sr ON su.uuid = sr.pt_uuid and sr.year = ?1 "
                    + "where su.user_type='pt' "
                    + "\n#pageable\n ",
            nativeQuery = true)
    List<Object[]> listTrainers(final int year, final Pageable pageable);
    
    @Query(
            value = "SELECT su.uuid, su.full_name, su.avatar ,su.city, su.country, su.username, "
                    + "su.doc_status, su.level, sr.pt_total_fee, sr.total_commission, su.join_date, su.activated, su.user_code, su.phone "
                    + "FROM session_users su LEFT JOIN session_trainer_revenue_mv sr ON su.uuid = sr.pt_uuid and sr.year = ?1 "
                    + "where su.user_type='pt' "
                    + "and su.join_date BETWEEN ?1 and ?2 "
                    + "\n#pageable\n ",
            nativeQuery = true)
    List<Object[]> listTrainers(final LocalDateTime from, final LocalDateTime to, final Pageable pageable);

    @Query(
            value = "SELECT su.uuid, su.full_name, su.avatar ,su.city, su.country, su.username, "
                    + "su.doc_status, su.level, sr.pt_total_fee, sr.total_commission, su.join_date, su.activated, su.user_code, su.phone "
                    + "FROM session_users su LEFT JOIN session_trainer_revenue_mv sr ON su.uuid = sr.pt_uuid and sr.year = ?1 "
                    + "where su.user_type='pt' "
                    + "and (su.full_name like %?2% or su.username like %?2% or su.email like %?2% or su.user_code like %?2% or su.phone like %?2%) "
                    + "\n#pageable\n ",
            nativeQuery = true)
    List<Object[]> searchTrainers(final int year, final String keyword, final Pageable pageable);
    
    @Query(
            value = "SELECT su.uuid, su.full_name, su.avatar ,su.city, su.country, su.username, "
                    + "su.doc_status, su.level, sr.pt_total_fee, sr.total_commission, su.join_date, su.activated, su.user_code, su.phone "
                    + "FROM session_users su LEFT JOIN session_trainer_revenue_mv sr ON su.uuid = sr.pt_uuid "
                    + "where su.user_type='pt' "
                    + "and su.join_date BETWEEN ?1 and ?2 "
                    + "and (su.full_name like %?3% or su.username like %?3% or su.email like %?3% or su.user_code like %?3% or su.phone like %?3%) "
                    + "\n#pageable\n ",
            nativeQuery = true)
    List<Object[]> searchTrainers(final LocalDateTime from, final LocalDateTime to, final String keyword, final Pageable pageable);

    @Query(
            value = "SELECT su.uuid, su.full_name, su.avatar ,su.city, su.country, su.username, "
                    + "su.doc_status, su.level, sr.pt_total_fee, sr.total_commission, su.join_date, su.activated, su.email, su.user_code "
                    + "FROM session_users su LEFT JOIN session_trainer_revenue_mv sr ON su.uuid = sr.pt_uuid and sr.year = ?1 "
                    + "where su.user_type='pt' "
                    + "order by su.full_name asc ",
            nativeQuery = true)
    List<Object[]> allTrainers(final int year);

    @Query(
            value = "select count(pt) from BasicUserEntity pt where pt.userType = 'pt' and pt.docStatus = ?1")
    Integer countTrainerByDocStatus(DocumentStatusType docStatus);

    @Query(
            value = "select pt from BasicUserEntity pt where pt.userType = 'pt' and pt.activated = 1 and pt.docStatus = 'APPROVED'")
    List<BasicUserEntity> findAllActivePT();

    @Query(
            value = "SELECT su.uuid, max(sa.ended_time) "
                    + "FROM session_users su left join session_availabilities sa on su.uuid = sa.user_uuid "
                    + "where su.user_type = 'pt' and su.activated = 1 and su.doc_status='APPROVED'"
                    + "group by su.uuid", nativeQuery = true)
    List<Object[]> findAllActivePTsWithLatestAvailability();

    @Query(value = "SELECT count(*) " +
            "FROM session_sessions ss " +
            "WHERE " +
            "ss.pt_uuid = :pt_uuid " +
            "AND (UNIX_TIMESTAMP(ss.modified_date) >= :from_date) " +
            "AND ss.status in :statusList ",
            nativeQuery = true)
    Long getNumberOfSessionBurnedOfPT(@Param("pt_uuid") String ptUuid,
                                     @Param("statusList") List<String> statusList,
                                     @Param("from_date") long fromDate);

    @Query(value = "SELECT count(*) " +
            "FROM session_orders so " +
            "WHERE " +
            "so.pt_uuid = :pt_uuid " +
            "AND (so.expired_date IS NULL or (so.num_of_burned < so.num_of_sessions and so.expired_date >= CURRENT_DATE))",
            nativeQuery = true)
    Long getNumberOfActiveClientsOfPT(@Param("pt_uuid") String ptUuid);

    @Query(value = "SELECT counter_value " +
            "FROM counter_counters " +
            "WHERE " +
            "counter_name = :post_counter_name ",
            nativeQuery = true)
    Long getLikesCounter(@Param("post_counter_name") String postCounterName);
}

