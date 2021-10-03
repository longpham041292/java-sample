package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.SessionHistoryEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created on 11/22/16.
 */
@Repository
public interface SessionHistoryRepository extends JpaRepository<SessionHistoryEntity, Long> {
    String TIMEZONE = "timezone";

	@Modifying
	@Query(value = "insert into session_session_daily_mv(package_uuid, pt_uuid, session_created_local_date, session_created_time, session_uuid, `status`, user_uuid) "
			+ "select ssh.new_package_uuid, ssh.new_pt_uuid, DATE(CONVERT_TZ(ssh.created_date, '+00:00', :timezone)),  ssh.created_date, ssh.session_uuid, ssh.new_status, ssh.user_uuid "
					+ "from session_session_histories ssh "
					+ "where ssh.created_date > (SELECT MAX(sd.session_created_time) FROM session_session_daily_mv sd) "
					 + "and ssh.created_date = ( "
						+ "select max(ssh2.created_date)  "
					    + "from session_session_histories ssh2  "
					    + "where ssh2.session_uuid = ssh.session_uuid "
					    + ") "
					+ "ORDER BY  ssh.created_date ASC "
					+ "on duplicate key update session_created_time = ssh.created_date;", nativeQuery = true)
	void updateSessionHistoryByTimeRange(@Param(TIMEZONE) final String timezone);

    @Query(
            value = "SELECT sh1.* "
                    + "FROM session_session_histories sh1 left outer join session_session_histories sh2 "
                    + "on sh1.session_uuid = sh2.session_uuid "
                    + "and date(CONVERT_TZ(sh1.created_date, '+00:00', :timezone)) = date(CONVERT_TZ(sh2.created_date, '+00:00', :timezone)) "
                    + "and (sh1.created_date < sh2.created_date "
                    + "or (sh1.created_date = sh2.created_date AND sh1.id < sh2.id and sh1.session_uuid < sh2.session_uuid )) "
                    + "WHERE sh2.created_date is null and UNIX_TIMESTAMP(sh1.created_date) <= :endTime "
                    + "ORDER BY sh1.created_date asc \n#pageable\n", nativeQuery = true)
    List<SessionHistoryEntity> getSessionHistoryToEndTime(@Param("endTime") final long endTime,
            @Param(TIMEZONE) final String timezone, final Pageable pageable);

    @Query(
            value = "SELECT distinct(year(CONVERT_TZ(ssh.created_date, '+00:00', :timezone))) year, ssh.new_pt_uuid ptUuid, "
                    + "sum(( so.price / so.num_of_sessions ) * (100 - so.commission)/100) ptFee, "
                    + "sum(if(so.free_order, -(so.price/so.num_of_sessions ) * (100 - so.commission)/100 , ( so.price / so.num_of_sessions ) * so.commission / 100)) commission "
                    + "FROM session_session_histories ssh join session_session_packages sp on ssh.new_package_uuid = sp.uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE UNIX_TIMESTAMP(ssh.created_date) between :startTime and :endTime "
                    + "and ssh.new_status in :status "
                    + "GROUP BY year(CONVERT_TZ(ssh.created_date, '+00:00', :timezone)), ssh.new_pt_uuid ",
            nativeQuery = true)
    List<Object[]> getTrainerRevenueReport(@Param("startTime") final long fromDate,
            @Param("endTime") final long endTime, @Param("status") List<String> revenueStatus,
            @Param(TIMEZONE) final String timezone);

    @Query(value = "select date(min(sh.createdDate)) from SessionHistoryEntity sh")
    Optional<Object> findFirstSessionHistoryDate();


    @Query(
            value = "SELECT distinct(so.uuid) uuid, "
                    + "count(if(ss.new_status = :open, 1, null)) new_open, "
                    + "count(if(ss.old_status = :open, 1, null)) old_open, "
                    + "count(if(ss.new_status = :pending, 1, null)) new_pending, "
                    + "count(if(ss.old_status = :pending, 1, null)) old_pending, "
                    + "count(if(ss.new_status = :confirmed, 1, null)) new_confirmed, "
                    + "count(if(ss.old_status = :confirmed, 1, null)) old_confirmed, "
                    + "count(if(ss.new_status = :euCancelled, 1, null)) new_eucancelled, "
                    + "count(if(ss.new_status = :ptCancelled, 1, null)) new_ptcancelled, "
                    + "count(if(ss.old_status = :ptCancelled, 1, null)) old_ptcancelled, "
                    + "count(if(ss.new_status = :cancelled, 1, null)) new_cancelled, "
                    + "count(if(ss.old_status = :cancelled, 1, null)) old_cancelled, "
                    + "count(if(ss.new_status = :burned, 1, null)) new_burned, "
                    + "count(if(ss.new_status = :completed, 1, null)) new_completed, "
                    + "(select count(*) from session_session_daily_mv mv " 
                    + "	 where mv.package_uuid=sp.uuid AND "
                    + "  (mv.status= :expired OR " 
					+ "	 ((CURRENT_DATE > date(CONVERT_TZ(so.expired_date, '+00:00', :offset))) "
					+ "		AND (mv.status= :open OR "
					+ "			mv.status= :pending OR "
					+ "			mv.status= :confirmed ))) "
                    + "     AND date(CONVERT_TZ(mv.session_created_time, '+00:00', :offset)) = :date) new_expired, "
                    + "count(if(ss.new_status = :transferred, 1, null)) new_transferred, "
                    + "count(if(ss.old_status = :transferred, 1, null)) old_transferred, "
                    + "so.num_of_sessions nos, "
                    + "sum(if(ss.new_status in :revenue, if(so.free_order, -(so.price * (100 - so.commission) / so.num_of_sessions / 100), (so.price * so.commission / so.num_of_sessions / 100)), 0 )) commission, "
                    + "sum(if(ss.new_status in :revenue, (so.price * (100 - so.commission) / so.num_of_sessions / 100), 0 )) ptFee "
                    + "FROM session_session_histories ss join session_session_packages sp on ss.new_package_uuid = sp.uuid "
                    + "join session_orders so on sp.order_uuid = so.uuid "
                    + "WHERE date(CONVERT_TZ(ss.created_date, '+00:00', :offset) ) = :date "
                    + "group by so.uuid", nativeQuery = true)
    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<Object[]> getOrderWithStatusInDate(@Param("open") final String open,
            @Param("pending") final String pending, @Param("confirmed") final String confirmed,
            @Param("euCancelled") final String euCancelled,
            @Param("ptCancelled") final String ptCancelled,
            @Param("cancelled") final String cancelled, @Param("burned") final String burned,
            @Param("completed") final String completed, @Param("expired") final String expired,
            @Param("transferred") final String transferred,
            @Param("revenue") final List<String> revenueStatus,
            @Param("date") final LocalDate date, @Param("offset") final String offset);

}
