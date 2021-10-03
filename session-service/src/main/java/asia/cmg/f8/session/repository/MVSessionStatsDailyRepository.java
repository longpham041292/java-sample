package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.MVSessionStatsDailyEntity;

import asia.cmg.f8.session.wrapper.dto.SessionFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MVSessionStatsDailyRepository extends
        JpaRepository<MVSessionStatsDailyEntity, Long> {

    @Query(value = "select max(ssd.statsDate) from MVSessionStatsDailyEntity ssd")
    Optional<Object> findMaxByStatsDate();

    /**
     * Perform insert select data from session_session_histories table to
     * session_session_stats_daily_mv by time range.
     * 
     * @param open
     * @param pending
     * @param confirmed
     * @param ptCancelled
     * @param burned
     * @param cancelled
     * @param completed
     * @param euCancelled
     * @param expired
     * @param transferred
     * @param fromDate
     * @param endTime
     * @param timezone
     * @return
     */
    @Modifying
    @Query(
            value = "INSERT INTO session_session_stats_daily_mv "
                    + "(session_uuid, stats_local_date, stats_date, user_uuid, pt_uuid, old_pt_uuid, package_uuid, "
                    + "open, pending, confirmed, pt_cancelled, burned, cancelled, completed, eu_cancelled, expired, transferred) "
                    + "SELECT htr.session_uuid, date(CONVERT_TZ(htr.created_date, '+00:00', :timezone)), htr.created_date, "
                    + "htr.user_uuid, htr.new_pt_uuid, htr.old_pt_uuid, htr.new_package_uuid, "
                    + "count(if(htr.new_status = :open, 1, null)) open, "
                    + "count(if(htr.new_status = :pending, 1, null)) pending, "
                    + "count(if(htr.new_status = :confirmed, 1, null)) confirmed, "
                    + "count(if(htr.new_status = :ptCancelled, 1, null)) trainer_cancelled, "
                    + "count(if(htr.new_status = :burned, 1, null)) burned, "
                    + "count(if(htr.new_status = :cancelled, 1, null)) cancelled, "
                    + "count(if(htr.new_status = :completed, 1, null)) completed, "
                    + "count(if(htr.new_status = :euCancelled, 1, null)) eu_cancelled, "
                    + "count(if(htr.new_status = :expired, 1, null)) expired, "
                    + "count(if(htr.new_status = :transferred, 1, null)) transferred "
                    + "FROM ( "
                    + "select ssh.created_date, ssh.session_uuid, ssh.new_status, "
                    + "ssh.user_uuid, ssh.new_package_uuid, ssh.new_pt_uuid, ssh.old_pt_uuid "
                    + "from session_session_histories ssh "
                    + "where UNIX_TIMESTAMP(ssh.created_date) between :startTime and :endTime "
                    + "order by ssh.created_date desc "
                    + ") htr "
                    + "GROUP BY date(CONVERT_TZ(htr.created_date, '+00:00', :timezone)), htr.session_uuid",
            nativeQuery = true)
    @SuppressWarnings("PMD.ExcessiveParameterList")
    Integer insertSessionStatsByTimeRange(@Param("open") final String open,
            @Param("pending") final String pending, @Param("confirmed") final String confirmed,
            @Param("ptCancelled") final String ptCancelled, @Param("burned") final String burned,
            @Param("cancelled") final String cancelled, @Param("completed") final String completed,
            @Param("euCancelled") final String euCancelled, @Param("expired") final String expired,
            @Param("transferred") final String transferred,
            @Param("startTime") final long fromDate, @Param("endTime") final long endTime,
            @Param("timezone") final String timezone);

	@Query(name = "MVSessionStatsDailyEntity.getTransferredFinancialReport", nativeQuery = true)
	List<SessionFinancial> getTransferredFinancialReport(final LocalDate startDate, final LocalDate endDate,
			final String timeZone);

    /**
     * In Financial report, open status include: open, pending, cancelled.
     * 
     * @param startDate
     * @param endDate
     * @return
     */
	@Query(name = "MVSessionStatsDailyEntity.getOpenFinancialReport", nativeQuery = true)
	List<SessionFinancial> getOpenFinancialReport(final LocalDate startDate, final LocalDate endDate,
			final String timeZone);

    /**
     * Because of duplicate confirmed for one session may happen, we use inner
     * query to group by session, and the outer query to prevent duplicate
     * confirmed.
     * 
     * @param userUuid
     * @param startTime
     * @param middleTime
     * @param endTime
     * @return
     */
    @Query(
            value = "SELECT count(if(rows.last > 0, 1, null)), count(if(rows.current > 0, 1, null)) "
                    + "FROM ("
                    + "SELECT count(if(UNIX_TIMESTAMP(stats_date) >= :startTime "
                    + "and UNIX_TIMESTAMP(stats_date) < :middleTime , 1, null)) last, "
                    + "count(if(UNIX_TIMESTAMP(stats_date) >= :middleTime "
                    + "and UNIX_TIMESTAMP(stats_date) < :endTime , 1, null)) current "
                    + "FROM session_session_stats_daily_mv "
                    + "WHERE user_uuid = :userUuid and confirmed > 0 "
                    + "and (UNIX_TIMESTAMP(stats_date) between :startTime and :endTime) "
                    + "group by session_uuid) rows", nativeQuery = true)
    List<Object[]> statsConfirmedSessionInTimeRange(@Param("userUuid") final String userUuid,
            @Param("startTime") final long startTime, @Param("middleTime") final long middleTime,
            @Param("endTime") final long endTime);

}
