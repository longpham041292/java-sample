package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.MVSessionDailyEntity;
import asia.cmg.f8.session.wrapper.dto.OrderFinancialRecord;
import asia.cmg.f8.session.wrapper.dto.SessionFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MVSessionDailyRepository extends JpaRepository<MVSessionDailyEntity, Long> {

	@Query(value = "SELECT UNIX_TIMESTAMP(MAX(sd.sessionCreatedTime)) FROM MVSessionDailyEntity sd")
	Long findMaxBySessionCreatedTime();


	@Query(name = "MVSessionDailyEntity.SessionFinancialReport", nativeQuery = true)
	List<SessionFinancial> getSessionFinancialReport(@Param("startDate") final long startDate,
			@Param("endDate") final long endDate, @Param("timeZone") final String timeZone);

	@Query(name = "MVSessionDailyEntity.FreeSessionFinancialReport", nativeQuery = true)
	List<SessionFinancial> getFreeSessionFinancialReport(@Param("startDate") final long startDate,
			@Param("endDate") final long endDate, @Param("timeZone") final String timeZone);

	@Query(name = "MVSessionDailyEntity.getOrderFinancialReport", nativeQuery = true)
    List<OrderFinancialRecord> getOrderFinancialReport(final LocalDate fromDate, final LocalDate toDate);
}
