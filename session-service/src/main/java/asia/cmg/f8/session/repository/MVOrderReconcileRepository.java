package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.MVOrderReconcile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MVOrderReconcileRepository extends JpaRepository<MVOrderReconcile, Long> {

    @Query(value = "select max(rec.recDate) from MVOrderReconcile rec")
    Optional<Object> findLatestReconcileDate();

    @Query(
            value = "select rec from MVOrderReconcile rec where rec.orderUuid = ?1 and rec.recDate = ?2")
    Optional<MVOrderReconcile> findOneByOrderUuidAndRecDate(final String orderUuid,
            final LocalDate recDate);

    /**
     * Clone orders from prevDate to current Date
     * 
     * @param prevDate
     * @return
     */
    @Modifying
    @Query(
            value = "INSERT INTO session_order_reconcile_mv "
                    + "(rec_date, created_date, burned, cancelled, completed, confirmed, eu_cancelled, expired, open, order_uuid, pending, pt_cancelled, transferred, num_of_session, order_active) "
                    + "select DATE_ADD(rec_date, INTERVAL 1 DAY), CURRENT_DATE, 0, cancelled, 0, 0, 0, 0, open, order_uuid, pending, pt_cancelled, 0, num_of_session, order_active "
                    + "from session_order_reconcile_mv where rec_date = ?1",
            nativeQuery = true)
    Integer cloneDataToNextDay(final LocalDate prevDate);


    @Query(
            value = "SELECT rec.recDate, sum(rec.open) + sum(rec.pending) + sum(rec.cancelled) + sum(rec.ptcancelled), "
                    + "sum(rec.confirmed), sum(rec.eucancelled), sum(rec.burned), sum(rec.completed), "
                    + "sum(expired), sum(transferred), sum(commission), sum(ptFee) "
                    + "FROM MVOrderReconcile rec "
                   + "WHERE rec.recDate between ?1 and ?2 "
                    + "GROUP BY rec.recDate ORDER BY rec.recDate")
    List<Object[]> getActivityFinancialReport(final LocalDate startDate, final LocalDate endDate);
   
}
